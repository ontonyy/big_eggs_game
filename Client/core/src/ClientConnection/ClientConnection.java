package ClientConnection;

import Packets.*;
import com.badlogic.gdx.Gdx;
import com.bigeggs.client.gameInfo.GameClient;
import com.bigeggs.client.models.EnemyAI;
import com.bigeggs.client.models.GameCharacter;
import com.bigeggs.client.models.Player;
import com.bigeggs.client.screens.GameScreen;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.bigeggs.client.world.ClientWorld;

import javax.swing.*;
import java.io.IOException;

public class ClientConnection {
    private Client client;
    private String playerName;
    private GameScreen gameScreen;
    private GameClient gameClient;
    private ClientWorld clientWorld;

    public ClientConnection() {
        String ip = "localhost";

        int udpPort = 54777, tcpPort = 54555;

        client = new Client();
        client.start();

        // Register all packets that are sent over the network.
        client.getKryo().register(PacketAddPlayer.class);
        client.getKryo().register(PacketRemovePlayer.class);
        client.getKryo().register(PacketAddEnemyAI.class);
        client.getKryo().register(PacketRemoveAI.class);
        client.getKryo().register(PacketUpdateEnemyAI.class);
        client.getKryo().register(PacketCreator.class);
        client.getKryo().register(PacketUpdatePlayerInfo.class);
        client.getKryo().register(Player.class);
        client.getKryo().register(GameCharacter.class);

        client.addListener(new Listener() {
            @Override
            public void received(final Connection connection, final Object object) {
                if (object instanceof PacketAddPlayer) {
                    // Get packet to add player and create thread(because of texture) to create player
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            PacketAddPlayer addPlayer = (PacketAddPlayer) object;

                            // Check if client world not contains and packet if not equal current id
                            if (addPlayer.getId() != connection.getID() || clientWorld.getPlayers().containsKey(addPlayer.getId())) {
                                final Player player = Player.createPlayer(200f, 300f, 0f, addPlayer.getPlayerName());
                                clientWorld.addPlayer(addPlayer.getId(), player);
                            }
                            System.out.println(addPlayer.getPlayerName() + " connected!");

                        }
                    });
                } else if (object instanceof PacketUpdatePlayerInfo) {
                    // Get packet to update player if client world contains it
                    final PacketUpdatePlayerInfo playerInfo = (PacketUpdatePlayerInfo) object;
                    if (clientWorld.getPlayers().containsKey(playerInfo.getId())) {
                        Player newPlayer = clientWorld.getPlayers().get(playerInfo.getId());
                        newPlayer.setPosition(playerInfo.getX(), playerInfo.getY());
                        newPlayer.setAngle(playerInfo.getAngle());
                        newPlayer.setDirection(playerInfo.getDirection());
                        newPlayer.setHealth(playerInfo.getHealth());
                    }

                } else if (object instanceof PacketRemovePlayer) {
                    // Get packet to remove player
                    PacketRemovePlayer removePlayer = (PacketRemovePlayer) object;
                    System.out.println("Player disconnected: " + connection.getID());
                    clientWorld.removePlayer(removePlayer.getId());

                } else if (object instanceof PacketAddEnemyAI) {
                    // Get packet to add enemy(AI) and create thread to it
                    final PacketAddEnemyAI enemyAI = (PacketAddEnemyAI) object;
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            final EnemyAI enemyBot = EnemyAI.createEnemyAI(enemyAI.getX(), enemyAI.getY(), enemyAI.getAngle(), enemyAI.getHealth(), enemyAI.getFollowPlayer());
                            enemyBot.setId(enemyAI.getId());
                            clientWorld.addEnemy(enemyAI.getId(), enemyBot);
                        }
                    });

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
                }
            }
        });

        try {
            // Connected to the server - wait 5000ms before failing.
            client.connect(5000, ip, tcpPort, udpPort);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, "Can not connect to the Server.");
        }
    }

    public void updatePlayer(float x, float y, float angle, String direction, int health) {
        PacketUpdatePlayerInfo updatePlayerInfo = PacketCreator.createPacketUpdatePlayer(x, y, angle, direction, health, client.getID());
        client.sendTCP(updatePlayerInfo);
    }

    public void updateEnemy(float x, float y, float angle, int health, int id, String follow) {
        PacketUpdateEnemyAI addEnemyAI = PacketCreator.createPacketUpdateEnemyAI(x, y, angle, health, id, follow);
        client.sendTCP(addEnemyAI);
    }

    public void sendPacketConnect() {
        PacketAddPlayer packetConnect = PacketCreator.createPacketAddPlayer(playerName);
        client.sendTCP(packetConnect);
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
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

    public ClientWorld getClientWorld() {
        return clientWorld;
    }

    public void setClientWorld(ClientWorld clientWorld) {
        this.clientWorld = clientWorld;
    }

    public static void main(String[] args) {
        new ClientConnection();
    }
}

