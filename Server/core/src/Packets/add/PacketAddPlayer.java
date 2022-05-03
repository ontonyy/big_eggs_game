package Packets.add;

public class PacketAddPlayer {
    private String playerName;
    private int id, skinId;

    public int getSkinId() {
        return skinId;
    }

    public void setSkinId(int randomSkin) {
        this.skinId = randomSkin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
