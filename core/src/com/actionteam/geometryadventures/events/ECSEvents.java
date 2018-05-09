package com.actionteam.geometryadventures.events;

import com.actionteam.geometryadventures.ecs.ECSEvent;
import com.badlogic.gdx.math.Vector3;

/**
 * A factory-like class that initializes all kinds of events
 * <p>
 * Created by theartful on 3/27/18.
 */

public class ECSEvents {

    public static final int RESIZE_EVENT = 0;
    public static final int DISPOSE_EVENT = 1;
    public static final int PLAYER_MOVED_EVENT = 2;
    public static final int COLLIDABLE_MOVED_EVENT = 3;
    public static final int COLLISION_EVENT = 4;
    public static final int ATTACK_EVENT = 5;
    public static final int LOUD_WEAPON_FIRED_EVENT = 6;
    public static final int ENEMY_COLLIDED_EVENT = 7;
    public static final int PLAYER_DEAD_EVENT = 8;
    public static final int ENEMY_DEAD_EVENT = 9;
    public static final int BULLET_COLLIDED_EVENT = 10;
    public static final int COLLECTIBLE_COLLIDED_EVENT = 11;
    public static final int MOVED_TO_A_PORTAL_EVENT = 12;
    public static final int END_OF_LEVEL_EVENT = 13;
    public static final int HEART_COLLECTED_EVENT = 14;
    public static final int COIN_COLLECTED_EVENT = 15;
    public static final int KEY_COLLECTED_EVENT = 16;
    public static final int LEVEL_PAUSED = 17;
    public static final int LEVEL_STARTED = 18;
    public static final int ADD_TASK = 19;

    public static ECSEvent resizeEvent(int width, int height) {
        return new ECSEvent(RESIZE_EVENT, new int[]{width, height});
    }

    public static ECSEvent disposeEvent() {
        return new ECSEvent(DISPOSE_EVENT, null);
    }

    public static ECSEvent playerMovedEvent(float x, float y) {
        return new ECSEvent(PLAYER_MOVED_EVENT, new float[]{x, y});
    }

    public static ECSEvent collidableMovedEvent(float x1, float y1, float x2, float y2, int entityID) {
        return new ECSEvent(COLLIDABLE_MOVED_EVENT, new float[]{x1, y1, x2, y2, entityID});
    }

    public static ECSEvent collisionEvent(boolean collided) {
        return new ECSEvent(COLLISION_EVENT, collided);
    }

    public static ECSEvent attackEvent(float x, float y, float angle, int entityId, boolean isPlayer) {
        return new ECSEvent(ATTACK_EVENT, new float[]{x, y, angle, entityId, isPlayer ? 1f : 0f});
    }

    public static ECSEvent enemyCollisionEvent(Integer entityID) {
        return new ECSEvent(ENEMY_COLLIDED_EVENT, entityID);
    }

    public static ECSEvent loudWeaponFired(Integer entityID) {
        return new ECSEvent(LOUD_WEAPON_FIRED_EVENT, entityID);
    }

    public static ECSEvent bulletCollisionEvent(int bulletId, int collidedEntityId) {
        return new ECSEvent(BULLET_COLLIDED_EVENT, new int[]{bulletId, collidedEntityId});
    }

    public static ECSEvent enemyDeadEvent(int enemyId, int killerId) {
        return new ECSEvent(ENEMY_DEAD_EVENT, new int[]{enemyId, killerId});
    }

    public static ECSEvent playerDeadEvent(int playerId, int killerId) {
        return new ECSEvent(PLAYER_DEAD_EVENT, new int[]{playerId, killerId});
    }

    public static ECSEvent collectibleCollisionEvent(int collectibleId, int collectorId) {
        return new ECSEvent(COLLECTIBLE_COLLIDED_EVENT, new int[]{collectibleId, collectorId});
    }

    public static ECSEvent endOfLevelEvent() {
        return new ECSEvent(END_OF_LEVEL_EVENT, null);
    }

    public static ECSEvent coinCollectedEvent(int collectorId, int coinValue) {
        return new ECSEvent(COIN_COLLECTED_EVENT, new int[]{collectorId, coinValue});
    }

    public static ECSEvent heartCollectedEvent(int collectorId, int healthValue) {
        return new ECSEvent(HEART_COLLECTED_EVENT, new int[]{collectorId, healthValue});
    }

    public static ECSEvent keyCollectedEvent(int collectorId, int keyValue) {
        return new ECSEvent(KEY_COLLECTED_EVENT, new int[]{collectorId, keyValue});
    }

    public static ECSEvent movedToAPortalEvent(Vector3 portalData) {
        return new ECSEvent(MOVED_TO_A_PORTAL_EVENT, portalData);
    }
}
