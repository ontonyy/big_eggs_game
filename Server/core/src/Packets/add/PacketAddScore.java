package Packets.add;

public class PacketAddScore {
    private int score, id, killedPlayerLives;
    private String playerName;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKilledPlayerLives() {
        return killedPlayerLives;
    }

    public void setKilledPlayerLives(int killedPlayerLives) {
        this.killedPlayerLives = killedPlayerLives;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}