package ClientConnection;

import Packets.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.bigeggs.client.gameInfo.GameClient;
import com.bigeggs.client.models.Bullet;
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
import java.util.Random;

public class ClientConnection {
    private Client client;
    private String playerName;
    private GameScreen gameScreen;
    private GameClient gameClient;
    private ClientWorld clientWorld;
    private int skinId;

    public ClientConnection() {
        String ip = "localhost";

        int udpPort = 8090, tcpPort = 8080;

        client = new Client();
        client.start();

        // set skin ID
        setSkinId();

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
        client.getKryo().register(PacketBullet.class);
        client.getKryo().register(PacketBoost.class);
        client.getKryo().register(PacketRemoveBoost.class);

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
                            if (addPlayer.getId() != connection.getID() && !clientWorld.getPlayers().containsKey(addPlayer.getId())) {
                                final Player player = Player.createPlayer(200f, 300f, 0f, addPlayer.getPlayerName(), "playerIcons/" + addPlayer.getSkinId() + ".png");
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
                } else if (object instanceof PacketBullet) {
                    PacketBullet packetBullet = (PacketBullet) object;
                    Bullet bullet = new Bullet(new Vector2(packetBullet.getPositionX(), packetBullet.getPositionY()),
                            new Vector2(packetBullet.getDirectionX(), packetBullet.getDirectionY()));
                    clientWorld.addBullet(bullet);
                } else if (object instanceof PacketBoost) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            PacketBoost boost = (PacketBoost) object;
                            if (!clientWorld.containsBoost(boost)) {
                                clientWorld.addBoost(boost);
                            }
                        }
                    });
                } else if (object instanceof PacketRemoveBoost) {
                    PacketRemoveBoost removeBoost = (PacketRemoveBoost) object;
                    clientWorld.removeBoost(removeBoost.getX(), removeBoost.getY());
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

    public void sendPacketRemoveBoost(float x, float y) {
        PacketRemoveBoost removeBoost = new PacketRemoveBoost();
        removeBoost.setX(x);
        removeBoost.setY(y);
        client.sendTCP(removeBoost);
    }

    public void updatePlayer(float x, float y, float angle, String direction, int health) {
        PacketUpdatePlayerInfo updatePlayerInfo = PacketCreator.createPacketUpdatePlayer(x, y, angle, direction, health, client.getID());
        client.sendTCP(updatePlayerInfo);
    }

    public void updateEnemy(float x, float y, float angle, int health, int id, String follow) {
        PacketUpdateEnemyAI addEnemyAI = PacketCreator.createPacketUpdateEnemyAI(x, y, angle, health, id, follow);
        client.sendTCP(addEnemyAI);
    }

    public void addBullet(float posX, float posY, float dirX, float dirY) {
        PacketBullet packetBullet = PacketCreator.createPacketBullet(posX, posY, dirX, dirY);
        client.sendUDP(packetBullet);
    }

    public void sendPacketConnect() {
        PacketAddPlayer packetConnect = PacketCreator.createPacketAddPlayer(playerName);
        packetConnect.setSkinId(getSkinId());
        client.sendTCP(packetConnect);
    }

    public int getSkinId() {
        return skinId;
    }

    public void setSkinId() {
        this.skinId = new Random().nextInt(5);
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

