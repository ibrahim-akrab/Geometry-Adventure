package com.actionteam.geometryadventures.model;

/**
 * Created by theartful on 3/27/18.
 */
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;

public class Map {
    private final String WALL = "Wall";
    private final String FLOOR = "Floor";
    private final String ENEMY = "Enemy";

    private ArrayList<Tile> floorTiles;
    private ArrayList<Tile> wallTiles;
    private ArrayList<Tile> enemyTiles;

    public Map() {
        floorTiles = new ArrayList<Tile>();
        wallTiles = new ArrayList<Tile>();
        enemyTiles = new ArrayList<Tile>();
    }

    public void addTile(Tile tile) {
        Gdx.app.log("Adding Tile", "x: " + tile.x +", y: " + tile.y + ", textureName: " +
                tile.textureName + ", index:" + tile.textureIndex);
        if(tile.type.equals(FLOOR) && !floorTiles.contains(tile)) floorTiles.add(tile);
        else if(tile.type.equals(WALL) && !wallTiles.contains(tile)) wallTiles.add(tile);
    }

    public Tile searchTile(float x, float y){
        for(Tile tile : enemyTiles) {
            if(tile.x == x && tile.y == y)
                return tile;
        }
        for(Tile tile : wallTiles) {
            if(tile.x == x && tile.y == y)
                return tile;
        }
        for(Tile tile : floorTiles) {
            if(tile.x == x && tile.y == y)
                return tile;
        }
        return null;
    }

    public Tile searchFloorTiles(float x, float y){
        return null;
    }

    public Tile searchWallTiles(float x, float y){
        for(Tile tile : wallTiles) {
            if(tile.x == x && tile.y == y)
                return tile;
        }
        return null;
    }

    public ArrayList<Tile> getFloorTiles(){
        return floorTiles;
    }

    public ArrayList<Tile> getWallTiles(){
        return wallTiles;
    }

    public ArrayList<Tile> getEnemyTiles(){
        return enemyTiles;
    }

    public void removeTile(Tile tile) {
        if(tile.type.equals(FLOOR)) floorTiles.remove(tile);
        else if(tile.type.equals(WALL)) wallTiles.remove(tile);
        else if(tile.type.equals(ENEMY)) enemyTiles.remove(tile);
        Gdx.app.log("Removing tile", "x: " + tile.x + ", y : " + tile.y);
    }
}
