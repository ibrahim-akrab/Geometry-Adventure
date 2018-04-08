package com.actionteam.geometryadventures.ecs;

/**
 * Created by ibrahim on 3/18/18.
 */

/**
 * Components are just data holders for a specific behaviour
 * It labels an entity as possessing this type of behaviour
 */

public abstract class Component {

    // a non-negative integer that represents the type of the component
    // each class that inherits from Component should have a unique code
    // component codes are also used in entities as the index in the components array
    // so a component with the code i is placed at the ith position
    private final int componentCode;

    // a non-negative integer that represents a specific instance of a component
    // component ids are also used in the ecs manager as the index in the components array
    private int componentId;

    protected Component(int componentCode) {
        this.componentCode = componentCode;
    }

    public int getCode() {
        return componentCode;
    }

    public void setId(int id) {
        componentId = id;
    }

    public int getId() {
        return componentId;
    }
}
