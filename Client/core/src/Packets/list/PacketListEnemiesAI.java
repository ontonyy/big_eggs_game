package Packets.list;

import Packets.add.PacketAddEnemyAI;

import java.util.List;

/**
 * Packet for send list of enemies in start and remove unused, if before that was game
 */
public class PacketListEnemiesAI {
    private List<PacketAddEnemyAI> enemies;
    private boolean removeUnused;

    public List<PacketAddEnemyAI> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<PacketAddEnemyAI> enemies) {
        this.enemies = enemies;
    }

    public boolean isRemoveUnused() {
        return removeUnused;
    }

    public void setRemoveUnused(boolean removeUnused) {
        this.removeUnused = removeUnused;
    }
}
