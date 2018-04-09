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
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
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
                entityAttacked(weaponData[0], weaponData[1], weaponData[2], (int)weaponData[3], (int)weaponData[4]);
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
     * @param componentId   the id of the weapon component that was used to attack
     * @param entityId  the id of the entity that attacked
     * @return  creates entities that has the properities of the bullet or the lethal object being shot
     */
    private boolean entityAttacked(float x, float y, float angle, int componentId, int entityId){
        WeaponComponent weaponComponent = (WeaponComponent) ecsManager.getComponent(componentId);
        if (TimeUtils.timeSinceMillis(weaponComponent.timeOfLastFire) < weaponComponent.coolDownTime)
            return false;

//        Gdx.app.log("entityAttack", "attacked");

        weaponComponent.timeOfLastFire = TimeUtils.millis();

        for (int i = 0; i < weaponComponent.numberOfLethalObjectsAtTime; i++){
            int entity = ecsManager.createEntity();
            //TODO handle if ecsManager can't add component
            ecsManager.addComponent(createLethalComponent(weaponComponent,entityId), entity);
            ecsManager.addComponent(createCollisionComponent(weaponComponent), entity);
            ecsManager.addComponent(createPhysicsComponent(weaponComponent, x, y, angle, i), entity);
            ecsManager.addComponent(createGraphicsComponent(weaponComponent), entity);
            ecsManager.addComponent(createLifetimeComponent(), entity);
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
    private CollisionComponent createCollisionComponent(WeaponComponent weaponComponent){
        CollisionComponent collisionComponent = new CollisionComponent();
        switch (weaponComponent.weaponDamageRegion){
            case WeaponComponent.CIRCLE:
                collisionComponent.shapeType = CollisionComponent.CIRCLE;
                break;
            case WeaponComponent.SEMICIRCLE:
                // TODO change to semicircle when omnia finishes it
                collisionComponent.shapeType = CollisionComponent.CIRCLE;
                break;
        }
        collisionComponent.radius = weaponComponent.radius;
        //TODO customize collision component ID and Mask
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
        physicsComponent.position.x = x;
        physicsComponent.position.y = y;
        angle += index * (float)Math.pow(-1, index) * weaponComponent.angleOfSpreading;
        physicsComponent.rotationAngle = angle;
        if (weaponComponent.speed != 0){
            physicsComponent.velocity.x = weaponComponent.speed * (float)Math.cos(angle);
            physicsComponent.velocity.y = weaponComponent.speed * (float)Math.sin(Math.PI + angle);
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
    private LifetimeComponent createLifetimeComponent(){
        LifetimeComponent lifetimeComponent = new LifetimeComponent();
        lifetimeComponent.lifetime = 200;
        return lifetimeComponent;
    }
}
