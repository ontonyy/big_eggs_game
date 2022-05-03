package com.bigeggs.client.models.boosts;

import com.badlogic.gdx.utils.TimeUtils;
import com.bigeggs.client.models.Player;

public class SpeedBoost extends Boost {
    public SpeedBoost(float x, float y) {
        super(x, y, "boosts/speed.png");
    }

    /**
     * Accelerate player for 5 seconds, and in Player class draw message timer
     */
    @Override
    public boolean collisionWithPlayer(Player player) {
        if (abstractBox.overlaps(player.abstractBox)) {
            player.speed = 10;
            player.speedStart = TimeUtils.millis();
            return true;
        } else return false;
    }
}
