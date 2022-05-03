package ClientConnection;

import Packets.PacketCreator;
import Packets.add.PacketAddEnemyAI;
import Packets.add.PacketAddPlayer;
import com.bigeggs.client.gameInfo.GameClient;
import com.bigeggs.client.models.EnemyAI;
import com.bigeggs.client.models.Player;
import com.bigeggs.client.world.ClientWorld;
import com.esotericsoftware.kryonet.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientConnectionTest {
    TestServer server;
    ClientConnection connection;
    ClientWorld world;

    @BeforeEach
    public void initialize() {
        server = new TestServer();
        connection = new ClientConnection();
        world = new ClientWorld();
        connection.setClientWorld(world);
    }

    @AfterEach
    public void closeServer() {
        server.server.close();
    }

    @Test
    void testBulletsCommunication() throws InterruptedException {
        connection.setPlayerName("Maxim");
        connection.sendPacketConnect();
        Thread.sleep(1000);
        assertEquals(0, connection.getClientWorld().getBullets().size());
        connection.addBullet(50f, 50f, 50f, 50f, "Maxim", 0);
        connection.addBullet(50f, 50f, 50f, 50f, "Maxim", 1);
        connection.addBullet(50f, 50f, 50f, 50f, "Maxim", 2);
        connection.addBullet(50f, 50f, 50f, 50f, "Maxim", 3);
        connection.addBullet(50f, 50f, 50f, 50f, "Maxim", 4);
        Thread.sleep(1000);
        assertEquals(5, connection.getClientWorld().getBullets().size());
        connection.removeBullet("Maxim", 0);
        connection.removeBullet("Maxim", 1);
        connection.removeBullet("Maxim", 2);
        Thread.sleep(1000);
        assertEquals(2, connection.getClientWorld().getBullets().size());
    }

    @Test
    void sendPacketRemoveBoost() throws InterruptedException {
        assertEquals(26, server.getServerWorld().getBoosts().size());
        connection.sendPacketRemoveBoost(0);
        connection.sendPacketRemoveBoost(1);
        connection.sendPacketRemoveBoost(2);
        Thread.sleep(1000);
        assertEquals(23, server.getServerWorld().getBoosts().size());
    }

    @Test
    void sendPacketRemoveAI() throws InterruptedException {
        connection.setPlayerName("Vasja");
        connection.sendPacketConnect();
        Thread.sleep(1000);
        server.getServerWorld().fillEnemiesList();
        connection.getClientWorld().addEnemy(1, new EnemyAI(0f, 0f, 0f));
        connection.getClientWorld().addEnemy(2, new EnemyAI(0f, 0f, 0f));
        assertEquals(4, server.getServerWorld().getEnemies().size());
        assertEquals(2, connection.getClientWorld().getEnemyAIList().size());
        connection.sendPacketRemoveAI(1);
        connection.sendPacketRemoveAI(2);
        connection.sendPacketRemoveAI(3);
        Thread.sleep(1000);
        assertEquals(1, server.getServerWorld().getEnemies().size());
        assertEquals(0, connection.getClientWorld().getEnemyAIList().size());
    }

    @Test
    void updatePlayer() throws InterruptedException {
        ClientConnection connection2 = new ClientConnection();
        ClientWorld world2 = new ClientWorld();
        connection2.setClientWorld(world2);
        connection2.setPlayerName("Maria");
        connection2.sendPacketConnect();

        connection.setPlayerName("Antonio");
        connection.sendPacketConnect();
        Player player = new Player(200f, 300f, "Gena");
        Player player1 = new Player(200f, 300f, "Artur");
        connection.getClientWorld().getPlayers().put(1, player);
        connection.getClientWorld().getPlayers().put(2, player1);
        assertEquals(2, connection.getClientWorld().getPlayers().size());
        assertEquals(100, player1.health);
        Thread.sleep(1000);
        connection.updatePlayer(50f, 50f, 50f, "left", 2000, 5, false, true);
        connection2.updatePlayer(50f, 50f, 50f, "left", 2000, 5, false, false);
        Thread.sleep(1000);
        assertEquals(1, server.getServerWorld().getPlayers().size()); // one player dead and remove from game
        // 2 connection and 1 player is updated from server
        Player player2 = connection.getClientWorld().getPlayers().get(2);
        assertEquals(player1, player2);
        assertEquals(2000, player2.health);
    }

    @Test
    void testShowMessageDialog() {
        connection.showDialogMessage("Maxina is here");
    }

    @Test
    void updateEnemy() throws InterruptedException {
        connection.setPlayerName("Vasja");
        connection.sendPacketConnect();
        Thread.sleep(1000);
        server.getServerWorld().fillEnemiesList();
        connection.getClientWorld().addEnemy(1, new EnemyAI(0f, 0f, 0f));
        Thread.sleep(1000);
        PacketAddEnemyAI serverEnemyAI = server.getServerWorld().getEnemies().get(1);
        EnemyAI clientEnemyAI = connection.getClientWorld().getEnemyAIList().get(1);
        assertEquals("", serverEnemyAI.getFollowPlayer());
        assertEquals(100, serverEnemyAI.getHealth());
        assertEquals(100, clientEnemyAI.health);
        assertEquals(4, server.getServerWorld().getEnemies().size());
        connection.updateEnemy(50f, 50f, 50f, 2000, 1, "Vasja");
        Thread.sleep(1000);
        PacketAddEnemyAI serverEnemyAI2 = server.getServerWorld().getEnemies().get(1);
        EnemyAI clientEnemyAI2 = connection.getClientWorld().getEnemyAIList().get(1);

        assertEquals("Vasja", serverEnemyAI2.getFollowPlayer());
        assertEquals("Vasja", clientEnemyAI2.getFollowPlayer());
    }

    @Test
    void sendPacketConnect() throws InterruptedException {
        connection.setPlayerName("Vasja");
        connection.sendPacketConnect();
        Thread.sleep(1000);
        System.out.println(server.getServerWorld().getPlayers());
        PacketAddPlayer player = server.getServerWorld().getPlayers().get(server.getServerWorld().getPlayers().keySet().toArray()[0]);
        assertEquals("Vasja", player.getPlayerName());
        assertEquals(1, server.getServerWorld().getPlayers().size());
    }

    @Test
    void sendPacketRemovePlayer() throws InterruptedException {
        connection.setPlayerName("Antonio");
        connection.sendPacketConnect();
        Player player = new Player(200f, 300f, "Gena");
        connection.getClientWorld().getPlayers().put(0, player);
        assertEquals(1, connection.getClientWorld().getPlayers().size());

        connection.sendPacketRemovePlayer("Gena", PacketCreator.createPacketRemovePlayer(0), 50f, 50f);
        server.clientDisconnect(0);
        Thread.sleep(1000);

        assertEquals(0, connection.getClientWorld().getPlayers().size());
    }

    @Test
    void sendPacketAddScore() throws InterruptedException {
        connection.setPlayerName("Kirill");
        connection.sendPacketConnect();
        System.out.println(connection.getGameScreen());
        Thread.sleep(1000);
        server.getServerWorld().fillScoreMap();
        System.out.println(server.getServerWorld().getScores());
        assertEquals(0, server.getServerWorld().getScores().get("Kirill"));
        connection.sendPacketAddScore(100, connection.getClient().getID(), 5, "Kirill");
        Thread.sleep(1000);
        assertEquals(220, server.getServerWorld().getScores().get("Kirill"));

        connection.sendPacketAddScore(100, connection.getClient().getID(), 4, "Kirill");
        Thread.sleep(1000);
        assertEquals(480, server.getServerWorld().getScores().get("Kirill"));

        connection.sendPacketAddScore(100, connection.getClient().getID(), 3, "Kirill");
        Thread.sleep(1000);
        assertEquals(780, server.getServerWorld().getScores().get("Kirill"));

        connection.sendPacketAddScore(100, connection.getClient().getID(), 2, "Kirill");
        Thread.sleep(1000);
        assertEquals(1120, server.getServerWorld().getScores().get("Kirill"));
    }

    @Test
    void getGameScreen() {
        assertNull(connection.getGameScreen());
    }

    @Test
    void setGameScreen() {
        assertNull(connection.getGameScreen());
    }

    @Test
    void getGameClient() {
        assertNull(connection.getGameClient());
    }

    @Test
    void setGameClient() {
        connection.setGameClient(new GameClient());
        assertNotNull(connection.getGameClient());
    }

    @Test
    void getPlayerName() {
        connection.setPlayerName("Vasja");
        assertEquals("Vasja", connection.getPlayerName());
    }

    @Test
    void setPlayerName() {
        connection.setPlayerName("Vasja");
        assertEquals("Vasja", connection.getPlayerName());
    }

    @Test
    void getClient() {
        assertNotNull(connection.getClient());
    }

    @Test
    void setClient() {
        connection.setClient(new Client());
        assertNotNull(connection.getClient());
    }

    @Test
    void getClientWorld() {
        assertNotNull(connection.getClientWorld());
    }

    @Test
    void setClientWorld() {
        assertNotNull(connection.getClientWorld());
    }

    @Test
    void testSendPacketPlayerPosition() throws InterruptedException {
        connection.setPlayerName("Kilka");
        connection.sendPacketConnect();
        Thread.sleep(1000);
        connection.sendPacketPlayerPosition(true);
    }
}