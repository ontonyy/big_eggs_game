package com.bigeggs.client.world;

import ClientConnection.ClientConnection;
import Packets.add.PacketAddBoost;
import com.badlogic.gdx.utils.TimeUtils;
import com.bigeggs.client.models.Bullet;
import com.bigeggs.client.models.EnemyAI;
import com.bigeggs.client.models.Player;
import com.bigeggs.client.models.boosts.*;

import java.util.*;

/**
 * Class that hold all variables from server (players, enemies, boosts, messages)
 */
public class ClientWorld {
    private ClientConnection clientConnection;
    private Map<Integer, Player> players = new LinkedHashMap<>();
    private Map<String, Integer> playersNames = new LinkedHashMap<>();
    private Map<Integer, EnemyAI> enemyAIList = new LinkedHashMap<>();
    private Map<Integer, Boost> boosts = new LinkedHashMap<>();
    private List<Bullet> bullets = new ArrayList<>();
    private String gameMessage, scoreText = "";
    private Map<String, Long> playersMessages = new LinkedHashMap<>();
    private List<Integer> removeBoostIds = new ArrayList<>();
    private int skinId;

    public ClientWorld() {
        setSkinId();
    }

    /**
     * @param boost that will be added
     */
    public void addBoost(PacketAddBoost boost) {
        switch (boost.getType()) {
            case "speed":
                boosts.put(boost.getId(), new SpeedBoost(boost.getX(), boost.getY()));
                break;
            case "hp":
                boosts.put(boost.getId(), new HealthBoost(boost.getX(), boost.getY()));
                break;
            case "ammo":
                boosts.put(boost.getId(), new AmmoBoost(boost.getX(), boost.getY()));
                break;
            case "invis":
                boosts.put(boost.getId(), new InvisibleBoost(boost.getX(), boost.getY()));
                break;
        }
    }

    public void removeBoostId(int id) {
        removeBoostIds.add(id);
    }

    public void removeBoostIdList() {
        boosts.keySet().removeAll(removeBoostIds);
    }

    public void removeBoost(int id) {
        boosts.remove(id);
    }

    public boolean containsBoost(PacketAddBoost boost) {
        return boosts.containsKey(boost.getId());
    }

    public Map<Integer, Boost> getBoosts() {
        return boosts;
    }

    public Map<Integer, Player> getPlayers() {
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
        playersNames.put(player.name, id);
    }

    public Player removePlayer(int id) {
        playersNames.values().remove(id);
        return players.remove(id);
    }

    public void addEnemy(int id, EnemyAI enemyAI) {
        enemyAIList.put(id, enemyAI);
    }

    public void removeEnemy(int id) {
        enemyAIList.remove(id);
    }

    public Map<Integer, EnemyAI> getEnemyAIList() {
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

    public String getGameMessage() {
        return gameMessage;
    }

    public void setGameMessage(String gameMessage) {
        this.gameMessage = gameMessage;
    }

    public void clearBoosts() {
        boosts.clear();
    }

    public void addPlayersMessage(String message) {
        playersMessages.put(message, TimeUtils.millis());
    }

    public Set<String> getPlayersMessages() {
        return playersMessages.keySet();
    }

    /**
     * Draw players that connected to server
     */
    public void checkTime() {
        List<String> removeList = new LinkedList<>();
        for (Map.Entry<String, Long> messageEntry : playersMessages.entrySet()) {
            if (TimeUtils.millis() - messageEntry.getValue() > 5000) {
                removeList.add(messageEntry.getKey());
            }
        }
        playersMessages.keySet().removeAll(removeList);
    }

    public int getPlayerId(String name) {
        if (playersNames.containsKey(name)) {
            return playersNames.get(name);
        } else {
            return -1;
        }
    }

    public String getScoreText() {
        return scoreText;
    }

    /**
     * Set special view of scoreText
     * @param scoreText all scores of players
     * @param playerName of main player
     */
    public void setScoreText(String scoreText, String playerName) {
        String[] splitted = scoreText.split("\n");
        String newText = "";
        for (String s : splitted) {
            String check = s.split(" ")[0];
            if (!check.equals(playerName)) {
                newText += s + "\n";
            }
        }
        this.scoreText = newText;
    }

    public List<Integer> getRemoveBoostIds() {
        return removeBoostIds;
    }

    public int getSkinId() {
        return skinId;
    }

    public void setSkinId() {
        this.skinId = new Random().nextInt(5);
    }
}