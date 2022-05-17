package models.boosts;

import models.Player;

public class AmmoBoost extends Boost {
    public AmmoBoost(float x, float y) {
        super(x, y, "boosts/ammo.png");
    }

    /**
     * Add ammo if player collide with AmmoBoost
     */
    @Override
    public boolean collisionWithPlayer(Player player) {
        if (abstractBox.overlaps(player.abstractBox)) {
            player.getWeapon().addAmmo(30);
            return true;
        }
        return false;
    }
}
