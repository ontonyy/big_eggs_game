package com.bigeggs.client.world;

import ClientConnection.ClientConnection;
import com.bigeggs.client.models.Bullet;
import com.bigeggs.client.models.EnemyAI;
import com.bigeggs.client.models.Player;

import java.util.*;

public class ClientWorld {
    private ClientConnection clientConnection;
    private HashMap<Integer, Player> players = new HashMap<>();
    private HashMap<Integer, EnemyAI> enemyAIList = new LinkedHashMap<>();
    private List<Bullet> bullets = new ArrayList<>();

    public HashMap<Integer, Player> getPlayers() {
        return players;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void addPlayer(int id, Player player) {
        players.put(id, player);
    }

    public void removePlayer(int id) {
        players.remove(id);
    }

    public void addEnemy(int id, EnemyAI enemyAI) {
        enemyAIList.put(id, enemyAI);
    }

    public void removeEnemy(int id) {
        enemyAIList.remove(id);
    }

    public HashMap<Integer, EnemyAI> getEnemyAIList() {
        return enemyAIList;
    }

    public Set<Integer> getEnemyAIListIds() {
        return enemyAIList.keySet();
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }
}
