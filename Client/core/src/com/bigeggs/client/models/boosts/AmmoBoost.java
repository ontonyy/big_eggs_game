package com.bigeggs.client.models.boosts;

import com.badlogic.gdx.utils.TimeUtils;
import com.bigeggs.client.models.Player;

public class AmmoBoost extends Boost {
    public AmmoBoost(float x, float y) {
        super(x, y, "boosts/ammo.png");
    }

    @Override
    public boolean collisionWithPlayer(Player player) {
        if (abstractBox.overlaps(player.abstractBox)) {
            player.getWeapon().addAmmo(30);
            player.ammoStart = TimeUtils.millis();
            return true;
        } else return false;
    }
}
