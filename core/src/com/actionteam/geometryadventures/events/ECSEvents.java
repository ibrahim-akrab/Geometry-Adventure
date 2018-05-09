package com.actionteam.geometryadventures.events;

import com.actionteam.geometryadventures.ecs.ECSEvent;
import com.actionteam.geometryadventures.systems.ClockSystem;
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
    public static final int CAST_EVENT = 20;
    public static final int FREEZE_EVENT = 21;
    public static final int UNFREEZE_EVENT = 22;

    /**
     * creates an event for resizing screen
     *
     * @param width  the width of the new screen window
     * @param height the height of the new screen window
     * @return created event with appropriate data
     */
    public static ECSEvent resizeEvent(int width, int height) {
        return new ECSEvent(RESIZE_EVENT, new int[]{width, height});
    }

    public static ECSEvent disposeEvent() {
        return new ECSEvent(DISPOSE_EVENT, null);
    }

    /**
     * creates an event for player's moving
     *
     * @param x new player's  x position
     * @param y new player's  y position
     * @return created event with appropriate data
     */
    public static ECSEvent playerMovedEvent(float x, float y) {
        return new ECSEvent(PLAYER_MOVED_EVENT, new float[]{x, y});
    }

    /**
     * creates an event for when a collidable object moves
     *
     * @param x1       old object's x-position
     * @param y1       old object's y-position
     * @param x2       new object's x-position
     * @param y2       new object's y-position
     * @param entityID moving object's id
     * @return created event with appropriate data
     */
    public static ECSEvent collidableMovedEvent(float x1, float y1, float x2, float y2, int entityID) {
        return new ECSEvent(COLLIDABLE_MOVED_EVENT, new float[]{x1, y1, x2, y2, entityID});
    }

    /**
     * creates an event for collision
     *
     * @param collided an indicator if collision happened or not
     * @return created event with appropriate data
     */
    public static ECSEvent collisionEvent(boolean collided) {
        return new ECSEvent(COLLISION_EVENT, collided);
    }

    /**
     * creates an event for when an attack occurs
     *
     * @param x        the x-position of the attacking entity
     * @param y        the y-position of the attacking entity
     * @param angle    the rotation angle of the attacking entity
     * @param entityId the ID of the attacking entity
     * @param isPlayer an indicator if the attacker is the player
     * @return created event with appropriate data
     */
    public static ECSEvent attackEvent(float x, float y, float angle, int entityId, boolean isPlayer) {
        return new ECSEvent(ATTACK_EVENT, new float[]{x, y, angle, entityId, isPlayer ? 1f : 0f});
    }

    /**
     * creates an event for when an is about to occur
     *
     * @param x        the x-position of the attacking entity
     * @param y        the y-position of the attacking entity
     * @param angle    the rotation angle of the attacking entity
     * @param entityId the ID of the attacking entity
     * @param isPlayer an indicator if the attacker is the player
     * @return created event with appropriate data
     */
    public static ECSEvent castEvent(float x, float y, float angle, int entityId, boolean isPlayer) {
        return new ECSEvent(CAST_EVENT, new float[]{x, y, angle, entityId, isPlayer ? 1f : 0f});
    }

    /**
     * creates an event for when an enemy collides
     *
     * @param entityID enemy's ID
     * @return created event with appropriate data
     */
    public static ECSEvent enemyCollisionEvent(Integer entityID) {
        return new ECSEvent(ENEMY_COLLIDED_EVENT, entityID);
    }

    /**
     * creates an event for when a weapon is fired
     *
     * @param entityID the ID of the fired bullets
     * @return created event with appropriate data
     */
    public static ECSEvent loudWeaponFired(Integer entityID) {
        return new ECSEvent(LOUD_WEAPON_FIRED_EVENT, entityID);
    }

    /**
     * creates an event for when a bullet collides
     *
     * @param bulletId         the ID of the collided bullet (surprisingly)
     * @param collidedEntityId the ID of the entity that collided with the bullet
     * @return created event with appropriate data
     */
    public static ECSEvent bulletCollisionEvent(int bulletId, int collidedEntityId) {
        return new ECSEvent(BULLET_COLLIDED_EVENT, new int[]{bulletId, collidedEntityId});
    }

    /**
     * creates an event for when an enemy is dead
     *
     * @param enemyId  the enemy's ID
     * @param killerId the killer's ID (usually the player if not in multiplayer)
     * @return created event with appropriate data
     */
    public static ECSEvent enemyDeadEvent(int enemyId, int killerId) {
        return new ECSEvent(ENEMY_DEAD_EVENT, new int[]{enemyId, killerId});
    }

    /**
     * creates an event for when player is dead
     *
     * @param playerId
     * @param killerId
     * @return created event with appropriate data
     */
    public static ECSEvent playerDeadEvent(int playerId, int killerId) {
        return new ECSEvent(PLAYER_DEAD_EVENT, new int[]{playerId, killerId});
    }

    /**
     * creates an event for when a collectible object collides with an entity
     *
     * @param collectibleId
     * @param collectorId
     * @return created event with appropriate data
     */
    public static ECSEvent collectibleCollisionEvent(int collectibleId, int collectorId) {
        return new ECSEvent(COLLECTIBLE_COLLIDED_EVENT, new int[]{collectibleId, collectorId});
    }

    /**
     * creates an event for when level is ended
     *
     * @return created event with appropriate data
     */
    public static ECSEvent endOfLevelEvent() {
        return new ECSEvent(END_OF_LEVEL_EVENT, null);
    }

    /**
     * creates an event for when a coin is collected
     *
     * @param collectorId
     * @param coinValue
     * @return created event with appropriate data
     */
    public static ECSEvent coinCollectedEvent(int collectorId, int coinValue) {
        return new ECSEvent(COIN_COLLECTED_EVENT, new int[]{collectorId, coinValue});
    }

    /**
     * creates an event for when a heart is collected
     *
     * @param collectorId
     * @param healthValue
     * @return created event with appropriate data
     */
    public static ECSEvent heartCollectedEvent(int collectorId, int healthValue) {
        return new ECSEvent(HEART_COLLECTED_EVENT, new int[]{collectorId, healthValue});
    }

    /**
     * creates an event for when a key is collected
     *
     * @param collectorId
     * @param keyValue
     * @return created event with appropriate data
     */
    public static ECSEvent keyCollectedEvent(int collectorId, int keyValue) {
        return new ECSEvent(KEY_COLLECTED_EVENT, new int[]{collectorId, keyValue});
    }

    /**
     * creates an event for when a portal is used
     *
     * @param portalData a vector containing position (x,y) and entity ID
     * @return created event with appropriate data
     */
    public static ECSEvent movedToAPortalEvent(Vector3 portalData) {
        return new ECSEvent(MOVED_TO_A_PORTAL_EVENT, portalData);
    }

    public static ECSEvent taskEvent(ECSEvent attackEvent, int castTime) {
        return new ECSEvent(ADD_TASK, new ClockSystem.Task(attackEvent, castTime));
    }

    public static ECSEvent freezeEvent(int entityId) {
        return new ECSEvent(FREEZE_EVENT, entityId);
    }

    public static ECSEvent unFreezeEvent(int entityId) {
        return new ECSEvent(UNFREEZE_EVENT, entityId);
    }
}
