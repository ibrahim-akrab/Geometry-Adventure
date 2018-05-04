package com.actionteam.geometryadventures.model;

/**
 * Created by theartful on 4/1/18.
 */

public class Tile {
    public String tileType;
    public String textureName;
    public int textureIndex;
    public boolean collidable;
    public float x;
    public float y;
    public int z;
    public boolean isAnimated;
    public int frames;
    public float speed;

    @Override
    public String toString(){
        return "Tile - type: " + tileType + ", textureName: " + textureName + ", x: " + x +
                ", y: " + y;
    }

}

