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
    private String followPlayer = "";
    private Float previuosX;
    private Float previuosY;

    public EnemyAI(float x, float y, float angle, MapLayer collisonLayer) {
        super(x, y, 64, angle, "", collisonLayer, "botModel.png");
        map = collisonLayer;
        shape = new Circle();
        shape.setPosition(x, y);
        shape.setRadius(400f);
    }

    public void move(GameCharacter character) {
        Vector2 pos = character.getPosition();
        Vector3 anglePos = new Vector3(pos.x, pos.y, 0);

        double speed;
        abstractBox = new Rectangle(position.x, position.y, 32, 32);
        if (isCollision()){
            speed = 0;
            if (previuosY != null && previuosX != null) {
                position.x = previuosX;
                position.y = previuosY;
            }
        } else {
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

    public static EnemyAI createEnemyAI(float x, float y, float angle, int health, String follow) {
        EnemyAI enemyAI = new EnemyAI(x, y, angle, (MapLayer) map);
        enemyAI.setHealth(health);
        enemyAI.setFollowPlayer(follow);
        return enemyAI;
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
