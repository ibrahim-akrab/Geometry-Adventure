package com.actionteam.geometryadventures.model;

/**
 * Created by theartful on 4/1/18.
 */

public class TileType {

    public static final String WALL = "wall";
    public static final String FLOOR = "floor";
    public static final String ENEMY = "enemy";
    public static final String PLAYER = "player";
    public static final String MISC = "misc";
    public static final String PORTAL = "portal";
    public static final String DOOR = "door";


    public String type;
    public String textureName;
    public int xTiles;
    public int numberOfTiles;
    public boolean isPattern;
    public int z;
    public boolean collidable;
    public boolean isAnimated;
    public int frames;
    public float speed;

    public TileType(String type, String textureName, int xTiles, int numberOfTiles, boolean isPattern,
                    int z, boolean collidable) {
        this.type = type;
        this.textureName = textureName;
        this.xTiles = xTiles;
        this.numberOfTiles = numberOfTiles;
        this.isPattern = isPattern;
        this.z = z;
        this.collidable = collidable;
    }

}
