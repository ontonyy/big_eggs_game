package com.bigeggs.server.world;

import Packets.*;
import Packets.add.PacketAddBoost;
import Packets.add.PacketAddEnemyAI;
import Packets.add.PacketAddPlayer;
import com.badlogic.gdx.utils.TimeUtils;

import java.nio.file.Paths;
import java.util.*;

/**
 * Class that hold all variables from clients in server (players, enemies, boosts, messages)
 */
public class ServerWorld {
    private Map<Integer, PacketAddPlayer> players = new LinkedHashMap<>();
    private Map<Integer, PacketAddEnemyAI> enemies = new LinkedHashMap<>();
    private Map<Integer, PacketAddBoost> boosts = new LinkedHashMap<>();
    private Map<String, Integer> scores = new LinkedHashMap<>();
    public List<String> boostsTypes = Arrays.asList("speed", "hp", "ammo", "invis");
    private List<List<Float>> positions = new LinkedList<>();
    private List<List<Float>> startPositions = new LinkedList<>();
    private int aiId = 0;
    private int boostID = 0;
    private float startPosition = 1000f;

    // Time variables of waiting, ready and game
    private long gameStartTime, readyTime, waitingTime = 0;
    private long waitingSeconds, readySeconds, gameSeconds = 0;
    private int tempSeconds = 0;
    private boolean startGame = false;

    public ServerWorld() {
        fillBoostsList();
    }

    /**
     * Fill positions for players and enemies that will be taken randomly from that list
     */
    public void fillPositions() {
        positions.clear();
        startPositions.clear();
        startPositions.add(Arrays.asList(85f, 775f)); startPositions.add(Arrays.asList(615f, 1530f));
        startPositions.add(Arrays.asList(1475f, 1055f)); startPositions.add(Arrays.asList(305f, 80f));
        startPositions.add(Arrays.asList(1400f, 135f));

        positions.add(Arrays.asList(1520f, 710f));
        positions.add(Arrays.asList(970f, 70f)); positions.add(Arrays.asList(740f, 730f));
        positions.add(Arrays.asList(60f, 475f)); positions.add(Arrays.asList(350f, 1360f));

        positions.add(Arrays.asList(550f, 550f)); positions.add(Arrays.asList(550f, 920f));
        positions.add(Arrays.asList(920f, 920f)); positions.add(Arrays.asList(920f, 550f));
        positions.add(Arrays.asList(1250f, 1230f)); positions.add(Arrays.asList(160f, 1150f));
        positions.add(Arrays.asList(980f, 330f)); positions.add(Arrays.asList(920f, 1150f));
        positions.add(Arrays.asList(450f, 330f)); positions.add(Arrays.asList(1075f, 670f));

        positions.add(Arrays.asList(1525f, 250f)); positions.add(Arrays.asList(1095f, 1525f));
        positions.add(Arrays.asList(1450f, 1485f)); positions.add(Arrays.asList(165f, 1500f));
    }

    /**
     * Fill enemies list according to player amount
     */
    public void fillEnemiesList() {
        enemies.clear();
        if (players.size() < 5 && players.size() > 1) {
            int index = aiId + 1;
            for (int i = 0; i < 5 - players.size(); i++) {
                int ind = index + i;
                List<Float> pos = getRandomPos();
                float x = pos.get(0);
                float y = pos.get(1);
                enemies.put(ind, PacketCreator.createPacketEnemyAI(x, y, getRandomNumberBetween(0, 360), 100, ind, ""));
            }
        }
    }

    /**
     * Fill boosts list on server if game start
     */
    public void fillBoostsList() {
        boosts.clear();
        addBoost(470f, 1045f);
        addBoost(470f, 405f);
        addBoost(980f, 405f);
        addBoost(980f, 1045f);
        addBoost(1045f, 655f);

        addBoost(1090f, 100f);
        addBoost(1110f, 1390f);
        addBoost(1400f, 1205f);
        addBoost(40f, 1220f);
        addBoost(105f, 1530f);
        addBoost(355f, 1350f);
        // Generate boosts randomly on hatch
        addBoost(getRandomNumberBetween(580, 880), getRandomNumberBetween(580, 880));
        addBoost(getRandomNumberBetween(580, 880), getRandomNumberBetween(580, 880));
        addBoost(getRandomNumberBetween(580, 880), getRandomNumberBetween(580, 880));
        addBoost(getRandomNumberBetween(580, 880), getRandomNumberBetween(580, 880));

        addBoost(getRandomNumberBetween(710, 760), getRandomNumberBetween(290, 350));
        addBoost(getRandomNumberBetween(550, 600), getRandomNumberBetween(60, 120));
        addBoost(getRandomNumberBetween(130, 180), getRandomNumberBetween(130, 190));
        addBoost(getRandomNumberBetween(200, 250), getRandomNumberBetween(510, 570));
        addBoost(getRandomNumberBetween(1510, 1560), getRandomNumberBetween(125, 185));
        addBoost(getRandomNumberBetween(1510, 1560), getRandomNumberBetween(410, 470));
        addBoost(getRandomNumberBetween(1415, 1465), getRandomNumberBetween(870, 930));
        addBoost(getRandomNumberBetween(1030, 1080), getRandomNumberBetween(1160, 1220));
        addBoost(getRandomNumberBetween(1515, 1565), getRandomNumberBetween(1440, 1500));
        addBoost(getRandomNumberBetween(740, 790), getRandomNumberBetween(1405, 1465));
        addBoost(getRandomNumberBetween(100, 150), getRandomNumberBetween(830, 890));

    }

