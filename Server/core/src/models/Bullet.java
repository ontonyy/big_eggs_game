package models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    public Vector2 position = new Vector2();
    public Vector2 direction = new Vector2();
    public float speed = 350;
    public Texture texture;

    public Bullet(Vector2 position, Vector2 direction) {
        this.position.set(position);
        this.direction.set(direction);
        if (texture == null) {
            texture = new Texture("bullet.png");
        }
    }

    public void update(float delta) {
        position.add(direction.x * delta * speed, direction.y * delta * speed);
    }

    public void render(Batch batch) {
        batch.draw(texture, position.x, position.y);
    }
}
