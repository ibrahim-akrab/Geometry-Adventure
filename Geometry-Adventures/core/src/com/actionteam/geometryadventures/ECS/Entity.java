package com.actionteam.geometryadventures.ECS;

/**
 * Created by ibrahim on 3/18/18.
 */

public class Entity {
    public static final int MAXIMUM_COMPONENT_NUMBER = 64;
    private final int id;

    private int components[]; // components ids
    private long componentsMask;

    public Entity(int id){
        this.id = id;
        components = new int[MAXIMUM_COMPONENT_NUMBER];
    }

    public Entity(){
        this(0);
    }

    public boolean addComponent(int componentCode, int componentId){
        long mask = 1L << componentCode;
        if((componentsMask & mask) == mask) return false;
        components[componentCode] = componentId;
        componentsMask |= mask;
        return true;
    }

    public boolean removeComponent(int componentCode){
        long mask = 1L << componentCode;
        if((componentsMask & mask) != mask) return false;
        componentsMask &= (~mask);
        return true;
    }

    public int getComponentId(int componentCode){
        long mask = 1L << componentCode;
        if((componentsMask & mask) != mask) return -1;
        return components[componentCode];
    }

    public int getId() {
        return id;
    }

    public boolean checkComponentAttached(int componentCode, int componentId){
        return components[componentCode] == componentId;
    }

    public long getComponentsMask(){
        return componentsMask;
    }
}
