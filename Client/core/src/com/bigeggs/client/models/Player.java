package com.bigeggs.client.models;
import ClientConnection.ClientConnection;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Player extends GameCharacter {
    private ClientConnection clientConnection;
    // Font for username
    public final BitmapFont font;
    // Font for UI
    public final BitmapFont uiFont;
    private static MapLayer map;
    public long speedStart = 0;
    public long healthDrawStart = 0;
    public boolean speedBoost = false;
    public boolean hpBoost = false;

    public Player(float x, float y, float angle, String name, MapLayer tiledMapTileLayer, String texture) {
        super(x, y, 64, angle, name, tiledMapTileLayer, texture);
        font = new BitmapFont();
        uiFont = new BitmapFont();
        uiFont.getData().scale(0.75f);
        map = tiledMapTileLayer;
    }

    @Override
    public void move() {
        super.move();
        clientConnection.updatePlayer(position.x, position.y, angle, direction, health);
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
        font.draw(batch, name, position.x - 35, position.y + 45);
    }

    @Override
    public Bullet createBullet(Vector2 mousePos) {
        Bullet bullet = super.createBullet(mousePos);
        if (bullet != null) {
            clientConnection.addBullet(bullet.position.x, bullet.position.y, bullet.direction.x, bullet.direction.y);
        }
        return bullet;
    }

    public static Player createPlayer(float x, float y, float angle, String name, String texture) {
        return new Player(x, y, angle, name, (MapLayer) map, texture) ;
    }

    public void checkSpeedTime(Batch batch) {
        if (speedStart != 0) {
            long timeEnd = TimeUtils.millis() - speedStart;
            if (timeEnd < 5000) {
                uiFont.draw(batch, "Speed boost: " + (double) (5 - timeEnd / 1000), position.x - 70, position.y + 370);
            } else {
                speedStart = 0;
                speed = 5;
            }
        }
    }

    public void actWithHealthBoost(Batch batch) {
        if (healthDrawStart != 0) {
            long timeEnd = TimeUtils.millis() - healthDrawStart;
            if (timeEnd < 1000) {
                uiFont.draw(batch, "Player health: " + health, position.x - 500, position.y + 370);
            }
        }
    }

    public void actWithBoosts(Batch batch) {
        if (speedBoost) {
            checkSpeedTime(batch);
        }
        if (hpBoost) {
            actWithHealthBoost(batch);
        }
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }
}