    public float getRandomNumberBetween(int start, int end) {
        Random rand = new Random();
        return rand.nextInt(end - start) + start;
    }

    public String getRandomItemList(List<String> itemList) {
        Random random = new Random();
        return itemList.get(random.nextInt(itemList.size()));
    }

    public List<Float> getRandomPos() {
        Random random = new Random();
        if (positions.size() < 1) {
            fillPositions();
        }
        return positions.remove(random.nextInt(positions.size()));
    }

    public List<Float> getRandomStartPos() {
        Random random = new Random();
        if (startPositions.size() < 1) {
            fillPositions();
        }
        return startPositions.remove(random.nextInt(startPositions.size()));
    }

    public List<List<Float>> getPositions() {
        return positions;
    }

    public void addBoost(float x, float y) {
        boosts.put(boostID, PacketCreator.createPacketAddBoost(boostID, x, y, getRandomItemList(boostsTypes)));
        boostID++;
    }

    public void addBoost(float x, float y, String type) {
        boosts.put(boostID, PacketCreator.createPacketAddBoost(boostID, x, y, type));
    }

    public void removeBoost(int id) {
        boosts.remove(id);
    }

    public PacketAddEnemyAI removeEnemy() {
        return enemies.remove(enemies.keySet().toArray()[enemies.size() - 1]);
    }

    public void removeEnemyAi(int id) {
        enemies.remove(id);
    }

    /**
     * Add enemy to list and increase id of enemy
     */
    public PacketAddEnemyAI addEnemyAI(float x, float y) {
        float randAngle = getRandomNumberBetween(0, 360);
        PacketAddEnemyAI ai = PacketCreator.createPacketEnemyAI(x, y, randAngle, 100, aiId, "");
        enemies.put(aiId, ai);
        aiId++;
        return ai;
    }

