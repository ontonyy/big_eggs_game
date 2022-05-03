package com.bigeggs.client.models;

public class Weapon {
    private String name = "M4";
    private int maxDmg = 20;
    private int minDmg = 10;
    private int loadedAmmo = 60;
    private int fullLoaded = 60;
    private int ammo = 150;
    private boolean automatic = false;

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
        } else if (ammo > 0){
            loadedAmmo += loadedAmmo + ammo;
            ammo = 0;
        }
    }

    public void shoot() {
        loadedAmmo -= 1;
    }

    public int getAmmo() {
        return ammo;
    }

    public void addAmmo(int ammo) {
        this.ammo += ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public boolean getAutomatic() {
        return automatic;
    }

    public int getLoadedAmmo() {
        return loadedAmmo;
    }

    public void setAutomatic() {
        automatic = !automatic;
    }

    public void setInitialAmmo() {
        loadedAmmo = 60;
        fullLoaded = 60;
        ammo = 150;
    }

    public void setMaxDmg(int maxDmg) {
        this.maxDmg = maxDmg;
    }

    public void setMinDmg(int minDmg) {
        this.minDmg = minDmg;
    }
}
