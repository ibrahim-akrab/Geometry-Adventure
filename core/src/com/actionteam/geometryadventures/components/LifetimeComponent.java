package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.actionteam.geometryadventures.systems.ClockSystem;

/**
 * Created by Ibrahim M. Akrab on 4/3/18.
 * ibrahim.m.akrab@gmail.com
 */
public class LifetimeComponent extends Component {

    public int timeOfCreation;
    public int lifetime;

    public LifetimeComponent() {
        super(0);
    }

    public LifetimeComponent(int lifetime) {
        super(Components.LIFETIME_COMPONENT_CODE);
        this.timeOfCreation = ClockSystem.millis();
        this.lifetime = lifetime;
    }
}
