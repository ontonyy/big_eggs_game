package ServerConnection;

import Packets.*;
import Packets.add.*;
import Packets.list.PacketListBoosts;
import Packets.list.PacketListEnemiesAI;
import Packets.remove.PacketRemoveAI;
import Packets.remove.PacketRemoveBoost;
import Packets.remove.PacketRemoveBullet;
import Packets.remove.PacketRemovePlayer;
import Packets.update.PacketGameStartInfo;
import Packets.update.PacketPlayerPosition;
import Packets.update.PacketUpdateEnemyAI;
import Packets.update.PacketUpdatePlayerInfo;
import com.bigeggs.server.world.ServerWorld;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import models.GameCharacter;
import models.Player;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

public class ServerConnection {
    private Server server;
    private ServerWorld serverWorld;
    static final int udpPort = 8080, tcpPort = 8090;

    /**
     * Constructor with server-client connect listening
     */
    public ServerConnection() {
        serverWorld = new ServerWorld();
        try {
            server = new Server();
            server.start();
            server.bind(tcpPort, udpPort);

        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, "Can not start the Server.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Register all packets that the same with ClientConnection that are sent over the network.
        server.getKryo().register(Player.class);
        server.getKryo().register(GameCharacter.class);
        server.getKryo().register(LinkedList.class);
        server.getKryo().register(PacketAddPlayer.class);
        server.getKryo().register(PacketAddBoost.class);
        server.getKryo().register(PacketAddEnemyAI.class);
        server.getKryo().register(PacketBullet.class);
        server.getKryo().register(PacketRemoveBullet.class);
        server.getKryo().register(PacketListEnemiesAI.class);
        server.getKryo().register(PacketListBoosts.class);
        server.getKryo().register(PacketRemovePlayer.class);
        server.getKryo().register(PacketRemoveAI.class);
        server.getKryo().register(PacketRemoveBoost.class);
        server.getKryo().register(PacketUpdateEnemyAI.class);
        server.getKryo().register(PacketUpdatePlayerInfo.class);
        server.getKryo().register(PacketGameStartInfo.class);
        server.getKryo().register(PacketCreator.class);
        server.getKryo().register(PacketPlayerPosition.class);
        server.getKryo().register(PacketAddScore.class);

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (checkGameStarting(connection)) {
                    if (object instanceof PacketAddPlayer) {
                        // Get packet if someone connects
                        PacketAddPlayer connect = (PacketAddPlayer) object;
                        sendPacketPlayerPosition(connection.getID());
                        System.out.println("Connected player: " + connect.getPlayerName());

                        // Add player on server and send to all client packet add player
                        serverWorld.addPlayer(connection.getID(), connect);
                        for (Map.Entry<Integer, PacketAddPlayer> integerPacketAddPlayerEntry : serverWorld.getPlayers().entrySet()) {
                            PacketAddPlayer addPlayer = integerPacketAddPlayerEntry.getValue();
                            addPlayer.setId(integerPacketAddPlayerEntry.getKey());
                            server.sendToAllTCP(addPlayer);
                        }

                    } else if (object instanceof PacketUpdatePlayerInfo) {
                        // Get packet to update info about player and send to all clients
                        PacketUpdatePlayerInfo playerInfo = (PacketUpdatePlayerInfo) object;
                        if (playerInfo.isDead()) {
                            clientDisconnect(playerInfo.getId());
                            server.sendToTCP(playerInfo.getId(), playerInfo);
                        } else {
                            playerInfo.setId(connection.getID());
                            server.sendToAllTCP(playerInfo);
                        }

                    } else if (object instanceof PacketUpdateEnemyAI) {
                        // Get packet to update enemy(AI) info on server and send to all client this packet
                        PacketUpdateEnemyAI enemyAI = (PacketUpdateEnemyAI) object;
                        if (serverWorld.getEnemies().size() > 0) {
                            PacketAddEnemyAI enemyAI1 = PacketCreator.createPacketEnemyAI(enemyAI.getX(), enemyAI.getY(), enemyAI.getAngle(), enemyAI.getHealth(), enemyAI.getId(), enemyAI.getFollowPlayer());
                            serverWorld.getEnemies().put(enemyAI.getId(), enemyAI1);

                            // Check if player not in list, enemy(AI) enough not follow it
                            if (!serverWorld.containsPlayer(enemyAI.getFollowPlayer()))
                                enemyAI.setFollowPlayer("");
                            server.sendToAllTCP(enemyAI);
                        }

                    } else if (object instanceof PacketBullet) {
                        PacketBullet packetBullet = (PacketBullet) object;
                        server.sendToAllUDP(packetBullet);

                    } else if (object instanceof PacketRemoveBullet) {
                        PacketRemoveBullet packetRemoveBullet = (PacketRemoveBullet) object;
                        server.sendToAllUDP(packetRemoveBullet);

                    } else if (object instanceof PacketRemoveBoost) {
                        PacketRemoveBoost removeBoost = (PacketRemoveBoost) object;
                        server.sendToAllTCP(removeBoost);
                        serverWorld.removeBoost(removeBoost.getId());

                    } else if (object instanceof PacketAddBoost) {
                        PacketAddBoost packetBoost = (PacketAddBoost) object;
                        server.sendToAllTCP(sendPacketAddBoost(packetBoost.getX(), packetBoost.getY(), packetBoost));

                    } else if (object instanceof PacketRemoveAI) {
                        PacketRemoveAI removeAI = (PacketRemoveAI) object;
                        server.sendToAllTCP(removeAI);
                        serverWorld.removeEnemyAi(removeAI.getId());

                    } else if (object instanceof PacketRemovePlayer) {
                        PacketRemovePlayer removedPlayer = (PacketRemovePlayer) object;
                        if (serverWorld.getPlayers().size() < 6 && serverWorld.getConnectedIds().contains(removedPlayer.getId())) {
                            System.out.println("Disconnected player: " + removedPlayer.getName());
                            serverWorld.removeId(removedPlayer.getId());
                            serverWorld.getScores().remove(removedPlayer.getName());
                            server.sendToAllTCP(serverWorld.addEnemyAI(removedPlayer.getX(), removedPlayer.getY()));
                        }
                    } else if (object instanceof PacketPlayerPosition) {
                        PacketPlayerPosition playerPosition = (PacketPlayerPosition) object;
                        if (playerPosition.isChange()) {
                            List<Float> pos = serverWorld.getRandomPos();
                            playerPosition.setX(pos.get(0));
                            playerPosition.setY(pos.get(1));
                            server.sendToTCP(connection.getID(), playerPosition);
                        }
                    } else if (object instanceof PacketAddScore) {
                        PacketAddScore addScore = (PacketAddScore) object;
                        if (serverWorld.getConnectedIds().contains(addScore.getId())) {
                            int score = (int) (addScore.getScore() * serverWorld.getScoreCoefficient(addScore.getKilledPlayerLives()));
                            addScore.setScore(score);
                            serverWorld.putScore(addScore.getPlayerName(), score);
                            server.sendToTCP(addScore.getId(), addScore);
                        }
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                clientDisconnect(connection.getID());
            }
        });
    }

