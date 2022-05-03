package Packets.list;

import Packets.add.PacketAddBoost;

import java.util.List;

/**
 * Packet for send list of boosts in start and remove unused, if before that was game
 */
public class PacketListBoosts {
    private List<PacketAddBoost> boostList;
    private boolean removeUnused;

    public List<PacketAddBoost> getBoostList() {
        return boostList;
    }

    public void setBoostList(List<PacketAddBoost> boostList) {
        this.boostList = boostList;
    }

    public boolean isRemoveUnused() {
        return removeUnused;
    }

    public void setRemoveUnused(boolean removeUnused) {
        this.removeUnused = removeUnused;
    }
}
