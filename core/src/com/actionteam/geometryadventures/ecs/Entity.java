package com.actionteam.geometryadventures.ecs;

/**
 * Created by ibrahim on 3/18/18.
 */

/**
 * An entity is a general purpose object with a unique id
 * It consists of a collection of components that make up its behaviour
 */

public class Entity {

    static final int MAXIMUM_COMPONENT_NUMBER = 64;

    // the id of the entity, which also represents its index in the ECSManager's entity list
    private final int id;
    // an array of components ids
    // the index of each component id corresponds with the code of the component
    private int components[];
    // a 64-bit mask where the i(th) bit represents the existence of the component with the code i
    private long componentsMask;

    Entity(int id) {
        this.id = id;
        components = new int[MAXIMUM_COMPONENT_NUMBER];
    }

    /**
     * Adds a component to the entity
     *
     * @return true if successful, false otherwise
     */
    boolean addComponent(int componentCode, int componentId) {
        long mask = 1L << componentCode;
        if (hasComponent(componentCode)) return false;
        components[componentCode] = componentId;
        componentsMask |= mask;
        return true;
    }

    /**
     * Removes a component from the entity
     *
     * @return true if successful, false otherwise
     */
    boolean removeComponent(int componentCode) {
        long mask = 1L << componentCode;
        if ((componentsMask & mask) != mask) return false;
        componentsMask &= (~mask);
        return true;
    }

    /**
     * Checks if a specific component is attached
     *
     * @return true if attached, false otherwise
     */
    boolean checkComponentAttached(int componentCode, int componentId) {
        return hasComponent(componentCode) && components[componentCode] == componentId;
    }

    /**
     * Checks if a specific type of component is attached
     *
     * @return true if attached, false otherwise
     */
    boolean hasComponent(int componentCode) {
        long mask = 1L << componentCode;
        return ((componentsMask & mask) == mask);
    }

    /**
     * @return The id of the component with the code @componentCode
     */
    int getComponentId(int componentCode) {
        if (!hasComponent(componentCode)) return -1;
        return components[componentCode];
    }

    public int getId() {
        return id;
    }


    long getComponentsMask() {
        return componentsMask;
    }
}
