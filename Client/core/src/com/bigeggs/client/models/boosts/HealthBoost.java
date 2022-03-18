package com.bigeggs.client.models.boosts;

import com.badlogic.gdx.utils.TimeUtils;
import com.bigeggs.client.models.Player;

public class HealthBoost extends Boost {

    public HealthBoost(float x, float y) {
        super(x, y, "boosts/hp.png");
    }

    @Override
    public boolean collisionWithPlayer(Player player) {
        if (abstractBox.overlaps(player.abstractBox)) {
            player.health += 30;
            player.healthDrawStart = TimeUtils.millis();
            return true;
        } else return false;
    }
}
