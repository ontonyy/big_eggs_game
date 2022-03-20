package ServerConnection;

import Packets.*;
import com.bigeggs.server.world.ServerWorld;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import models.GameCharacter;
import models.Player;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

public class ServerConnection {
    private Server server;
    private ServerWorld serverWorld = new ServerWorld();
    static final int udpPort = 8090, tcpPort = 8080;

    public ServerConnection() {
        try {
            server = new Server();
            server.start();
            server.bind(tcpPort, udpPort);

        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, "Can not start the Server.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create 5 enemies(Bots AI) on server
        serverWorld.fillEnemiesList();

        // Create boosts on server
        serverWorld.fillBoostsList();

        // Register all packets that the same with ClientConnection that are sent over the network.
        server.getKryo().register(PacketAddPlayer.class);
        server.getKryo().register(PacketRemovePlayer.class);
        server.getKryo().register(PacketAddEnemyAI.class);
        server.getKryo().register(PacketRemoveAI.class);
        server.getKryo().register(PacketUpdateEnemyAI.class);
        server.getKryo().register(PacketCreator.class);
        server.getKryo().register(PacketUpdatePlayerInfo.class);
        server.getKryo().register(Player.class);
        server.getKryo().register(GameCharacter.class);
        server.getKryo().register(PacketBullet.class);
        server.getKryo().register(PacketBoost.class);
        server.getKryo().register(PacketRemoveBoost.class);

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketAddPlayer) {
                    // Get packet if someone connects
                    PacketAddPlayer connect = (PacketAddPlayer) object;
                    System.out.println("Connected player: " + connect.getPlayerName());

                    // Remove 1 AI if player connect and send this info to all clients
                    PacketRemoveAI removeAI = new PacketRemoveAI();
                    removeAI.setId(serverWorld.removeEnemy());

                    // Send to clients enemies(AI) packets
                    for (PacketAddEnemyAI enemy : serverWorld.getEnemies()) {
                        server.sendToAllTCP(enemy);
                    }
                    server.sendToAllTCP(removeAI);

                    // Send to clients boosts
                    for (PacketBoost boost : serverWorld.getBoosts()) {
                        server.sendToAllTCP(boost);
                    }

                    // Add player on server and send to all client packet add player
                    serverWorld.addPlayer(connection.getID(), connect);
                    for (Map.Entry<Integer, PacketAddPlayer> integerPacketAddPlayerEntry : serverWorld.getPlayers().entrySet()) {
                        PacketAddPlayer addPlayer = integerPacketAddPlayerEntry.getValue();
                        addPlayer.setId(integerPacketAddPlayerEntry.getKey());
                        server.sendToAllTCP(addPlayer);
                    }

                    System.out.println(serverWorld.getConnectedIds());

                } else if (object instanceof PacketUpdatePlayerInfo) {
                    // Get packet to update info about player and send to all clients
                    PacketUpdatePlayerInfo playerInfo = (PacketUpdatePlayerInfo) object;
                    playerInfo.setId(connection.getID());
                    server.sendToAllTCP(playerInfo);

                } else if (object instanceof PacketUpdateEnemyAI) {
                    // Get packet to update enemy(AI) info on server and send to all client this packet
                    PacketUpdateEnemyAI enemyAI = (PacketUpdateEnemyAI) object;
                    for (PacketAddEnemyAI enemy : serverWorld.getEnemies()) {
                        if (enemyAI.getId() == enemy.getId()) {
                            enemy.setX(enemyAI.getX());
                            enemy.setY(enemyAI.getY());
                            enemy.setAngle(enemyAI.getAngle());
                            enemy.setFollowPlayer(enemyAI.getFollowPlayer());
                            enemy.setHealth(enemyAI.getHealth());
                        }

                        // Create packet with updated info about enemy(AI)
                        PacketUpdateEnemyAI ai = PacketCreator.createPacketUpdateEnemyAI(
                                enemyAI.getX(), enemyAI.getY(), enemyAI.getAngle(), enemyAI.getHealth(), enemyAI.getId(), enemyAI.getFollowPlayer());

                        // Check if player not in list, enemy(AI) enough not follow it
                        if (!serverWorld.containsPlayer(enemyAI.getFollowPlayer()))
                            ai.setFollowPlayer("");
                        server.sendToAllTCP(ai);
                    }
                } else if (object instanceof PacketBullet) {
                    PacketBullet packetBullet = (PacketBullet) object;
                    server.sendToAllUDP(packetBullet);
                } else if (object instanceof  PacketRemoveBoost) {
                    PacketRemoveBoost removeBoost = (PacketRemoveBoost) object;
                    server.sendToAllTCP(removeBoost);
                    serverWorld.removeBoost(removeBoost.getX(), removeBoost.getY());
                }
            }

            @Override
            public void disconnected(Connection connection) {
                // Get packet to remove player, and instead add enemy
                PacketRemovePlayer removePlayer = PacketCreator.createPacketRemovePlayer(connection.getID());
                serverWorld.removeId(connection.getID());
                System.out.println("Player disconnected: " + connection.getID());

                server.sendToAllTCP(serverWorld.addEnemyAI());
                server.sendToAllTCP(removePlayer);
            }
        });
    }

    public static void main(String[] args) {
        new ServerConnection();
    }
}
