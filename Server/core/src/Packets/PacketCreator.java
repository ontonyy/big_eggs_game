package Packets;

public class PacketCreator {

    public static PacketAddPlayer createPacketAddPlayer(String name) {
        PacketAddPlayer packetConnect = new PacketAddPlayer();
        packetConnect.setPlayerName(name);
        return packetConnect;
    }

    public static PacketUpdatePlayerInfo createPacketUpdatePlayer(float x, float y, float angle, String direction, int health, int id) {
        PacketUpdatePlayerInfo packetPlayerInfo = new PacketUpdatePlayerInfo();
        packetPlayerInfo.setX(x);
        packetPlayerInfo.setY(y);
        packetPlayerInfo.setAngle(angle);
        packetPlayerInfo.setDirection(direction);
        packetPlayerInfo.setHealth(health);
        packetPlayerInfo.setId(id);
        return packetPlayerInfo;
    }

    public static PacketRemovePlayer createPacketRemovePlayer(int id) {
        PacketRemovePlayer removePlayer = new PacketRemovePlayer();
        removePlayer.setId(id);
        return removePlayer;
    }

    public static PacketAddEnemyAI createPacketEnemyAI(float x, float y, float angle, int health, int id, String follow) {
        PacketAddEnemyAI packetAddEnemyAI = new PacketAddEnemyAI();
        packetAddEnemyAI.setX(x);
        packetAddEnemyAI.setY(y);
        packetAddEnemyAI.setAngle(angle);
        packetAddEnemyAI.setHealth(health);
        packetAddEnemyAI.setId(id);
        packetAddEnemyAI.setFollowPlayer(follow);
        return packetAddEnemyAI;
    }

    public static PacketUpdateEnemyAI createPacketUpdateEnemyAI(float x, float y, float angle, int health, int id, String follow) {
        PacketUpdateEnemyAI packetAddEnemyAI = new PacketUpdateEnemyAI();
        packetAddEnemyAI.setX(x);
        packetAddEnemyAI.setY(y);
        packetAddEnemyAI.setAngle(angle);
        packetAddEnemyAI.setHealth(health);
        packetAddEnemyAI.setId(id);
        packetAddEnemyAI.setFollowPlayer(follow);
        return packetAddEnemyAI;
    }
}