    public boolean checkGameStarting(Connection connection) {
        if (!serverWorld.getConnectedIds().contains(connection.getID()) && (serverWorld.getPlayers().size() >= 5 || serverWorld.isStartGame())) {
            sendServerFull(connection.getID(), "Server full, wait game end!");
            return false;
        }
        if (serverWorld.isStartGame()) {
            boolean start = serverWorld.getGameStartTime() == 0;
            // Game continue 2 minutes
            if (serverWorld.checkGameEnd()) {
                System.out.println("Scores: " + serverWorld.getScores());
                System.out.println("Players amount: " + serverWorld.getPlayers().size());
                serverWorld.setStartGame(false);
                serverWorld.setGameSeconds(0);
                sendPacketWin(serverWorld.getWinner());
                serverWorld.clearScores();
                serverWorld.resetYPosition();
                serverWorld.fillPositions();
            } else {
                long seconds = serverWorld.getGameSeconds();
                sendPacketGameStart(start, String.format("TIME: %s.%s", seconds / 60, seconds % 60), serverWorld.getScoreMessage());
            }
        } else {
            if (serverWorld.canStartGame()) {
                if (serverWorld.checkReady()) {
                    serverWorld.setStartGame(true);

                    // Update boosts, enemies, scores when game is start
                    serverWorld.fillBoostsList();
                    serverWorld.fillEnemiesList();
                    serverWorld.fillScoreMap();

                    sendPacketListEnemies();
                    sendPacketListBoosts(new LinkedList<>(serverWorld.getBoosts().values()), true);

                } else {
                    sendPacketGameStart(false, "Ready steady go: " + serverWorld.getReadySeconds(), "");
                }
            } else {
                sendPacketGameStart(false, "Waiting players (" + serverWorld.getPlayers().size() + "/5)" + serverWorld.getWaitingString(), "");
            }
        }
        return true;
    }

