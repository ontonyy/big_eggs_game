package models;

public class Weapon {
    private String name = "M4";
    private int maxDmg = 20;
    private int minDmg = 10;
    private int loadedAmmo = 15;
    private int fullLoaded = 15;
    private int ammo = 30;

    public String getName() {
        return name;
    }

    public int getMaxDmg() {
        return maxDmg;
    }

    public int getMinDmg() {
        return minDmg;
    }

    public void reload() {
        if (ammo >= fullLoaded - loadedAmmo) {
            ammo -= fullLoaded - loadedAmmo;
            loadedAmmo = fullLoaded;
        } else {
            loadedAmmo += loadedAmmo + ammo;
            ammo = 0;
        }
    }
}