package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.LethalComponent;
import com.actionteam.geometryadventures.components.LifetimeComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.components.WeaponComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.entities.Entities;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by ibrahim on 4/2/18.
 */

public class WeaponSystem extends System implements ECSEventListener{

    public WeaponSystem(){ super(Components.WEAPON_COMPONENT_CODE, Components.HEALTH_COMPONENT_CODE, Components.LETHAL_COMPONENT_CODE);}

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode){
            case ECSEvents.ATTACK_EVENT:
                float[] weaponData = (float[]) message;
                entityAttacked(weaponData[0], weaponData[1], weaponData[2],
                        (int) weaponData[3], weaponData[4] == 1f ? true: false);
        }
        return false;
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void ecsManagerAttached(){
        ecsManager.subscribe(ECSEvents.ATTACK_EVENT, this);
    }

    /**
     * called when an entity attacks
     * @param x x-position of entity when fired
     * @param y y-position of entity when fired
     * @param angle rotation angle of entity
     * @param entityId  the id of the entity that attacked
     * @return  creates entities that has the properities of the bullet or the lethal object being shot
     */
    private boolean entityAttacked(float x, float y, float angle, int entityId, boolean isPlayer) {
        WeaponComponent weaponComponent = (WeaponComponent) ecsManager.getComponent(entityId,
                Components.WEAPON_COMPONENT_CODE);
        if (TimeUtils.timeSinceMillis(weaponComponent.timeOfLastFire) < weaponComponent.coolDownTime)
            return false;

//        Gdx.app.log("entityAttack", "attacked");

        weaponComponent.timeOfLastFire = TimeUtils.millis();

        for (int i = 0; i < weaponComponent.numberOfLethalObjectsAtTime; i++){
            int entity = ecsManager.createEntity();
            //TODO handle if ecsManager can't add component
            ecsManager.addComponent(createLethalComponent(weaponComponent,entityId), entity);
            ecsManager.addComponent(createCollisionComponent(weaponComponent, isPlayer), entity);
            ecsManager.addComponent(createPhysicsComponent(weaponComponent, x, y, angle, i), entity);
            ecsManager.addComponent(createGraphicsComponent(weaponComponent), entity);
            ecsManager.addComponent(createLifetimeComponent(weaponComponent), entity);
        }
        return true;
    }

    /**
     * creates lethal component that is suitable to the weapon component in question
     * @param weaponComponent   the weapon component in question
     * @param entityId          the id of the entity that the weapon component belongs to
     * @return                  the created lethal component
     */
    private LethalComponent createLethalComponent(WeaponComponent weaponComponent, int entityId){
        LethalComponent lethalComponent = new LethalComponent();
        lethalComponent.damage = weaponComponent.damage;
        lethalComponent.owner = entityId;
        return lethalComponent;
    }

    /**
     * creates collision component that is suitable to the weapon component in question
     * @param weaponComponent   the weapon component in question
     * @return                  the created collision component
     */
    private CollisionComponent createCollisionComponent(WeaponComponent weaponComponent, boolean isPlayer){
        CollisionComponent collisionComponent = new CollisionComponent();
        collisionComponent.shapeType = CollisionComponent.CIRCLE;
        collisionComponent.radius = weaponComponent.radius;
        collisionComponent.id = isPlayer? Entities.LETHAL_PLAYER_COLLISION_ID: Entities.LETHAL_ENEMY_COLLISION_ID;
        collisionComponent.mask = ~((1L << (isPlayer? Entities.PLAYER_COLLISION_ID: Entities.ENEMY_COLLISION_ID)) | (1L << collisionComponent.id));
        return collisionComponent;
    }

    /**
     * creates physics component that is suitable to the weapon component in question
     * @param weaponComponent   the weapon component in question
     * @param x                 the x-position of entity when firing
     * @param y                 the y-position of entity when firing
     * @param angle             the rotation angle of entity when firing
     * @param index             the index of the bullet or lethal objects (in order of creation)
     * @return                  the created physics component
     */
    private PhysicsComponent createPhysicsComponent(WeaponComponent weaponComponent, float x,
                                                    float y, float angle, int index){
        PhysicsComponent physicsComponent = new PhysicsComponent();
        if (weaponComponent.weaponDamageRegion == WeaponComponent.CIRCLE) {
            physicsComponent.position.x = x;
            physicsComponent.position.y = y;
            angle += index * (float) Math.pow(-1, index) * weaponComponent.angleOfSpreading;
            if (weaponComponent.speed != 0) {
                physicsComponent.velocity =
                        new Vector2((float) Math.cos(angle),(float) Math.sin(Math.PI + angle))
                                .scl(weaponComponent.speed);
            }
            physicsComponent.rotationAngle = angle;
        }
        else if (weaponComponent.weaponDamageRegion == WeaponComponent.SEMICIRCLE){
            physicsComponent.centerOfRotation.x = x;
            physicsComponent.centerOfRotation.y = y;
            angle += index * (float) Math.pow(-1, index) * weaponComponent.angleOfSpreading;
            if (weaponComponent.speed != 0) {
                physicsComponent.velocity =
                        new Vector2((float) Math.sin(Math.PI - angle),(float) Math.cos(angle))
                                .scl(weaponComponent.speed);
            }
            physicsComponent.rotationAngle = angle;
            physicsComponent.position =
                    physicsComponent.centerOfRotation.cpy()
                    .add(new Vector2(
                            (float) Math.cos(angle), (float) Math.sin(Math.PI + angle))
                            .scl(weaponComponent.radiusOfDamageRegion));

            physicsComponent.angularAcceleration =
                    physicsComponent.centerOfRotation.cpy().sub(physicsComponent.position).limit(1.0f).scl(
                            weaponComponent.speed * weaponComponent.speed/
                                    weaponComponent.radiusOfDamageRegion
                    );
        }
        return physicsComponent;

    }

    /**
     * creates graphics component that is suitable to the weapon component in question
     * @param weaponComponent   the weapon component in question
     * @return                  the created graphics component
     */
    private GraphicsComponent createGraphicsComponent(WeaponComponent weaponComponent){
        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.width = weaponComponent.radius;
        graphicsComponent.height = weaponComponent.radius;
        graphicsComponent.textureIndex = 0;
        graphicsComponent.textureName = "wall";
        return graphicsComponent;
    }

    /**
     * creates lifetime component for the bullets or lethal objects
     * @return  the created lifetime component
     */
    private LifetimeComponent createLifetimeComponent(WeaponComponent weaponComponent){
        LifetimeComponent lifetimeComponent = new LifetimeComponent();
        lifetimeComponent.lifetime = weaponComponent.lifetimeOfLethalObject;
        return lifetimeComponent;
    }
}