    public PacketAddBoost sendPacketAddBoost(float x, float y, PacketAddBoost packetBoost) {
        packetBoost.setX(x);
        packetBoost.setY(y);
        packetBoost.setType(serverWorld.getRandomItemList(serverWorld.boostsTypes));
        serverWorld.addBoost(x, y, packetBoost.getType());
        return packetBoost;
    }

    /**
     * @param start boolean | if true then send initial random positions for players(clients)
     * @param message of game(timer)
     * @param scoreText of all players
     */
    public void sendPacketGameStart(boolean start, String message, String scoreText) {
        PacketGameStartInfo gameMessage = new PacketGameStartInfo();
        gameMessage.setStart(start);
        gameMessage.setMessage(message);
        gameMessage.setScoreText(scoreText);
        if (start) {
            sendPositions(gameMessage);
        } else {
            server.sendToAllTCP(gameMessage);
        }
    }

    /**
     * Send random positions for players(clients) in server
     */
    public void sendPositions(PacketGameStartInfo startInfo) {
        for (Integer connectedId : serverWorld.getConnectedIds()) {
            List<Float> position = serverWorld.getRandomStartPos();
            startInfo.setX(position.get(0));
            startInfo.setY(position.get(1));
            server.sendToTCP(connectedId, startInfo);
        }
    }

    /**
     * If server full send message about it to client
     */
    public void sendServerFull(int id, String message) {
        PacketGameStartInfo gameMessage = new PacketGameStartInfo();
        gameMessage.setStart(false);
        gameMessage.setMessage(message);
        server.sendToTCP(id, gameMessage);
    }

    public void sendPacketListBoosts(List<PacketAddBoost> boosts, boolean deleteUnused) {
        PacketListBoosts listBoosts = new PacketListBoosts();
        listBoosts.setBoostList(boosts);
        listBoosts.setRemoveUnused(deleteUnused);
        server.sendToAllTCP(listBoosts);
    }

    public void sendPacketListEnemies() {
        PacketListEnemiesAI enemiesAI = new PacketListEnemiesAI();
        enemiesAI.setEnemies(new LinkedList<>(serverWorld.getEnemies().values()));
        enemiesAI.setRemoveUnused(true);
        server.sendToAllTCP(enemiesAI);
    }

    /**
     * Check player(clients) disconnection and sending PlayerRemove packet
     */
    public void clientDisconnect(int id) {
        // Get packet to remove player, and instead add enemy
        PacketRemovePlayer removePlayer = PacketCreator.createPacketRemovePlayer(id);
        if (serverWorld.getPlayers().size() == 1) serverWorld.removeId(id);
        server.sendToAllTCP(removePlayer);
    }

    /**
     * If only 1 player in server or game time is ended send PacketWin to player winner
     */
    public void sendPacketWin(int id) {
        PacketUpdatePlayerInfo playerInfo = new PacketUpdatePlayerInfo();
        playerInfo.setId(id);
        playerInfo.setWin(true);
        server.sendToTCP(id, playerInfo);
    }

    public void sendPacketPlayerPosition(int id) {
        PacketPlayerPosition position = new PacketPlayerPosition();
        position.setX(740f);
        position.setY(serverWorld.getYPosition());
        server.sendToTCP(id, position);
    }

    public ServerWorld getServerWorld() {
        return serverWorld;
    }

    public Server getServer() {
        return server;
    }

    public static void main(String[] args) {
        new ServerConnection();
    }
}