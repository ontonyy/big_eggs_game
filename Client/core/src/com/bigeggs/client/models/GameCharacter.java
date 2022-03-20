package com.bigeggs.client.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.*;
import com.bigeggs.client.world.ClientWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class GameCharacter {
    public final Vector2 position;
    private Texture picture;
    private TextureRegion pictureRegion;
    private Vector2 barrelPosition = new Vector2();
    private Vector2 directionv = new Vector2();
    private Vector2 origin = new Vector2();
    private Vector2 offset = new Vector2();
    public final Vector2 barrelOffset;
    public float speed;
    public Double boost;

    public String name;
    public int health = 100;
    private final float size, halfSize;
    public float angle;
    public String direction = "up";
    public Weapon weapon = new Weapon();

    public float oldX;
    public float oldY;

    public MapLayer collisionLayer;
    public ClientWorld world = new ClientWorld();



    public ArrayList<Bullet> bullets;

    public Rectangle abstractBox; //abstract rectangle that collides with map

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
    }


    public void render(Batch batch) {
        batch.draw(pictureRegion,
                position.x - halfSize,
                position.y - halfSize,
                halfSize, halfSize, size, size,
                1, 1, angle);
        directionv = (new Vector2(1.0f, 0.0f)).rotateDeg(angle); // unit vector of the direction of the player
        origin = new Vector2(0.5f, 0.5f); // rotation origin, rotate around the center of the image. ( 0,0 would have been upper left corner)
        offset = (new Vector2(barrelOffset)).rotateDeg(angle).add(origin); // Rotated barrel offset
        barrelPosition = (new Vector2(position)).add(offset);
    }

    public void move() {
        oldX = position.x;
        oldY = position.y;
        abstractBox = new Rectangle(position.x -12, position.y - 8, 35, 35);
        if (!isCollision() && !isPlayerCollision()) {
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
            }else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                position.x += speed;
                this.direction = "right";
            }else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                position.y += speed;
                this.direction = "up";
            }else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                position.y -= speed;
                this.direction = "down";
            }
        }
        else{
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

    }

    public Bullet createBullet(Vector2 mousePos) {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            return new Bullet(barrelPosition, mousePos);
        }
        return null;
    }

    public boolean isCollision() {
        MapObjects objects = collisionLayer.getObjects(); //get objects
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) { //get rectangle objects
            Rectangle rectangle = rectangleObject.getRectangle();
            if (rectangle.overlaps(abstractBox)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerCollision(){
        HashMap<Integer, Player> players = world.getPlayers();
        for (Player player : players.values()) {
            Rectangle enemyabstractBox = new Rectangle(player.getPosition().x - 12, player.getPosition().y - 8, 35, 35);
            String enemyDirection = player.getDirection();
            switch (enemyDirection){
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


    public ClientWorld getWorld() {
        return world;
    }

    public void setWorld(ClientWorld world) {
        this.world = world;
    }

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

    public void dispose() {
        picture.dispose();
    }
}

