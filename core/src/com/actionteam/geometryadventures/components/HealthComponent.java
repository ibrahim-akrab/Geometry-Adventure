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

    /**
     * decreases character's health by an amount of damage
     * @param damage the amount of damage will hurt character
     */
    public void takeDamage(int damage){
        health -= damage;
    }

    /**
     * increases character's health by an amount of healing
     * @param healingValue the amount of healing will cure character
     */
    public void heal(int healingValue){
        health += healingValue;
    }
}