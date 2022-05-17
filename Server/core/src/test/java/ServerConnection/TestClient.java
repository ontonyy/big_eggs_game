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
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import models.Bullet;
import models.EnemyAI;
import models.GameCharacter;
import models.Player;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;

public class TestClient {
    private Client client;
    private String playerName;
    private TestClientWorld clientWorld;
    public Player player;

    /**
     * Constructor with client-server connect listening
     */
    public TestClient() {
        /*
         * To start game, should go to Putty, write this IP, add Putty Private Key
         * Start session, add repository from 'git clone https://github.com/ontonyy/big_eggs_game.git'
         * And start file 'java -jar Server.core.main.jar', and server will work
         * IP using, instead can use 'localhost' or '193.40.156.162'
         * */
        String ip = "localhost";
        player = new Player(0f, 0f, "Gerakl");

        int udpPort = 8080, tcpPort = 8090;

        client = new Client(49152, 49152);
        client.start();

        // Register all packets that are sent over the network.
        client.getKryo().register(Player.class);
        client.getKryo().register(GameCharacter.class);
        client.getKryo().register(LinkedList.class);
        client.getKryo().register(PacketAddPlayer.class);
        client.getKryo().register(PacketAddBoost.class);
        client.getKryo().register(PacketAddEnemyAI.class);
        client.getKryo().register(PacketBullet.class);
        client.getKryo().register(PacketRemoveBullet.class);
        client.getKryo().register(PacketListEnemiesAI.class);
        client.getKryo().register(PacketListBoosts.class);
        client.getKryo().register(PacketRemovePlayer.class);
        client.getKryo().register(PacketRemoveAI.class);
        client.getKryo().register(PacketRemoveBoost.class);
        client.getKryo().register(PacketUpdateEnemyAI.class);
        client.getKryo().register(PacketUpdatePlayerInfo.class);
        client.getKryo().register(PacketGameStartInfo.class);
        client.getKryo().register(PacketCreator.class);
        client.getKryo().register(PacketPlayerPosition.class);
        client.getKryo().register(PacketAddScore.class);

        client.addListener(new Listener() {
            @Override
            public void received(final Connection connection, final Object object) {
                if (object instanceof PacketAddPlayer) {
                    // Get packet to add player and create thread(because of texture) to create player
                    PacketAddPlayer addPlayer = (PacketAddPlayer) object;
                    // Check if client world not contains and packet if not equal current id
                    if (addPlayer.getId() != connection.getID() && !clientWorld.getPlayers().containsKey(addPlayer.getId())) {
                        Player player = new Player(200f, 300f, addPlayer.getPlayerName());
                        clientWorld.addPlayer(addPlayer.getId(), player);
                        clientWorld.addPlayersMessage(addPlayer.getPlayerName() + " connected!");
                        System.out.println(addPlayer.getPlayerName() + " connected!");
                    }
                } else if (object instanceof PacketUpdatePlayerInfo) {
                    // Get packet to update player if client world contains it
                    final PacketUpdatePlayerInfo playerInfo = (PacketUpdatePlayerInfo) object;
                    if (playerInfo.getId() == connection.getID()) {
                        if (playerInfo.isDead()) {
                            client.close();
                            showDialogMessage("You lose\nScore: " + player.getScore());
                        } else if (playerInfo.isWin()) {
                            showDialogMessage("You win!\nScore: " + player.getScore());
                        }
                    } else {
                        if (clientWorld.getPlayers().containsKey(playerInfo.getId())) {
                            Player newPlayer = clientWorld.getPlayers().get(playerInfo.getId());
                            newPlayer.setPosition(playerInfo.getX(), playerInfo.getY());
                            newPlayer.setAngle(playerInfo.getAngle());
                            newPlayer.setDirection(playerInfo.getDirection());
                            newPlayer.setHealth(playerInfo.getHealth());
                        }
                    }
                } else if (object instanceof PacketRemovePlayer) {
                    // Get packet to remove player
                    PacketRemovePlayer removePlayer = (PacketRemovePlayer) object;

                    Player rPlayer = clientWorld.removePlayer(removePlayer.getId());
                    if (rPlayer != null) {
                        float removedPlayerX = rPlayer.getPosition().x;
                        float removedPlayerY = rPlayer.getPosition().y;
                        sendPacketAddBoost(removedPlayerX, removedPlayerY);
                        sendPacketRemovePlayer(rPlayer.name, removePlayer, removedPlayerX, removedPlayerY);
                    }

                } else if (object instanceof PacketAddEnemyAI) {
                    // Get packet to add enemy(AI) and create thread to it
                    final PacketAddEnemyAI enemyAI = (PacketAddEnemyAI) object;
                    EnemyAI enemyBot = new EnemyAI(enemyAI.getX(), enemyAI.getY(), enemyAI.getAngle());
                    enemyBot.setId(enemyAI.getId());
                    clientWorld.addEnemy(enemyAI.getId(), enemyBot);
                } else if (object instanceof PacketUpdateEnemyAI) {
                    // Get packet to update enemy(AI) info
                    PacketUpdateEnemyAI enemyAI = (PacketUpdateEnemyAI) object;
                    if (clientWorld.getEnemyAIList().containsKey(enemyAI.getId())) {
                        EnemyAI ai = clientWorld.getEnemyAIList().get(enemyAI.getId());
                        ai.setPosition(enemyAI.getX(), enemyAI.getY());
                        ai.setAngle(enemyAI.getAngle());
                        ai.setHealth(enemyAI.getHealth());
                        ai.setFollowPlayer(enemyAI.getFollowPlayer());
                    }

                } else if (object instanceof PacketRemoveAI) {
                    // Get packet to remove enemy(AI)
                    PacketRemoveAI ai = (PacketRemoveAI) object;
                    if (clientWorld.getEnemyAIListIds().contains(ai.getId())) {
                        clientWorld.removeEnemy(ai.getId());
                    }

                } else if (object instanceof PacketBullet) {
                    PacketBullet packetBullet = (PacketBullet) object;
                    Bullet bullet = new Bullet(new Vector2(packetBullet.getPositionX(), packetBullet.getPositionY()),
                            new Vector2(packetBullet.getDirectionX(), packetBullet.getDirectionY()), packetBullet.getPlayerName());
                    bullet.objectId = packetBullet.getId();
                    clientWorld.addBullet(bullet);

                } else if (object instanceof PacketRemoveBullet) {
                    PacketRemoveBullet packetRemoveBullet = (PacketRemoveBullet) object;
                    for (Bullet b : new ArrayList<>(clientWorld.getBullets())) {
                        if (b != null) {
                            if (b.objectId == packetRemoveBullet.getId() && b.playerName.equals(packetRemoveBullet.getPlayerName())) {
                                clientWorld.getBullets().remove(b);
                            }
                        }
                    }
                } else if (object instanceof PacketAddBoost) {
                    PacketAddBoost boost = (PacketAddBoost) object;

                } else if (object instanceof PacketRemoveBoost) {
                    PacketRemoveBoost removeBoost = (PacketRemoveBoost) object;
                    clientWorld.removeBoostId(removeBoost.getId());

                } else if (object instanceof PacketListEnemiesAI) {
                    PacketListEnemiesAI enemiesAI = (PacketListEnemiesAI) object;
                    if (enemiesAI.isRemoveUnused()) {
                        clientWorld.getEnemyAIList().clear();
                    }
                    for (final PacketAddEnemyAI enemyAI : enemiesAI.getEnemies()) {
                        EnemyAI enemyBot = new EnemyAI(enemyAI.getX(), enemyAI.getY(), enemyAI.getAngle());
                        enemyBot.setId(enemyAI.getId());
                        clientWorld.addEnemy(enemyAI.getId(), enemyBot);
                    }
                } else if (object instanceof PacketListBoosts) {
                    PacketListBoosts boosts = (PacketListBoosts) object;
                    if (boosts.isRemoveUnused()) {
                        clientWorld.clearBoosts();
                    }
                } else if (object instanceof PacketGameStartInfo) {
                    PacketGameStartInfo gameMessage = (PacketGameStartInfo) object;
                    clientWorld.setGameMessage(gameMessage.getMessage());
                    if (gameMessage.getScoreText() != null) {
                        clientWorld.setScoreText(gameMessage.getScoreText(), playerName);
                    }
                    if (gameMessage.isStart()) {
                        player.setPosition(gameMessage.getX(), gameMessage.getY());
                        player.setHealth(100);
                        player.setFullLives();
                        player.getWeapon().setInitialAmmo();
                        player.emptyBoostFlags();
                        clientWorld.getPlayersMessages().clear();
                        player.nullScore();
                    } else {
                        if (gameMessage.getMessage() != null) {
                            if (gameMessage.getMessage().contains("Waiting")) {
                                player.getWeapon().setMaxDmg(0);
                                player.getWeapon().setMinDmg(0);
                                clientWorld.getBoosts().clear();
                                clientWorld.getEnemyAIList().clear();
                            } else if (gameMessage.getMessage().contains("TIME")) {
                                player.getWeapon().setMaxDmg(20);
                                player.getWeapon().setMinDmg(10);
                            } else if (gameMessage.getMessage().contains("Server")) {
                                client.close();
                                showDialogMessage("Server is full, wait game end");
                            }
                        }
                    }
                } else if (object instanceof PacketPlayerPosition) {
                    PacketPlayerPosition playerPosition = (PacketPlayerPosition) object;
                    player.setPosition(playerPosition.getX(), playerPosition.getY());
                    player.setHealth(100);
                } else if (object instanceof PacketAddScore) {
                    PacketAddScore addScore = (PacketAddScore) object;
                    player.addScore(addScore.getScore());
                    System.out.println("Score: " + player.getScore());
                }
            }
        });

        try {
            // Connected to the server - wait 5000ms before failing.
            client.connect(5000, ip, tcpPort, udpPort);
        } catch (IOException exception) {
            showDialogMessage("Can not connect to the Server");
        }
    }

