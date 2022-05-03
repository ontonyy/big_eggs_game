package com.bigeggs.client.models.boosts;

import com.badlogic.gdx.utils.TimeUtils;
import com.bigeggs.client.models.Player;

public class InvisibleBoost extends Boost {
    public InvisibleBoost(float x, float y) {
        super(x, y, "boosts/invis.png");
    }

    /**
     * Player who collide make invisible for 5 seconds and in Player class draw InvisibleBoost message timer
     */
    @Override
    public boolean collisionWithPlayer(Player player) {
        if (abstractBox.overlaps(player.abstractBox)) {
            player.setInvisible(true);
            player.invisStart = TimeUtils.millis();
            return true;
        } else return false;
    }
}