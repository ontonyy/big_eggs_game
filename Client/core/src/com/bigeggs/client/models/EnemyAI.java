package com.bigeggs.client.models;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.CircleShape;

public class EnemyAI extends GameCharacter {
    private static MapLayer map;
    private int id;
    private Circle shape;
    public String followPlayer = "";
    private Float previuosX;
    private Float previuosY;
    private long time = 0;

    /**
     *
     * @param x of enemy position
     * @param y of enemy position
     * @param angle rotation of enemy
     * @param collisonLayer of main map with enemy
     */
    public EnemyAI(float x, float y, float angle, MapLayer collisonLayer) {
        super(x, y, 64, angle, "", collisonLayer, "botModel.png");
        map = collisonLayer;
        shape = new Circle();
        shape.setPosition(x, y);
        shape.setRadius(400f);
        this.weapon = new EnemyAIWeapon();
    }
    /**
     * Constructor for tests.
     */
    public EnemyAI(float x, float y, float angle) {
        super(x, y, angle, "", null);
        shape = new Circle();
        shape.setPosition(x, y);
        shape.setRadius(400f);
    }

    /**
     * Enemy moving, check collision with wall, and player noticing in circle and then following
     * @param character (Player) for whom enemy follow
     */
    public void move(GameCharacter character) {
        Vector2 pos = character.getPosition();
        Vector3 anglePos = new Vector3(pos.x, pos.y, 0);

        double speed;
        abstractBox = new Rectangle(position.x, position.y, 32, 32);
        // abstract box - area that interacts with other objects
        if (isCollisionWithWall()){
            speed = 0;
            if (previuosY != null && previuosX != null) {
                // made for avoiding a nullPointerException
                position.x = previuosX;
                position.y = previuosY;
            }
        } else {
            // if collides with wall, send to previous position aka do not allow go through wall
            speed = 1;
            previuosX = position.x;
            previuosY = position.y;
        }

        if (followPlayer.equals("") || followPlayer.equals(character.name)) {
            if (shape.contains(pos.x, pos.y)) {
                // for enemy to follow player
                float x = pos.x - position.x;
                float y = pos.y - position.y;
                float angle = (float) Math.atan2(y, x);
                position.x += speed * Math.cos(angle);
                position.y += speed * Math.sin(angle);
                shape.setPosition(position.x, position.y);
                rotate(anglePos);
                followPlayer = character.name;
            } else {
                followPlayer = "";
            }
        }

    }

    /**
     * Simple enemy creation with constructor and setters
     * @return EnemyAI
     */
    public static EnemyAI createEnemyAI(float x, float y, float angle, int health, String follow) {
        EnemyAI enemyAI = new EnemyAI(x, y, angle, (MapLayer) map);
        enemyAI.setHealth(health);
        enemyAI.setFollowPlayer(follow);
        return enemyAI;
    }

    public boolean isCollisionWithBullet(Rectangle r) {
        return this.abstractBox.overlaps(r);
    }

    public Bullet shoot(Player player, String name, long time) {
        if (!followPlayer.equals("") && System.currentTimeMillis() - time >= 400) {
            Bullet bullet = new Bullet(getBarrelPosition(), new Vector2(player.getPosition().x - position.x, player.getPosition().y - position.y).nor(), name);
            return bullet;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFollowPlayer() {
        return followPlayer;
    }

    public void setFollowPlayer(String followPlayer) {
        this.followPlayer = followPlayer;
    }
}