    public Map<Integer, PacketAddEnemyAI> getEnemies() {
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

    /**
     * Check player containing on server by name
     */
    public boolean containsPlayer(String playerName) {
        for (PacketAddPlayer player : players.values()) {
            if (player.getPlayerName().equals(playerName)) return true;
        }
        return false;
    }

    public Map<Integer, PacketAddBoost> getBoosts() {
        return boosts;
    }

    public void setGameStartTime(int gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public long getGameStartTime() {
        return gameStartTime;
    }

    /**
     * @return ReadyTime according to current time
     */
    public long getReadyTime() {
        if (readyTime == 0) {
            readyTime = TimeUtils.millis();
        }
        return TimeUtils.millis() - readyTime;
    }

    /**
     * @return GameTime according to current time
     */
    public long getGameStart() {
        if (gameStartTime == 0) {
            gameStartTime = TimeUtils.millis();
        }
        return TimeUtils.millis() - gameStartTime;
    }

    /**
     * @return WaitingTime according to current time
     */
    public long getWaitingTime() {
        if (waitingTime == 0) {
            waitingTime = TimeUtils.millis();
        }
        return TimeUtils.millis() - waitingTime;
    }

    public void setReadyTime(long readyTime) {
        this.readyTime = readyTime;
    }

    public boolean isStartGame() {
        return startGame;
    }

    public void setStartGame(boolean startGame) {
        this.startGame = startGame;
    }

    public void putScore(String name, int score) {
        scores.put(name, scores.get(name) + score);
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void setWaitingTime(long waitingTime) {
        this.waitingTime = waitingTime;
    }

    /**
     * @return boolean | true if players is more than 1 and waiting and ready time is ended
     */
    public boolean canStartGame() {
        setGameStartTime(0);
        if (players.size() > 1) {
            return getWaitingSeconds() < 1 || players.size() >= 5;
        } else {
            setWaitingSeconds(0);
            setWaitingTime(0);
        }
        return false;
    }

    /**
     * @return WaitingTime according to player amount and current time
     */
    public long getWaitingSeconds() {
        switch (players.size()) {
            case 2:
                if (tempSeconds != 60000) {
                    tempSeconds = 60000;
                    setWaitingTime(0);
                }
                break;
            case 3:
                if (tempSeconds != 45000) {
                    tempSeconds = 45000;
                    setWaitingTime(0);
                }
                break;
            case 4:
                if (tempSeconds != 30000) {
                    tempSeconds = 30000;
                    setWaitingTime(0);
                }
                break;
        }
        waitingSeconds = (tempSeconds - getWaitingTime()) / 1000;
        return waitingSeconds;
    }

    public String getWaitingString() {
        if (waitingSeconds != 0) {
            return ": " + waitingSeconds;
        }
        return "";
    }

    public void setWaitingSeconds(long waitingSeconds) {
        this.waitingSeconds = waitingSeconds;
    }

    /**
     * @return boolean | true if ready time is ended
     */
    public boolean checkReady() {
        readySeconds = (6000 - getReadyTime()) / 1000;
        if (readySeconds < 1) {
            setReadyTime(0);
            setWaitingSeconds(0);
            setWaitingTime(0);
            return true;
        }
        return false;
    }

    public long getReadySeconds() {
        return readySeconds;
    }

    public long getGameSeconds() {
        return gameSeconds;
    }

    public void setGameSeconds(long gameSeconds) {
        this.gameSeconds = gameSeconds;
    }

    /**
     * @return boolean | true if game time is ended
     */
    public boolean checkGameEnd() {
        if (players.size() <= 1 || gameSeconds < 0) {
            return true;
        } else {
            setGameSeconds((180000 - getGameStart()) / 1000);
            return false;
        }
    }

    /**
     * @return Score message of all players
     */
    public String getScoreMessage() {
        String text = "";
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            text += entry.getKey() + " -> " + entry.getValue() + "\n";
        }
        return text;
    }

    /**
     * Fill score map according to players names
     */
    public void fillScoreMap() {
        for (PacketAddPlayer value : players.values()) {
            scores.put(value.getPlayerName(), 0);
        }
    }

    /**
     * @param name of player
     * @return id of player by name
     */
    public int getIdByPlayerName(String name) {
        int id = -1;
        for (Map.Entry<Integer, PacketAddPlayer> entry : players.entrySet()) {
            if (entry.getValue().getPlayerName().equalsIgnoreCase(name)) {
                id = entry.getKey();
            }
        }
        return id;
    }

    /**
     * Find winner in game by score map or if only one player in game return it
     * @return Player with higher score in game
     */
    public int getWinner() {
        String name = "";
        int maxScore = 0;
        int id = (int) players.keySet().toArray()[0];
        if (scores.size() > 1) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                if (entry.getValue() > maxScore) {
                    maxScore = entry.getValue();
                    name = entry.getKey();
                }
            }
            id = getIdByPlayerName(name);
        }
        System.out.println("Winner: " + id + " -> " + name);
        return id;
    }

    public void clearScores() {
        scores.clear();
    }

    /**
     * Compute coefficient by killed player lives and time of game
     * @param lives of killed player
     * @return coefficient that will be multiplied with score
     */
    public double getScoreCoefficient(int lives) {
        double coeff = 1.2;
        long seconds = getGameSeconds();
        if (seconds > 150 || seconds < 30) coeff = 1.8;
        else if (seconds > 120 || seconds < 60) coeff = 1.5;

        if (lives == 5) coeff += 0.4;
        else if (lives == 4) coeff += 0.8;
        else if (lives == 3) coeff += 1.2;
        else if (lives == 2) coeff += 1.6;
        else if (lives == 1) coeff += 2;

        int playersSize = players.size();

        if (playersSize == 5) coeff *= 0.7;
        else if (playersSize == 4) coeff *= 1;
        else if (playersSize == 3) coeff *= 1.3;
        else if (playersSize == 2) coeff *= 1.6;

        return coeff;
    }

    public float getYPosition() {
        if (startPosition < 625 || startPosition > 1150) {
            startPosition = 1000;
        }
        startPosition -= 75f;
        return startPosition;
    }

    public void resetYPosition() {
        startPosition = 1000f;
    }
}
