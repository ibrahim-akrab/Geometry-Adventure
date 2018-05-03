package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by theartful on 3/27/18.
 */

public class HealthComponent extends Component {
    public int health;
    public boolean isDead = false;

    public HealthComponent() {
        super(Components.HEALTH_COMPONENT_CODE);
    }

    public void takeDamage(int damage){
        health -= damage;
    }
}