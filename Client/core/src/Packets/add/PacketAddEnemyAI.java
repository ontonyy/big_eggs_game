package Packets.add;

public class PacketAddEnemyAI {
    private float x, y, angle;
    private int health, id;
    private String followPlayer;

    public String getFollowPlayer() {
        return followPlayer;
    }

    public void setFollowPlayer(String followPlayer) {
        this.followPlayer = followPlayer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}