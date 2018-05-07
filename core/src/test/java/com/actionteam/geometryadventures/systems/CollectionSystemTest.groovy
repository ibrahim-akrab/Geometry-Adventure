package com.actionteam.geometryadventures.systems

import com.actionteam.geometryadventures.ecs.ECSManager
import com.actionteam.geometryadventures.events.ECSEvents

/**
 * Created by Ibrahim M. Akrab on 5/4/18.
 * ibrahim.m.akrab@gmail.com
 */
class CollectionSystemTest extends GroovyTestCase {
    ECSManager ecsManager = ECSManager.getInstance()
    CollectionSystem collectionSystem = new CollectionSystem()
    void setUp() {
        super.setUp()
        ecsManager.addSystem(collectionSystem);
        ecsManager.subscribe(ECSEvents.COLLECTIBLE_COLLIDED_EVENT, collectionSystem)

    }

    void tearDown() {
    }

    void testHandle() {
    }

    void testEcsManagerAttached() {
    }

    void testUpdate() {
    }
}