    public void sendPacketRemoveBoost(int id) {
        PacketRemoveBoost removeBoost = new PacketRemoveBoost();
        removeBoost.setId(id);
        client.sendTCP(removeBoost);
    }

    public void sendPacketRemoveAI(int id) {
        PacketRemoveAI removeAI = new PacketRemoveAI();
        removeAI.setId(id);
        client.sendTCP(removeAI);
    }

    public void sendPacketAddBoost(float x, float y) {
        PacketAddBoost packetBoost = new PacketAddBoost();
        packetBoost.setX(x);
        packetBoost.setY(y);
        client.sendTCP(packetBoost);
    }

    public void updatePlayer(float x, float y, float angle, String direction, int health, int lives, boolean invisible, boolean dead) {
        PacketUpdatePlayerInfo updatePlayerInfo = PacketCreator.createPacketUpdatePlayer(x, y, angle, direction, health, lives, client.getID(), invisible, dead);
        client.sendTCP(updatePlayerInfo);
    }

    public void updateEnemy(float x, float y, float angle, int health, int id, String follow) {
        PacketUpdateEnemyAI addEnemyAI = PacketCreator.createPacketUpdateEnemyAI(x, y, angle, health, id, follow);
        client.sendTCP(addEnemyAI);
    }

    public void addBullet(float posX, float posY, float dirX, float dirY, String name, int id) {
        PacketBullet packetBullet = PacketCreator.createPacketBullet(posX, posY, dirX, dirY, name, id);
        client.sendUDP(packetBullet);
    }

