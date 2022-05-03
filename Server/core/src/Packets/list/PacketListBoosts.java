package Packets.list;

import Packets.add.PacketAddBoost;

import java.util.List;

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
