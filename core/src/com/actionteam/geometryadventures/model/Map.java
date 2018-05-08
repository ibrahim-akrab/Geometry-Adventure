package com.actionteam.geometryadventures.model;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by theartful on 4/1/18.
 */

public class Map {

    public static final String WALL = "wall";
    public static final String FLOOR = "floor";
    public static final String ENEMY = "enemy";
    public static final String PLAYER = "player";
    public static final String MISC = "misc";
    public static final String PORTAL = "portal";
    public static final String DOOR = "door";
    public static final String LIGHT = "light";
    public static final String COLLECTABLE = "collectable";

    private int[] dimensions;
    private boolean newLight;
    private MapConfig config;

    private List<Tile> tiles;
    private List<Tile> floorTiles;
    private List<Tile> wallTiles;
    private List<Tile> enemyTiles;
    private List<Tile> miscTiles;
    private List<Tile> portalTiles;
    private List<Tile> doorTiles;
    private List<Tile> lightTiles;
    private List<CollectibleTile> collectibleTiles;
    private PlayerTile playerTile;

    public class MapConfig{
        public Vector3 ambientLight = new Vector3(1,1,1);
        public float ambientIntensity = 0.5f;
    }

    public Map() {
        tiles = new ArrayList<Tile>();
    }

    public void updateTiles() {
        floorTiles = new ArrayList<Tile>();
        wallTiles = new ArrayList<Tile>();
        enemyTiles = new ArrayList<Tile>();
        miscTiles = new ArrayList<Tile>();
        portalTiles = new ArrayList<Tile>();
        doorTiles = new ArrayList<Tile>();
        lightTiles = new ArrayList<Tile>();
        collectibleTiles = new ArrayList<CollectibleTile>();

        for (Tile tile : tiles) {
            if (tile.tileType.equals(ENEMY))
                enemyTiles.add(tile);
            else if (tile.tileType.equals(FLOOR))
                floorTiles.add(tile);
            else if (tile.tileType.equals(PORTAL))
                portalTiles.add(tile);
            else if (tile.tileType.equals(PLAYER))
                playerTile = (PlayerTile) tile;
            else if (tile.tileType.equals(WALL))
                wallTiles.add(tile);
            else if (tile.tileType.equals(DOOR))
                doorTiles.add(tile);
            else if (tile.tileType.equals(LIGHT))
                lightTiles.add(tile);
            else if (tile.tileType.equals(COLLECTABLE))
                collectibleTiles.add((CollectibleTile)tile);
            else if (tile.collidable)
                wallTiles.add(tile);
            else
                miscTiles.add(tile);
        }
        tiles = null;
    }

    public List<Tile> getFloorTiles() {
        return floorTiles;
    }

    public List<Tile> getWallTiles() {
        return wallTiles;
    }

    public List<Tile> getEnemyTiles() {
        return enemyTiles;
    }

    public List<Tile> getPortalTiles() {
        return portalTiles;
    }

    public List<Tile> getLightTiles() {
        return lightTiles;
    }

    public List<CollectibleTile> getCollectibleTiles() {
        return collectibleTiles;
    }

    /* These are the tiles the enemy can not traverse. */
    public List<Tile> getBlockedTiles() {
        return getWallTiles(); // should be extended when there are other blockes tiles.
    }

    /* Get the dimensions of the map. */
    public int[] getMapDimensions() {
        if (dimensions != null)
            return dimensions;
        int minX = 0;
        int maxX = 0;
        int minY = 0;
        int maxY = 0;
        ArrayList<List<Tile>> tileArrays = new ArrayList<List<Tile>>(3);
        tileArrays.add(floorTiles);
        tileArrays.add(enemyTiles);
        tileArrays.add(wallTiles);

        for (List<Tile> tileArray : tileArrays) {
            for (Tile tile : tileArray) {
                if (tile.x < minX)
                    minX = (int) (tile.x);
                if (tile.y < minY)
                    minY = (int) (tile.y);
                if (tile.x > maxX)
                    maxX = (int) (tile.x);
                if (tile.y > maxY)
                    maxY = (int) (tile.y);
            }
        }
        dimensions = new int[]{minX, maxX, minY, maxY};
        return dimensions;
    }

    public List<Tile> getMiscTiles() {
        return miscTiles;
    }

    public PlayerTile getPlayerTile() {
        return playerTile;
    }

    public MapConfig getConfig() {
        return config;
    }
}