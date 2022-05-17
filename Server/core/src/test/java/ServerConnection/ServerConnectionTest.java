package ServerConnection;

import Packets.add.PacketAddBoost;
import Packets.add.PacketAddPlayer;
import Packets.update.PacketGameStartInfo;
import models.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class ServerConnectionTest {
    TestClient client1;
    TestClient client2;
    TestClient client3;
    TestClient client4;
    TestClient client5;
    ServerConnection server;

    @BeforeEach
    void initialize() throws InterruptedException {
        server = new ServerConnection();
        client1 = new TestClient();
        client2 = new TestClient();
        client3 = new TestClient();
        client4 = new TestClient();
        client5 = new TestClient();
        client1.setClientWorld(new TestClientWorld());
        client2.setClientWorld(new TestClientWorld());
        client3.setClientWorld(new TestClientWorld());
        client4.setClientWorld(new TestClientWorld());
        client5.setClientWorld(new TestClientWorld());
        client1.setPlayerName("Artjom");
        client2.setPlayerName("Edgar");
        client3.setPlayerName("Vanja");
        client4.setPlayerName("Misha");
        client5.setPlayerName("Slava");
        client1.sendPacketConnect();
        client2.sendPacketConnect();
        Thread.sleep(1000);
    }

    @AfterEach
    void closeConnections () {
        client1.getClient().close();
        client2.getClient().close();
        client3.getClient().close();
        client4.getClient().close();
        client5.getClient().close();
        server.getServer().close();
    }

    /**
     * Update player info through server, and it sends to clients
     * @throws InterruptedException
     */
    @Test
    void updatePlayers() throws InterruptedException {
        client3.sendPacketConnect();
        Thread.sleep(1000);

        assertEquals(3, server.getServerWorld().getPlayers().size());
        assertEquals(100, client1.player.health);
        assertEquals(100, client2.player.health);
        assertEquals(100, client3.player.health);

        // Send one player that dead, and one player with changed info
        client1.updatePlayer(50f, 50f, 50f, "left", 1000, 5, false, true);
        client2.updatePlayer(50f, 50f, 50f, "left", 10, 5, false, false);
        client3.updatePlayer(50f, 50f, 50f, "left", 50000, 5, false, false);
        Thread.sleep(1000);

        assertEquals(2, server.getServerWorld().getPlayers().size());
        assertEquals(50000, client2.getClientWorld().getPlayers().get(3).health);
        assertEquals(10, client3.getClientWorld().getPlayers().get(2).health);
    }

    @Test
    void updateEnemies() throws InterruptedException {
        server.getServerWorld().fillEnemiesList();
        server.sendPacketListEnemies();
        Thread.sleep(1000);

        assertEquals(3, server.getServerWorld().getEnemies().size());
        assertEquals(100, server.getServerWorld().getEnemies().get(1).getHealth());

        client1.updateEnemy(50f, 50f, 50f, 10000, 1, "Kalla");
        client1.sendPacketRemoveAI(2);
        Thread.sleep(1000);

        assertEquals(2, server.getServerWorld().getEnemies().size());
        assertEquals(10000, server.getServerWorld().getEnemies().get(1).getHealth());
    }

    @Test
    void changePlayerPosition() throws InterruptedException {
        assertEquals(740f, client1.player.position.x);
        client1.sendPacketPlayerPosition(true);
        Thread.sleep(1000);
        assertNotEquals(740f, client1.player.position.x);
    }

    @Test
    void actionBullets() throws InterruptedException {
        assertEquals(0, client2.getClientWorld().getBullets().size());

        client1.addBullet(0f, 0f, 0f, 0f, "Artjom", 1);
        client1.addBullet(0f, 0f, 0f, 0f, "Artjom", 2);
        client1.addBullet(0f, 0f, 0f, 0f, "Artjom", 3);
        Thread.sleep(1000);

        assertEquals(3, client2.getClientWorld().getBullets().size());

        client1.removeBullet("Artjom", 2);
        client1.removeBullet("Artjom", 3);
        Thread.sleep(1000);
        assertEquals(1, client2.getClientWorld().getBullets().size());
    }

    @Test
    void addScore() throws InterruptedException {
        server.getServerWorld().fillScoreMap();
        assertEquals(0, client1.player.getScore());

        client1.sendPacketAddScore(100, 1, 5, "Artjom");
        Thread.sleep(1000);
        assertEquals(352, client1.player.getScore());
    }

    @Test
    void checkGameStarting() throws InterruptedException {
        client3.sendPacketConnect();
        client4.sendPacketConnect();
        client5.sendPacketConnect();
        Thread.sleep(1000);
        server.getServerWorld().setStartGame(false);
        server.checkGameStarting(client1.getClient());

        server.getServerWorld().setReadyTime(6000);
        server.checkGameStarting(client1.getClient());

        assertTrue(server.getServerWorld().isStartGame());

        server.getServerWorld().setGameSeconds(-1); // end game and reset needed values
        server.checkGameStarting(client1.getClient());
        assertEquals(925f, server.getServerWorld().getYPosition());
        assertEquals(0, server.getServerWorld().getScores().size());
        assertFalse(server.getServerWorld().isStartGame());
    }

    @Test
    void sendPacketGameStart() throws InterruptedException {
        assertEquals(740f, client1.player.position.x);
        assertEquals(740f, client2.player.position.x);
        server.sendPacketGameStart(true, "Game is start", server.getServerWorld().getScoreMessage());
        Thread.sleep(1000);
        // position will be chosen randomly, but not equal start position
        assertNotEquals(740f, client1.player.position.x);
        assertNotEquals(740f, client2.player.position.x);
    }

    @Test
    void sendServerFull() throws InterruptedException {
        assertEquals(2, server.getServerWorld().getPlayers().size());
        server.sendServerFull(1, "Server is full, cannot connect");
        Thread.sleep(1000);
        assertEquals(1, server.getServerWorld().getPlayers().size());
    }

    @Test
    void sendPacketListBoosts() {
        server.sendPacketListBoosts(new LinkedList<PacketAddBoost>(), true);
        assertEquals(0, client1.getClientWorld().getBoosts().size());
    }

    /**
     * When game start, if not all players connect(5) server will send enemies, if 4 players, will send 1 enemyAI,
     * that was always 5 players in game start.
     * @throws InterruptedException
     */
    @Test
    void sendPacketListEnemies() throws InterruptedException {
        assertEquals(0, client1.getClientWorld().getEnemyAIList().size());
        assertEquals(0, client2.getClientWorld().getEnemyAIList().size());
        server.getServerWorld().fillEnemiesList();
        server.sendPacketListEnemies();
        Thread.sleep(1000);
        System.out.println(client2.getClientWorld().getEnemyAIList());
        assertEquals(3, client1.getClientWorld().getEnemyAIList().size());
        assertEquals(3, client2.getClientWorld().getEnemyAIList().size());
    }

    @Test
    void clientDisconnect() throws InterruptedException {
        assertEquals(1, client2.getClientWorld().getPlayers().size());
        assertEquals(2, server.getServerWorld().getPlayers().size());

        assertEquals("Artjom", server.getServerWorld().getPlayers().get(1).getPlayerName());
        assertEquals("Edgar", server.getServerWorld().getPlayers().get(2).getPlayerName());

        server.clientDisconnect(1);
        Thread.sleep(1000);

        assertEquals(0, client2.getClientWorld().getPlayers().size());
        assertEquals(1, server.getServerWorld().getPlayers().size());

        assertNull(server.getServerWorld().getPlayers().get(1));
        assertEquals("Edgar", server.getServerWorld().getPlayers().get(2).getPlayerName());
    }

    @Test
    void sendPacketWin() throws InterruptedException {
        server.sendPacketWin(1); // will display on client dialog window with message about win
    }

    @Test
    void sendPacketPlayerPosition() {
        assertEquals(740f, client1.player.position.x);
        assertEquals(925f, client1.player.position.y);
        assertEquals(850f, client2.player.position.y);
    }

    @Test
    void playersConnect() throws InterruptedException {
        assertEquals(2, server.getServerWorld().getPlayers().size());
        assertEquals("Artjom", server.getServerWorld().getPlayers().get(1).getPlayerName());
        assertEquals("Edgar", server.getServerWorld().getPlayers().get(2).getPlayerName());

    }
}