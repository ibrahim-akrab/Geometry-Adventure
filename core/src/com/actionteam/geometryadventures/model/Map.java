package com.actionteam.geometryadventures.model;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by theartful on 4/1/18.
 */

public class Map {

    private final String WALL = "Wall";
    private final String FLOOR = "Floor";
    private final String ENEMY = "Enemy";

    private int[] dimensions;

    private List<Tile> tiles;
    private List<Tile> floorTiles;
    private List<Tile> wallTiles;
    private List<Tile> enemyTiles;
    private List<Tile> miscTiles;
    private List<Tile> portalTiles;

    public Map() {
        tiles = new ArrayList<Tile>();
    }

    public void updateTiles() {
        floorTiles = new ArrayList<Tile>();
        wallTiles = new ArrayList<Tile>();
        enemyTiles = new ArrayList<Tile>();
        miscTiles = new ArrayList<Tile>();
        for (Tile tile : tiles) {
            if (tile.type.equals("enemy"))
                enemyTiles.add(tile);
            else if (tile.type.equals("floor"))
                floorTiles.add(tile);
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

    public List<Tile> getPortalTiles() { return portalTiles;}

    public Tile searchTiles(float x, float y) {
        for (Tile tile : tiles) {
            if (tile.x == x && tile.y == y)
                return tile;
        }
        return null;
    }

    public Tile searchTilesFiltered(float x, float y, String type) {
        for (Tile tile : tiles) {
            if (tile.x == x && tile.y == y && tile.type.equals(type))
                return tile;
        }
        return null;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void removeTile(Tile tile) {
        if (tile == null) return;
        tiles.remove(tile);
        Gdx.app.log("Removing tile", "x: " + tile.x + ", y : " + tile.y);
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
}