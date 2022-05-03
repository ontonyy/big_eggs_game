package com.bigeggs.client.models.boosts;

import com.badlogic.gdx.utils.TimeUtils;
import com.bigeggs.client.models.Player;

public class HealthBoost extends Boost {

    public HealthBoost(float x, float y) {
        super(x, y, "boosts/hp.png");
    }

    /**
     * Add health for player who collide and in Player class drawing boost message;
     */
    @Override
    public boolean collisionWithPlayer(Player player) {
        if (abstractBox.overlaps(player.abstractBox)) {
            player.health += 30;
            player.healthStart = TimeUtils.millis();
            return true;
        } else return false;
    }
}
