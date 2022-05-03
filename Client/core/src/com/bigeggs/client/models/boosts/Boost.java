package com.bigeggs.client.models.boosts;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bigeggs.client.models.Player;

public class Boost {
    private final Vector2 position;
    private Texture picture;
    private TextureRegion pictureRegion;
    public Rectangle abstractBox;

    /**
     * Parent boost constructor for different boosts
     * @param x of boost position
     * @param y of boost position
     * @param picture of boost, for different boosts
     */
    public Boost(float x, float y, String picture) {
        this.position = new Vector2(x, y);
        this.picture = new Texture(picture);
        this.pictureRegion = new TextureRegion(this.picture);
        this.abstractBox = new Rectangle(x, y, 25, 25);
    }

    public void render(Batch batch) {
        batch.draw(pictureRegion, position.x, position.y, 25, 25);
    }

    /**
     * @param player with whom will be collision
     * @return boolean | true if collision was
     */
    public boolean collisionWithPlayer(Player player) {
        return abstractBox.overlaps(player.abstractBox);
    }

    public void dispose() {
        picture.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }
}
