package Packets;

import Packets.add.*;
import Packets.remove.PacketRemoveBoost;
import Packets.remove.PacketRemoveBullet;
import Packets.remove.PacketRemovePlayer;
import Packets.update.PacketPlayerPosition;
import Packets.update.PacketUpdateEnemyAI;
import Packets.update.PacketUpdatePlayerInfo;

/**
 * Creator for different packets that will be sent between client-server
 */
public class PacketCreator {

    public static PacketAddPlayer createPacketAddPlayer(String name) {
        PacketAddPlayer packetConnect = new PacketAddPlayer();
        packetConnect.setPlayerName(name);
        return packetConnect;
    }

    public static PacketUpdatePlayerInfo createPacketUpdatePlayer(float x, float y, float angle, String direction, int health, int lives, int id, boolean invisible, boolean dead) {
        PacketUpdatePlayerInfo packetPlayerInfo = new PacketUpdatePlayerInfo();
        packetPlayerInfo.setX(x);
        packetPlayerInfo.setY(y);
        packetPlayerInfo.setAngle(angle);
        packetPlayerInfo.setDirection(direction);
        packetPlayerInfo.setHealth(health);
        packetPlayerInfo.setId(id);
        packetPlayerInfo.setInvisible(invisible);
        packetPlayerInfo.setLives(lives);
        packetPlayerInfo.setDead(dead);
        return packetPlayerInfo;
    }

    public static PacketBullet createPacketBullet(float posX, float posY, float dirX, float dirY, String name, int id) {
        PacketBullet packetBullet = new PacketBullet();
        packetBullet.setDirectionX(dirX);
        packetBullet.setDirectionY(dirY);
        packetBullet.setPositionX(posX);
        packetBullet.setPositionY(posY);
        packetBullet.setId(id);
        packetBullet.setPlayerName(name);
        return packetBullet;
    }

    public static PacketRemoveBullet createPacketRemoveBullet(String name, int id) {
        PacketRemoveBullet packetRemoveBullet = new PacketRemoveBullet();
        packetRemoveBullet.setId(id);
        packetRemoveBullet.setPlayerName(name);
        return packetRemoveBullet;
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

    public static PacketAddBoost createPacketAddBoost(int id, float x, float y, String type) {
        PacketAddBoost boost = new PacketAddBoost();
        boost.setX(x);
        boost.setY(y);
        boost.setType(type);
        boost.setId(id);
        return boost;
    }

    public static PacketRemoveBoost createPacketRemoveBoost(int id) {
        PacketRemoveBoost removeBoost = new PacketRemoveBoost();
        removeBoost.setId(id);
        return removeBoost;
    }

    public static PacketPlayerPosition createPacketPlayerPosition(boolean change, int x, int y) {
        PacketPlayerPosition packetPosition = new PacketPlayerPosition();
        packetPosition.setChange(change);
        packetPosition.setX(x);
        packetPosition.setY(y);
        return packetPosition;
    }

    public static PacketAddScore createPacketAddScore(int score, int id, int lives, String name) {
        PacketAddScore addScore = new PacketAddScore();
        addScore.setScore(score);
        addScore.setKilledPlayerLives(lives);
        addScore.setPlayerName(name);
        addScore.setId(id);
        return addScore;
    }
}
