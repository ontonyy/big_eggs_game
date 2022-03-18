package Packets;


public class PacketBullet {
    private float positionX;
    private float positionY;
    private float directionX;
    private float directionY;

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public float getDirectionX() {
        return directionX;
    }

    public float getDirectionY() {
        return directionY;
    }

    public void setPositionX(float x) {
        positionX = x;
    }

    public void setPositionY(float y) {
        positionY = y;
    }

    public void setDirectionX(float x) {
        directionX = x;
    }

    public void setDirectionY(float y) {
        directionY = y;
    }
}
