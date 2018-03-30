package com.actionteam.geometryadventures.model;




/**
 * Created by theartful on 3/27/18.
 */

public class TileType {
    public String type;
    public String textureName;
    public int xTiles;
    public int yTiles;
    public int numberOfTiles;

    public TileType(String type, String textureName, int xTiles, int yTiles, int numberOfTiles){
        this.type = type;
        this.textureName = textureName;
        this.xTiles = xTiles;
        this.yTiles = yTiles;
        this.numberOfTiles = numberOfTiles;
    }
}