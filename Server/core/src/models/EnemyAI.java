package models;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;

public class EnemyAI extends GameCharacter {
    private static MapLayer map;
    private int id;
    private Circle shape;

    public EnemyAI(float x, float y, float angle, MapLayer collisonLayer) {
        super(x, y, 64, angle, "", collisonLayer, "player1.png");
        map = collisonLayer;
        shape = new Circle();
        shape.setPosition(x, y);
        shape.setRadius(400f);
    }

    public void move(Vector2 pos) {

        double speed;
        abstractBox = new Rectangle(position.x, position.y, 32, 32);
        if (isCollision()){ //collision handling (works bad)
            speed = 0.2;
        } else speed = 1;

        if (shape.contains(pos.x, pos.y)) {
            // for enemy to follow player
            float x = pos.x - position.x;
            float y = pos.y - position.y;
            float angle = (float) Math.atan2(y, x);
            position.x += speed * Math.cos(angle);
            position.y += speed * Math.sin(angle);
            shape.setPosition(position.x, position.y);
        }

    }

    public static EnemyAI createEnemyAI(float x, float y, float angle, int health) {
        EnemyAI enemyAI = new EnemyAI(x, y, angle, (MapLayer) map);
        enemyAI.setHealth(health);
        return enemyAI;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}