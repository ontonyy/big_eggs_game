package com.bigeggs.client.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.*;
import com.bigeggs.client.world.ClientWorld;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class GameCharacter {
    // Main variables
    public final Vector2 position;
    public String name;
    private Texture picture;
    public float speed;
    public MapLayer collisionLayer;
    public ClientWorld world = new ClientWorld();
    public ArrayList<Bullet> bullets;
    public Weapon weapon = new Weapon();
    private final float size, halfSize;
    public float angle;
    public Rectangle abstractBox; //abstract rectangle that collides with map

    private TextureRegion pictureRegion;
    private Vector2 barrelPosition = new Vector2();
    private Vector2 directionv = new Vector2();
    private Vector2 origin = new Vector2();
    private Vector2 offset = new Vector2();
    public Vector2 barrelOffset;
    public Double boost;

    // Live and health values
    public int health = 100;
    public int lives = 5;
    public boolean dead = false;
    public String direction = "up";

    public float oldX;
    public float oldY;

    //sounds
    protected Sound step;
    //protected Sound oof;
    protected Sound shot;
    protected Sound letsGo;
    private Music music;

    protected Float TimeToNextStep = 12f;
    //protected Float TimeToNextOof = 0.5f;
    protected Float TimeToNextShot = 0.15f;
    protected Float TimeToNextAaa = 8f;

    public Float abstractX = 0f;
    public Float abstractY = 0f;
    public Float abstractWidth = 37f;
    public Float abstractHeight = 40f;
    //position.x - 15, position.y - 12, 37, 40
    public Float playerModelScale = 1f;


    /**
     * Constructor will set many variables.
     * @param x of position
     * @param y of position
     * @param size of character
     * @param angle initial of character
     * @param name of character
     * @param collisonLayer that will collide with character
     * @param textureName for character
     */
    public GameCharacter(float x, float y, float size, float angle, String name, MapLayer collisonLayer, String textureName) {
        this.position = new Vector2();
        this.position.set(x, y);
        this.name = name;
        this.size = size;
        this.angle = angle;
        this.halfSize = size / 2;
        this.picture = new Texture(textureName);
        this.pictureRegion = new TextureRegion(picture);
        this.collisionLayer = collisonLayer; //get layer with collidable objects
        barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);
        bullets = new ArrayList<>();
        Random rand = new Random();
        boost = rand.nextDouble();
        speed = 5;
        step = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/steps.mp3"));
        //oof = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/oof.mp3"));
        shot = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/shot.mp3"));
        letsGo = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/okLetsGo.mp3"));

        music = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/background.mp3"));
        music.setVolume(0.05f);
        music.setLooping(true);
        music.play();
    }

    /**
     * Constructor without sounds for test.
     */
    public GameCharacter(float x, float y, float angle, String name, MapLayer collisonLayer) {
        this.position = new Vector2();
        this.position.set(x, y);
        this.name = name;
        this.size = 32;
        this.angle = angle;
        this.halfSize = size / 2;
        this.collisionLayer = collisonLayer; //get layer with collidable objects
        barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);
        bullets = new ArrayList<>();
    }


    /**
     * Main loop method of every character, will render every delta seconds
     * @param batch help to draw texture
     */
    public void render(Batch batch) {
        if (health >= 180) {
            playerModelScale = 1.6f;
            abstractX = position.x - 24;
            abstractY = position.y - 19.2f;
            abstractWidth = 59f;
            abstractHeight = 64f;
            barrelOffset = new Vector2(83.0f, -48.0f).scl(0.5f);

        } else if (health > 140 && health < 171) {
            playerModelScale = 1.4f;
            abstractX = position.x - 21;
            abstractY = position.y - 16.8f;
            abstractWidth = 51.8f;
            abstractHeight = 56f;
            barrelOffset = new Vector2(70.0f, -40.0f).scl(0.5f);

        } else if (health > 101 && health < 141) {
            playerModelScale = 1.2f;
            abstractX = position.x - 18;
            abstractY = position.y - 14.4f;
            abstractWidth = 44.4f;
            abstractHeight = 48f;
            barrelOffset = new Vector2(60.0f, -37.0f).scl(0.5f);
        } else if (health == 100) {
            playerModelScale = 1f;
            abstractX = position.x - 15;
            abstractY = position.y - 12;
            barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);
        } else if (health < 91 && health > 70) {
            playerModelScale = 0.95f;
            abstractX = position.x - 14;
            abstractY = position.y - 13.5f;
            abstractWidth = 36f;
            abstractHeight = 39f;
            barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);
        } else if (health < 71 && health > 50) {
            playerModelScale = 0.8f;
            abstractX = position.x - 12;
            abstractY = position.y - 9.6f;
            abstractWidth = 34f;
            abstractHeight = 37f;
            barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);
        } else if (health < 51 && health > 30) {
            playerModelScale = 0.65f;
            abstractX = position.x - 9.75f;
            abstractY = position.y - 7.8f;
            abstractWidth = 33f;
            abstractHeight = 35f;
            barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);
        } else if (health < 31 && health > 10) {
            playerModelScale = 0.5f;
            abstractX = position.x - 7.5f;
            abstractY = position.y - 6;
            abstractWidth = 32f;
            abstractHeight = 33f;
            barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);
        } else if (health < 11) {
            playerModelScale = 0.35f;
            abstractX = position.x - 5.25f;
            abstractY = position.y - 4.2f;
            abstractWidth = 20f;
            abstractHeight = 20f;
            barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);
        }
        batch.draw(pictureRegion,
                position.x - halfSize,
                position.y - halfSize,
                halfSize, halfSize, size, size,
                playerModelScale, playerModelScale, angle);
        directionv = (new Vector2(1.0f, 0.0f)).rotateDeg(angle); // unit vector of the direction of the player
        origin = new Vector2(0.5f, 0.5f); // rotation origin, rotate around the center of the image. ( 0,0 would have been upper left corner)
        offset = (new Vector2(barrelOffset)).rotateDeg(angle).add(origin); // Rotated barrel offset
        barrelPosition = (new Vector2(position)).add(offset);
    }

    /**
     * Movement with collision controlling and sound reproducing
     */
    public void move() {
        if (TimeToNextStep == 12f) {
            letsGo.play(0.5f);
        }
        if (oldY != position.y || oldX != position.x) {
            if (TimeToNextStep == 1.5f) {
                step.play(2f);

            }
            TimeToNextStep -= 0.1f;
            if (TimeToNextStep <= 0f) {
                TimeToNextStep = 1.5f;
            }
        } else {
            TimeToNextStep = 1.5f;
        }
        oldX = position.x;
        oldY = position.y;
        abstractBox = new Rectangle(abstractX, abstractY, abstractWidth, abstractHeight);


        if (isAiBotCollision()) {
            boost += 1;
        } else {
            Random rand = new Random();
            boost = rand.nextDouble();
        }
        if (!isCollisionWithWall() && !isPlayerCollision() && !isAiBotCollision()) {
            if (Gdx.input.isKeyPressed(Input.Keys.A) && Gdx.input.isKeyPressed(Input.Keys.W)) {
                position.y += speed;
                position.x -= speed;
                this.direction = "up-left";
            } else if (Gdx.input.isKeyPressed(Input.Keys.A) && Gdx.input.isKeyPressed(Input.Keys.S)) {
                position.y -= speed;
                position.x -= speed;
                this.direction = "down-left";
            } else if (Gdx.input.isKeyPressed(Input.Keys.D) && Gdx.input.isKeyPressed(Input.Keys.S)) {
                position.y -= speed;
                position.x += speed;
                this.direction = "down-right";
            } else if (Gdx.input.isKeyPressed(Input.Keys.D) && Gdx.input.isKeyPressed(Input.Keys.W)) {
                position.y += speed;
                position.x += speed;
                this.direction = "up-right";
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                position.x -= speed;
                this.direction = "left";
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                position.x += speed;
                this.direction = "right";
            } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                position.y += speed;
                this.direction = "up";
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                position.y -= speed;
                this.direction = "down";
            }

        } else {

            switch (this.direction) {
                case "left":
                    position.x += 0.5 + boost;
                    break;
                case "right":
                    position.x -= 0.5 + boost;
                    break;
                case "up":
                    position.y -= 0.5 + boost;
                    break;
                case "down":
                    position.y += 0.5 + boost;
                    break;
                case "up-left":
                    position.y -= 0.5 + boost;
                    position.x += 0.5 + boost;
                    break;
                case "down-left":
                    position.y += 0.5 + boost;
                    position.x += 0.5 + boost;
                    break;
                case "up-right":
                    position.y -= 0.5 + boost;
                    position.x -= 0.5 + boost;
                    break;
                case "down-right":
                    position.y += 0.5 + boost;
                    position.x -= 0.5 + boost;
                    break;

            }

        }
        if (isCollisionWithWall()) {
            switch (this.direction) {
                case "left":
                    position.x += 0.5 + boost;
                    break;
                case "right":
                    position.x -= 0.5 + boost;
                    break;
                case "up":
                    position.y -= 0.5 + boost;
                    break;
                case "down":
                    position.y += 0.5 + boost;
                    break;
                case "up-left":
                    position.y -= 0.5 + boost;
                    position.x += 0.5 + boost;
                    break;
                case "down-left":
                    position.y += 0.5 + boost;
                    position.x += 0.5 + boost;
                    break;
                case "up-right":
                    position.y -= 0.5 + boost;
                    position.x -= 0.5 + boost;
                    break;
                case "down-right":
                    position.y += 0.5 + boost;
                    position.x -= 0.5 + boost;
                    break;

            }

        }