    public void removeBullet(String name, int id) {
        PacketRemoveBullet packetRemoveBullet = PacketCreator.createPacketRemoveBullet(name, id);
        client.sendUDP(packetRemoveBullet);
    }

    public void sendPacketConnect() {
        PacketAddPlayer packetConnect = PacketCreator.createPacketAddPlayer(playerName);
        packetConnect.setSkinId(clientWorld.getSkinId());
        client.sendTCP(packetConnect);
    }

    public void sendPacketPlayerPosition(boolean change) {
        PacketPlayerPosition packetPlayerPosition = PacketCreator.createPacketPlayerPosition(change, 0, 0);
        client.sendTCP(packetPlayerPosition);
    }

    public void sendPacketRemovePlayer(String name, PacketRemovePlayer removePlayer, float removedPlayerX, float removedPlayerY) {
        removePlayer.setX(removedPlayerX);
        removePlayer.setY(removedPlayerY);
        removePlayer.setName(name);
        client.sendTCP(removePlayer);
    }

    public void sendPacketAddScore(int score, int id, int lives, String name) {
        PacketAddScore addScore = PacketCreator.createPacketAddScore(score, id, lives, name);
        client.sendTCP(addScore);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public TestClientWorld getClientWorld() {
        return clientWorld;
    }

    public void setClientWorld(TestClientWorld clientWorld) {
        this.clientWorld = clientWorld;
    }

    public static void main(String[] args) {
        new TestClient();
    }

    public void showDialogMessage(String message) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        final JDialog dialog = pane.createDialog(null, "BIG EGGS message for " + playerName);
        dialog.setModal(false);
        dialog.setVisible(true);

        new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        }).start();
    }
}

