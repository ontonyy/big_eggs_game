package com.bigeggs.client.models;


import com.badlogic.gdx.math.Vector2;

public class Bullet {
    public Vector2 position = new Vector2();
    public Vector2 direction = new Vector2();
    public float speed = 700;
    private static int counter = 0;
    public int objectId;
    public final String playerName;

    public Bullet (Vector2 position, Vector2 direction, String playerName) {
        this.position.set(position);
        this.direction.set(direction);
        this.objectId = counter++;
        this.playerName = playerName;
    }

    public void update(float delta) {
        position.add(direction.x * delta * speed, direction.y * delta * speed);
    }
}