/*        if (isPlayerCollision() || isAiBotCollision() || isCollisionWithWall()){
            if (oldY != position.y || oldX != position.x) {
                if (TimeToNextOof == 0.5f) {
                    oof.play(0.4f);
                }
                TimeToNextOof -= 0.1f;
                if (TimeToNextOof <= 0f) {
                    TimeToNextOof = 0.5f;
                }
            } else {
                TimeToNextOof = 0.5f;
            }
        }*/

    }

    /**
     * Bullet creation by delta time controlling
     * @param mousePos of player global mouse
     * @param name of player
     * @return bullet or null
     */
    public Bullet createBullet(Vector2 mousePos, String name) {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && weapon.getLoadedAmmo() > 0) {
            if (TimeToNextShot == 0.25f) {
                shot.play(0.6f);

            }
            TimeToNextShot -= 0.1f;
            if (TimeToNextShot <= 0f) {
                TimeToNextShot = 0.25f;
            }

            TimeToNextAaa -= 0.1f;
            if (TimeToNextAaa <= 0f) {
                TimeToNextAaa = 12f;
            }
            return new Bullet(barrelPosition, mousePos, name);
        }
        TimeToNextShot = 0.25f;
        TimeToNextAaa = 12f;
        return null;
    }

    /**
     * Collision controlling with wall
     * @return boolean | true if collide with wall
     */
    public boolean isCollisionWithWall() {
        MapObjects objects = collisionLayer.getObjects(); //get objects
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) { //get rectangle objects
            Rectangle rectangle = rectangleObject.getRectangle();
            if (rectangle.overlaps(abstractBox)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Weapon fire mode changing if pressed E
     */
    public void changeFireMode() {
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            weapon.setAutomatic();
        }
    }

    /**
     * Weapon reload if pressed R
     */
    public void reload() {
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            weapon.reload();
        }
    }

    /**
     * Collision with others players in server
     * @return boolean | true if collide other players in server
     */
    public boolean isPlayerCollision() {
        Map<Integer, Player> players = world.getPlayers();
        for (Player player : players.values()) {
            Rectangle enemyabstractBox = new Rectangle(player.getPosition().x - 12, player.getPosition().y - 8, 35, 35);
            String enemyDirection = player.getDirection();
            switch (enemyDirection) {
                case "up":
                    this.direction = "down";
                    break;
                case "down":
                    this.direction = "up";
                    break;
                case "right":
                    this.direction = "left";
                    break;
                case "up-left":
                    this.direction = "down-right";
                    break;
                case "down-left":
                    this.direction = "up-right";
                    break;
                case "up-right":
                    this.direction = "down-left";
                    break;
                case "down-right":
                    this.direction = "up-left";
                    break;
            }
            if (enemyabstractBox.overlaps(abstractBox)) {
                return true;
            }

        }
        return false;
    }

    /**
     * Collision controlling with enemyAIs in server
     * @return boolean | true if collide with some enemyAI in server
     */
    public boolean isAiBotCollision() {
        Map<Integer, EnemyAI> enemyAIList = world.getEnemyAIList();
        for (EnemyAI bot : enemyAIList.values()) {
            Rectangle enemyabstractBox = new Rectangle(bot.getPosition().x - 12, bot.getPosition().y - 8, 35, 35);
            String enemyDirection = bot.getDirection();

            if (enemyabstractBox.overlaps(abstractBox)) {
                bot.speed = 0;
                bot.followPlayer = "./;[p[p";
                return true;
            }
        }
        return false;

    }

    public ClientWorld getWorld() {
        return world;
    }

    public void setWorld(ClientWorld world) {
        this.world = world;
    }

    /**
     * Changing angle of character by mouse
     * @param mousePos 3D global mouse position
     */
    public void rotate(Vector3 mousePos) {
        angle = MathUtils.radiansToDegrees * MathUtils.atan2(mousePos.y - position.y, mousePos.x - position.x);
    }

    public void setTexture(String texture) {
        this.picture = new Texture(texture);
        this.pictureRegion = new TextureRegion(picture);
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public String getDirection() {
        return direction;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeapon(Weapon w) {
        this.weapon = w;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public Vector2 getPosition() {
        return position;
    }

    public MapLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(MapLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }

    /**
     * Disposing pictures and sounds
     */
    public void dispose() {
        picture.dispose();
        step.dispose();
        //oof.dispose();
        letsGo.dispose();
        shot.dispose();
        music.dispose();
    }
}
