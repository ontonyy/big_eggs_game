package models;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Player extends GameCharacter {
    // Font for username
    public final BitmapFont font;
    private static MapLayer map;

    // Boost values for time counting
    public long speedStart = 0;
    public long healthStart = 0;
    public long invisStart = 0;

    // boosts booleans for check if boost is pick uped
    public boolean speedBoost = false;
    public boolean hpBoost = false;
    public boolean invisBoost = false;
    private boolean invisible = false;

    private long time = 0;
    private int score = 0;
    private List<String> boostsMessages = new LinkedList<>(Arrays.asList("", "", ""));

    private boolean displayScore = false;

    /**
     * Basic constructor inherit by GameCharacter class
     */
    public Player(float x, float y, float angle, String name, MapLayer tiledMapTileLayer, String texture) {
        super(x, y, 64, angle, name, tiledMapTileLayer, texture);
        font = new BitmapFont();
        map = tiledMapTileLayer;
    }

    /**
     * Basic constructor for tests.
     */
    public Player(float x, float y, String name) {
        super(x, y, 0, name, null);
        font = null;
    }

    /**
     * Send PacketPlayerUpdateInfo with ClientConnection to server
     */
    @Override
    public void move() {
        super.move();
    }

    /**
     * Draw player name above player in game
     */
    @Override
    public void render(Batch batch) {
        super.render(batch);
        font.draw(batch, name, position.x - 35, position.y + 45);
    }

    /**
     * Display main player and others players scores
     * @param batch help to draw texture
     * @param font for score drawing
     */
    public void displayScore(Batch batch, BitmapFont font) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            displayScore = !displayScore;
        }
    }

    /**
     * Check conditions (check time of last created bullet, check automatic, check ammo) and create bullet
     * @param mousePos of player global mouse
     * @param name of player
     * @return Created bullet
     */
    @Override
    public Bullet createBullet(Vector2 mousePos, String name) {
        name = this.name;
        Bullet bullet = super.createBullet(mousePos, name);
        if (!this.weapon.getAutomatic() && bullet != null && weapon.getLoadedAmmo() > 0) {
            if (System.currentTimeMillis() - time >= 70) {
                time = System.currentTimeMillis();
                weapon.shoot();
            }
        } else if (this.weapon.getAutomatic() && bullet != null && weapon.getLoadedAmmo() > 0) {
            if (System.currentTimeMillis() - time >= 150) {
                time = System.currentTimeMillis();
                weapon.shoot();
            }
        }
        return bullet;
    }

    public static Player createPlayer(float x, float y, float angle, String name, String texture) {
        return new Player(x, y, angle, name, (MapLayer) map, texture) ;
    }

    /**
     * Start action if player picked up SpeedBoost, and check time of action, then end this action.
     * Add action message to special list, that display all boosts messages together
     */
    public void actWithSpeedBoost() {
        if (speedStart != 0) {
            long timeEnd = TimeUtils.millis() - speedStart;
            if (timeEnd < 5000) {
                boostsMessages.set(0, "   Speed boost: " + (double) (5 - timeEnd / 1000) + "\n");
            } else {
                speedBoost = false;
                speedStart = 0;
                speed = 5;
                boostsMessages.set(0, "");
            }
        }
    }

    /**
     * Start action if player picked up HealthBoost, and check time of action, then end this action.
     * Add action message to special list, that display all boosts messages together
     */
    public void actWithHealthBoost() {
        if (healthStart != 0) {
            long timeEnd = TimeUtils.millis() - healthStart;
            if (timeEnd < 1000) {
                boostsMessages.set(1, "   Player health: " + health + "\n");
            } else {
                hpBoost = false;
                boostsMessages.set(1, "");
            }
        }
    }

    /**
     * Check health and lives, if damaged send PacketAddScore and if player lived decreased PacketPlayerPosition
     * @param damagePlayerName player damaged this player
     */
    public void damage(String damagePlayerName) {
        if (health <= 0) {
            if (lives <= 1) {
                dead = true;
            } else {
                health = 100;
                lives--;
            }
        } else {
            health -= weapon.getMinDmg();
        }
    }

    /**
     * Start action if player picked up InvisibilityBoost, and check time of action, then end this action.
     * Add action message to special list, that display all boosts messages together
     */
    public void actWithInvisibilityBoost() {
        if (invisStart != 0) {
            long timeEnd = TimeUtils.millis() - invisStart;
            if (timeEnd < 5000) {
                boostsMessages.set(2, "Invisibility boost: " + (double) (5 - timeEnd / 1000) + "\n");
            } else {
                invisStart = 0;
                invisible = false;
                invisBoost = false;
                boostsMessages.set(2, "");
            }
        }
    }

    /**
     * Main method for action with boosts and displaying it
     * @param batch help to draw in screen
     * @param font of boosts
     */
    public void actWithBoosts(Batch batch, BitmapFont font) {
        if (speedBoost) {
            actWithSpeedBoost();
        }
        if (hpBoost) {
            actWithHealthBoost();
        }
        if (invisBoost) {
            actWithInvisibilityBoost();
        }
        int y = 220;
        for (String boostsMessage : boostsMessages) {
            if (!boostsMessage.equals("")) {
                font.draw(batch, boostsMessage, position.x + 60, position.y + y);
                y -= 50;
            }
        }
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    /**
     * Set False all boosts flags
     */
    public void emptyBoostFlags() {
        invisBoost = false;
        hpBoost = false;
        speedBoost = false;
    }

    public void setFullLives() {
        lives = 5;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int getScore() {
        return score;
    }

    public void nullScore() {
        score = 0;
    }
}
