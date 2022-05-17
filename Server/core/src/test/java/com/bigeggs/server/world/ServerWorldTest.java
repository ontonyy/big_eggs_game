package com.bigeggs.server.world;

import Packets.add.PacketAddPlayer;
import com.badlogic.gdx.utils.TimeUtils;
import models.Player;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServerWorldTest {

    ServerWorld world = new ServerWorld();

    @Test
    void fillPositions() {
        assertEquals(0, world.getPositions().size());
        world.fillPositions();
        assertEquals(19, world.getPositions().size());
    }

    @Test
    void fillEnemiesList() {
        world.addPlayer(0, new PacketAddPlayer());
        world.addPlayer(1, new PacketAddPlayer());
        assertEquals(0, world.getEnemies().size());
        world.fillEnemiesList();
        assertEquals(3, world.getEnemies().size());
    }

    @Test
    void fillBoostsList() {
        // in constructor called this method
        assertEquals(26, world.getBoosts().size());
    }

    @Test
    void getRandomNumberBetween() {
        float num = world.getRandomNumberBetween(0, 10);
        assertTrue(num >= 0 && num <= 10);
    }

    @Test
    void getRandomItemList() {
        List<String> items = Arrays.asList("car", "bicycle", "trumm");
        String random = world.getRandomItemList(items);
        assertTrue(items.contains(random));
    }

    @Test
    void getRandomPos() {
        world.getRandomPos();
    }

    @Test
    void getRandomStartPos() {
        world.getRandomStartPos();
    }

    @Test
    void addBoost() {
        assertEquals(26, world.getBoosts().size());
        world.addBoost(0f, 0f);
        assertEquals(27, world.getBoosts().size());
    }

    @Test
    void testAddBoost() {
        assertEquals(26, world.getBoosts().size());
        world.addBoost(0f, 0f, "ammo");
        assertEquals(27, world.getBoosts().size());
        System.out.println(world.getBoosts());
        assertEquals("ammo", world.getBoosts().get(26).getType());
    }

    @Test
    void removeBoost() {
        assertEquals(26, world.getBoosts().size());
        world.removeBoost(0);
        world.removeBoost(1);
        world.removeBoost(2);
        assertEquals(23, world.getBoosts().size());
    }

    @Test
    void removeEnemy() {
        world.addPlayer(0, new PacketAddPlayer());
        world.addPlayer(1, new PacketAddPlayer());
        world.fillEnemiesList();
        assertEquals(3, world.getEnemies().size());
        world.removeEnemy();
        world.removeEnemy();
        assertEquals(1, world.getEnemies().size());
    }

    @Test
    void removeEnemyAi() {
        world.addPlayer(0, new PacketAddPlayer());
        world.addPlayer(1, new PacketAddPlayer());
        world.fillEnemiesList();
        assertEquals(3, world.getEnemies().size());
        world.removeEnemyAi(1);
        world.removeEnemyAi(2);
        assertEquals(1, world.getEnemies().size());
    }

    @Test
    void addEnemyAI() {
        world.addPlayer(0, new PacketAddPlayer());
        world.addPlayer(1, new PacketAddPlayer());
        world.fillEnemiesList();
        assertEquals(3, world.getEnemies().size());
        world.addEnemyAI(1000f, 1000f);
        assertEquals(4, world.getEnemies().size());
        assertEquals(1000f, world.removeEnemy().getX());


    }

    @Test
    void removeId() {
        world.addPlayer(0, new PacketAddPlayer());
        world.addPlayer(1, new PacketAddPlayer());
        assertEquals(2, world.getPlayers().size());
        world.removeId(0);
        world.removeId(1);
        assertEquals(0, world.getPlayers().size());
    }

    @Test
    void getConnectedIds() {
        world.addPlayer(50, new PacketAddPlayer());
        world.addPlayer(51, new PacketAddPlayer());
        assertEquals(2, world.getConnectedIds().size());
        assertTrue(world.getConnectedIds().contains(50));
        assertTrue(world.getConnectedIds().contains(51));
    }

    @Test
    void containsPlayer() {
        PacketAddPlayer player = new PacketAddPlayer();
        player.setPlayerName("Kirill");
        world.addPlayer(1, player);
        assertEquals(1, world.getPlayers().size());
        assertTrue(world.containsPlayer("Kirill"));
    }

    @Test
    void setGameStartTime() {
        assertEquals(0, world.getGameStartTime());
        world.setGameStartTime(100);
        assertEquals(100, world.getGameStartTime());
    }

    @Test
    void setReadyTime() {
        assertEquals(0, world.getReadyTime());
        world.setReadyTime(TimeUtils.millis() - 100);
        assertEquals(100, world.getReadyTime());
    }

    @Test
    void setStartGame() {
        assertFalse(world.isStartGame());
        world.setStartGame(true);
        assertTrue(world.isStartGame());
    }

    @Test
    void putScore() {
        PacketAddPlayer player1 = new PacketAddPlayer();
        player1.setPlayerName("Tjoma");
        PacketAddPlayer player2 = new PacketAddPlayer();
        player2.setPlayerName("Genii");
        world.addPlayer(0, player1);
        world.addPlayer(1, player2);
        assertEquals(0, world.getScores().size());
        world.fillScoreMap();
        world.putScore("Tjoma", 200);
        world.putScore("Genii", 500);

        assertEquals(2, world.getScores().size());
        assertEquals(500, world.getScores().get("Genii"));
        assertEquals(1, world.getWinner());

        world.clearScores();
        assertEquals(0, world.getScores().size());
    }

    @Test
    void setWaitingTime() {
        assertEquals(0, world.getWaitingTime());
        world.setWaitingTime(TimeUtils.millis() - 1000);
        assertEquals(1000, world.getWaitingTime());
    }

    @Test
    void canStartGame() {
        assertFalse(world.canStartGame());
        world.addPlayer(0, new PacketAddPlayer());
        world.addPlayer(1, new PacketAddPlayer());
        world.addPlayer(2, new PacketAddPlayer());
        world.addPlayer(3, new PacketAddPlayer());
        world.addPlayer(4, new PacketAddPlayer());
        assertTrue(world.canStartGame());
    }

    @Test
    void setWaitingSeconds() {
        assertEquals(0, world.getWaitingSeconds());
        world.addPlayer(0, new PacketAddPlayer());
        world.addPlayer(1, new PacketAddPlayer());
        world.addPlayer(2, new PacketAddPlayer());
        assertEquals(45, world.getWaitingSeconds());
    }

    @Test
    void checkReady() {
        assertFalse(world.checkReady());
        world.setReadyTime(TimeUtils.millis() - 6000);
        assertTrue(world.checkReady());
    }

    @Test
    void setGameSeconds() {
        assertEquals(0, world.getGameSeconds());
        world.setGameSeconds(50);
        assertEquals(50, world.getGameSeconds());
    }

    @Test
    void checkGameEnd() {
        assertTrue(world.checkGameEnd());
        world.addPlayer(0, new PacketAddPlayer());
        world.addPlayer(1, new PacketAddPlayer());
        assertFalse(world.checkGameEnd());
    }

    @Test
    void getScoreMessage() {
        PacketAddPlayer player1 = new PacketAddPlayer();
        player1.setPlayerName("Tjoma");
        PacketAddPlayer player2 = new PacketAddPlayer();
        player2.setPlayerName("Genii");
        world.addPlayer(0, player1);
        world.addPlayer(1, player2);
        world.fillScoreMap();
        assertEquals(22, world.getScoreMessage().length());
    }

    @Test
    void getIdByPlayerName() {
        PacketAddPlayer player1 = new PacketAddPlayer();
        player1.setPlayerName("Tjoma");
        PacketAddPlayer player2 = new PacketAddPlayer();
        player2.setPlayerName("Genii");
        world.addPlayer(0, player1);
        world.addPlayer(1, player2);
        assertEquals(0, world.getIdByPlayerName("Tjoma"));
        assertEquals(1, world.getIdByPlayerName("Genii"));
    }

    @Test
    void getScoreCoefficient() {
        world.setGameSeconds(160);
        assertEquals(2.2, world.getScoreCoefficient(5));
        world.setGameSeconds(100);
        assertEquals(2.0, world.getScoreCoefficient(4));

        world.addPlayer(0, new PacketAddPlayer());
        world.addPlayer(1, new PacketAddPlayer());
        assertEquals(4.0, Math.round(world.getScoreCoefficient(3)));
        world.addPlayer(2, new PacketAddPlayer());
        assertEquals(4.0, Math.round(world.getScoreCoefficient(2)));
        world.addPlayer(3, new PacketAddPlayer());
        assertEquals(3.0, Math.round(world.getScoreCoefficient(1)));
        world.addPlayer(4, new PacketAddPlayer());
        assertEquals(2.0, Math.round(world.getScoreCoefficient(1)));
    }

    @Test
    void getYPosition() {
        assertEquals(925f, world.getYPosition());
        world.getYPosition();
        world.getYPosition();
        world.getYPosition();
        assertEquals(625f, world.getYPosition());
        world.resetYPosition();
        assertEquals(925f, world.getYPosition());
    }
}