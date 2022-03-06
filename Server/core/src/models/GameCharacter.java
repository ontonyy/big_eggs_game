package models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.*;

import java.util.ArrayList;


public class GameCharacter {
    public final Vector2 position;
    private final Texture picture;
    private final TextureRegion pictureRegion;
    public final Vector2 dir;
    public final Vector2 origin;
    public final Vector2 offset;
    public final Vector2 barrelPosition;
    public final Vector2 barrelOffset;

    public String name;
    public int health = 100;
    private final float size, halfSize;
    public float angle;
    public String direction = "";
    public Weapon weapon = new Weapon();

    public float oldX;
    public float oldY;

    public boolean flagLeft = false;
    public boolean flagRight = false;
    public boolean flagUp = false;
    public boolean flagDown = false;
    public MapLayer collisionLayer;

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
        dir = (new Vector2(1.0f, 0.0f)).rotateDeg(angle);
        origin = new Vector2(0.5f, 0.5f); // rotation origin, rotate around the center of the image. ( 0,0 would have been upper left corner)
        barrelOffset = new Vector2(1.0f, -0.5f).scl(0.5f);
        offset = (new Vector2(barrelOffset)).rotateDeg(angle).add(origin); // Rotated barrel offset
        barrelPosition = (new Vector2(position)).add(offset);
        bullets = new ArrayList<>();
    }


    public void render(Batch batch) {
        batch.draw(pictureRegion,
                position.x - halfSize,
                position.y - halfSize,
                halfSize, halfSize, size, size,
                1, 1, angle);
    }

    public void move() {
        oldX = position.x;
        oldY = position.y;
        abstractBox = new Rectangle(position.x, position.y, 32, 32);

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= 5;
            this.direction = "left";
            flagLeft = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += 5;
            this.direction = "right";
            flagRight = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += 5;
            this.direction = "up";
            flagUp = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            position.y -= 5;
            this.direction = "down";
            flagDown = true;


        }
        if (isCollision()){ //collision handling (works bad)
            if (flagLeft){
                flagLeft = false;
                position.x += 7;
            }else if (flagRight){
                flagRight = false;
                position.x -= 7;
            } else if (flagUp){
                flagUp = false;
                position.y -= 7;
            } else if (flagDown){
                flagDown = false;
                position.y += 7;
            }
        }
        //System.out.println(isCollision());

    }

    public void shoot() {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            bullets.add(new Bullet(position, dir));
        }
        for(Bullet bullet : bullets) {
            bullet.update(delta);
        }
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

    public void rotate(Vector3 mousePos) {
        angle = MathUtils.radiansToDegrees * MathUtils.atan2(mousePos.y - position.y, mousePos.x - position.x);
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

    public void setHealth(int health) {
        this.health = health;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeapon(Weapon w) {
        this.weapon = w;
    }

    public Vector2 getPosition() {
        return position;
    }


    public void dispose() {
        picture.dispose();
    }
}
