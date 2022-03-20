package com.bigeggs.client.world;

import ClientConnection.ClientConnection;
import Packets.PacketBoost;
import com.bigeggs.client.models.Bullet;
import com.bigeggs.client.models.EnemyAI;
import com.bigeggs.client.models.Player;
import com.bigeggs.client.models.boosts.AmmoBoost;
import com.bigeggs.client.models.boosts.Boost;
import com.bigeggs.client.models.boosts.HealthBoost;
import com.bigeggs.client.models.boosts.SpeedBoost;

import java.util.*;

public class ClientWorld {
    private ClientConnection clientConnection;
    private HashMap<Integer, Player> players = new HashMap<>();
    private HashMap<Integer, EnemyAI> enemyAIList = new LinkedHashMap<>();
    private List<Boost> boosts = new LinkedList<>();
    private List<Bullet> bullets = new ArrayList<>();

    public void addBoost(PacketBoost boost) {
        switch (boost.getType()) {
            case "speed":
                boosts.add(new SpeedBoost(boost.getX(), boost.getY()));
                break;
            case "hp":
                boosts.add(new HealthBoost(boost.getX(), boost.getY()));
                break;
            case "ammo":
                boosts.add(new AmmoBoost(boost.getX(), boost.getY()));
                break;
        }
    }

    public void removeBoost(float x, float y) {
        Boost rBoost = null;
        for (Boost boost : boosts) {
            if (boost.getPosition().x == x && boost.getPosition().y == y) {
                rBoost = boost;
            }
        }
        boosts.remove(rBoost);
    }

    public boolean containsBoost(PacketBoost boost) {
        for (Boost boost1 : boosts) {
            if (boost1.getPosition().x == boost.getX() && boost1.getPosition().y == boost.getY()) {
                return true;
            }
        }
        return false;
    }

    public List<Boost> getBoosts() {
        return boosts;
    }

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
