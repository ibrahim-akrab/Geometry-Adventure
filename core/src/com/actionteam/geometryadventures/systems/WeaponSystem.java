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

    private boolean entityAttacked(float x, float y, float angle, int componentId, int entityId){
        WeaponComponent weaponComponent = (WeaponComponent) ecsManager.getComponent(componentId);
        if (TimeUtils.timeSinceMillis(weaponComponent.timeOfLastFire) < weaponComponent.coolDownTime)
            return false;

//        Gdx.app.log("entityAttack", "attacked");

        weaponComponent.timeOfLastFire = TimeUtils.millis();

        for (int i = 0; i < weaponComponent.numberOfLethalObjectsAtTime; i++){
            int entity = ecsManager.createEntity();
            LethalComponent lethalComponent = new LethalComponent();
            lethalComponent.damage = weaponComponent.damage;
            lethalComponent.owner = entityId;
            //TODO handle if ecsManager can't add component
            ecsManager.addComponent(lethalComponent, entity);
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
            ecsManager.addComponent(collisionComponent, entity);
            PhysicsComponent physicsComponent = new PhysicsComponent();
            physicsComponent.position.x = x;
            physicsComponent.position.y = y;
            angle += i * (float)Math.pow(-1, i) * weaponComponent.angleOfSpreading;
            physicsComponent.rotationAngle = angle;
            if (weaponComponent.speed != 0){
                physicsComponent.velocity.x = weaponComponent.speed * (float)Math.cos(angle);
                physicsComponent.velocity.y = weaponComponent.speed * (float)Math.sin(Math.PI + angle);
            }
            ecsManager.addComponent(physicsComponent, entity);
            GraphicsComponent graphicsComponent = new GraphicsComponent();
            graphicsComponent.width = weaponComponent.radius;
            graphicsComponent.height = weaponComponent.radius;
            graphicsComponent.textureIndex = 0;
            graphicsComponent.textureName = "wall";
            ecsManager.addComponent(graphicsComponent, entity);

            LifetimeComponent lifetimeComponent = new LifetimeComponent();
            lifetimeComponent.lifetime = 200;
            ecsManager.addComponent(lifetimeComponent, entity);
        }
        return true;
    }
}
