package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.actionteam.geometryadventures.systems.ClockSystem;

/**
 * Created by Ibrahim M. Akrab on 4/3/18.
 * ibrahim.m.akrab@gmail.com
 */
public class LifetimeComponent extends Component {

    public long timeOfCreation;
    public long lifetime;

    public LifetimeComponent() {
        super(Components.LIFETIME_COMPONENT_CODE);
        timeOfCreation = ClockSystem.millis();
    }
}
