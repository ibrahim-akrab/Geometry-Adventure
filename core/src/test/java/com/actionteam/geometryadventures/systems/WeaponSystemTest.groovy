package com.actionteam.geometryadventures.systems

import com.actionteam.geometryadventures.WeaponFactory
import com.actionteam.geometryadventures.components.CollisionComponent
import com.actionteam.geometryadventures.components.LethalComponent
import com.actionteam.geometryadventures.components.WeaponComponent
import com.actionteam.geometryadventures.ecs.ECSManager
import com.actionteam.geometryadventures.entities.Entities
import com.actionteam.geometryadventures.events.ECSEvents
import org.unitils.reflectionassert.ReflectionAssert



/**
 * Created by Ibrahim M. Akrab on 4/19/18.
 * ibrahim.m.akrab@gmail.com
 */

class WeaponSystemTest extends GroovyTestCase {
    void setUp() {
        super.setUp()
    }

    void tearDown() {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone()
    }

    void testHandle() {
        def weaponSystem = new WeaponSystem()
        weaponSystem.ecsManagerAttached()
        def message = new float[5]
        assertFalse("WeaponSystem handled an event other than the assigned one (ATTACK_EVENT)",
                weaponSystem.handle(ECSEvents.PLAYER_MOVED_EVENT, message))
        def ecsManager = ECSManager.getInstance()
        def entity = ecsManager.createEntity()
        ecsManager.addComponent(WeaponFactory.createWeapon(WeaponComponent.MELEE), entity)
        message[0] = 1f
        message[1] = 1f
        message[2] = 1f
        message[3] = entity
        message[4] = 1f
        assertTrue("WeaponSystem didn't handle the assigned event (ATTACK EVENT)",
                weaponSystem.handle(ECSEvents.ATTACK_EVENT, message))
    }

    void testEntityAttacked(){
        def weaponSystem = new WeaponSystem()
        ReflectionAssert.assertReflectionEquals("LethalComponent creation inside WeaponSystem wasn't done correctly",
                new LethalComponent(damage: 50, owner: 1),
                weaponSystem.createLethalComponent(new WeaponComponent(damage: 50), 1))

    }

    void testCreateLethalComponent(){
        def weaponSystem = new WeaponSystem()
        ReflectionAssert.assertReflectionEquals("LethalComponent creation inside WeaponSystem wasn't done correctly",
                new LethalComponent(damage: 50, owner: 1),
                weaponSystem.createLethalComponent(new WeaponComponent(damage: 50), 1))
    }

    void testCreateCollisionComponent(){
        def weaponSystem = new WeaponSystem()
        ReflectionAssert.assertReflectionEquals("CollisionComponent creation inside WeaponSystem wasn't done correctly",
                new CollisionComponent(shapeType: CollisionComponent.CIRCLE, radius: 50.5,
                        id: Entities.LETHAL_PLAYER_COLLISION_ID,
                        mask: ~(1L << Entities.PLAYER_COLLISION_ID |
                                1L << Entities.LETHAL_PLAYER_COLLISION_ID |
                                1L << Entities.LETHAL_ENEMY_COLLISION_ID)),
                weaponSystem.createCollisionComponent(new WeaponComponent(radius: 50.5), true))

        ReflectionAssert.assertReflectionEquals("CollisionComponent creation inside WeaponSystem wasn't done correctly",
                new CollisionComponent(shapeType: CollisionComponent.CIRCLE, radius: 154,
                        id: Entities.LETHAL_ENEMY_COLLISION_ID,
                        mask: ~(1L << Entities.ENEMY_COLLISION_ID |
                                1L << Entities.LETHAL_PLAYER_COLLISION_ID |
                                1L << Entities.LETHAL_ENEMY_COLLISION_ID)),
                weaponSystem.createCollisionComponent(new WeaponComponent(radius: 154), false))
    }
}
