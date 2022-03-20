package com.bigeggs.server.world;

import Packets.PacketAddEnemyAI;
import Packets.PacketAddPlayer;
import Packets.PacketBoost;
import Packets.PacketCreator;
import models.Bullet;
import models.EnemyAI;

import java.util.*;

public class ServerWorld {
    private Map<Integer, PacketAddPlayer> players = new LinkedHashMap<>();
    private List<PacketAddEnemyAI> enemies = new LinkedList<>();
    private List<PacketBoost> boosts = new LinkedList<>();
    private List<String> boostsTypes = Arrays.asList("speed", "hp", "ammo");
    private int id = 5;

    public void fillEnemiesList() {
        enemies.add(PacketCreator.createPacketEnemyAI(300f, 0f, 0f, 100, 0, ""));
        enemies.add(PacketCreator.createPacketEnemyAI(250f, 300f, 0f, 100, 4, ""));
        enemies.add(PacketCreator.createPacketEnemyAI(300f, 1275f, 0f, 100, 1, ""));
        enemies.add(PacketCreator.createPacketEnemyAI(1250, 1250f, 0f, 100, 2, ""));
        enemies.add(PacketCreator.createPacketEnemyAI(1300f, 300f, 0f, 100, 3, ""));
    }

    public void fillBoostsList() {
        addBoost(500f, 600f);
        addBoost(600f, 600f);
        addBoost(700f, 600f);
    }

    public String getRandomItemList(List<String> itemList) {
        Random random = new Random();
        return itemList.get(random.nextInt(itemList.size()));
    }

    public void addBoost(float x, float y) {
        boosts.add(PacketCreator.createPacketBoost(x, y, getRandomItemList(boostsTypes)));
    }

    public void removeBoost(float x, float y) {
        PacketBoost rBoost = null;
        for (PacketBoost boost : boosts) {
            if (boost.getX() == x && boost.getY() == y) {
                rBoost = boost;
            }
        }
        boosts.remove(rBoost);
    }

    public int removeEnemy() {
        int id = 0;
        if (enemies.size() > 0) {
            id = enemies.get(0).getId();
            enemies.remove(0);
        }
        return id;
    }

    public PacketAddEnemyAI addEnemyAI() {
        PacketAddEnemyAI ai = PacketCreator.createPacketEnemyAI(100f, 100f, 0f, 100, id, "");
        id++;
        enemies.add(ai);
        return ai;
    }

    public List<PacketAddEnemyAI> getEnemies() {
        return enemies;
    }

    public void addPlayer(Integer id, PacketAddPlayer addPlayer) {
        players.put(id, addPlayer);
    }

    public void removeId(int id) {
        players.remove(id);
    }

    public Set<Integer> getConnectedIds() {
        return players.keySet();
    }

    public Map<Integer, PacketAddPlayer> getPlayers() {
        return players;
    }

    public boolean containsPlayer(String playerName) {
        for (PacketAddPlayer player : players.values()) {
            if (player.getPlayerName().equals(playerName)) return true;
        }
        return false;
    }

    public List<PacketBoost> getBoosts() {
        return boosts;
    }
}
