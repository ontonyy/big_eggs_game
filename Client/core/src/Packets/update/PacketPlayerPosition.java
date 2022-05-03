package Packets.update;

/**
 * Packet for send new player position, if player was dead and lives more than 0
 */
public class PacketPlayerPosition {
    private boolean change;
    private float x, y;

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
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
}