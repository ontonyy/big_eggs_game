package com.bigeggs.server.world;

import Packets.PacketAddEnemyAI;
import Packets.PacketAddPlayer;
import Packets.PacketCreator;
import models.EnemyAI;

import java.util.*;

public class ServerWorld {
    private HashMap<Integer, String> players = new LinkedHashMap<>();
    private List<PacketAddEnemyAI> enemies = new LinkedList<>();
    private int id = 5;

    public void fillEnemiesList() {
        enemies.add(PacketCreator.createPacketEnemyAI(300f, 0f, 0f, 100, 0, ""));
        enemies.add(PacketCreator.createPacketEnemyAI(100f, 900f, 0f, 100, 1, ""));
        enemies.add(PacketCreator.createPacketEnemyAI(900f, 900f, 0f, 100, 2, ""));
        enemies.add(PacketCreator.createPacketEnemyAI(900f, 100f, 0f, 100, 3, ""));
        enemies.add(PacketCreator.createPacketEnemyAI(100f, 100f, 0f, 100, 4, ""));
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

    public void addPlayer(int id, String name) {
        players.put(id, name);
    }

    public void removeId(int id) {
        players.remove(id);
    }

    public Set<Integer> getConnectedIds() {
        return players.keySet();
    }

    public HashMap<Integer, String> getPlayers() {
        return players;
    }
}
