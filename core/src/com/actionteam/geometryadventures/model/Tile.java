package com.actionteam.geometryadventures.model;

/**
 * Created by theartful on 3/27/18.
 */

public class Tile {
    public String type;
    public String textureName;
    public int textureIndex;
    public float x;
    public float y;
    public float z;
    public boolean collidable;

    @Override
    public String toString(){
        return "Tile - type: " + type + ", textureName: " + textureName + ", x: " + x +
                ", y: " + y;
    }
}
