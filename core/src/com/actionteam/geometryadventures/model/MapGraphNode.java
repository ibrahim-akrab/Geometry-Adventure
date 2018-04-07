package com.actionteam.geometryadventures.model;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

/**
 * Created by rka97 on 4/7/2018.
 */

public class MapGraphNode {
    public int x;
    public int y;
    public int index;
    public Array<Connection<MapGraphNode>> edges;
    public MapGraphNode(int x, int y, int index, int numEdges)
    {
        this.x = x;
        this.y = y;
        this.index = index;
        edges = new Array<Connection<MapGraphNode>>(numEdges);
    }
    public void addEdge(MapGraphEdge edgeToAdd)
    {
        edges.add(edgeToAdd);
    }
    public void removeEdge(int arrayIndex) { edges.removeIndex(arrayIndex); }
    public int getIndex() { return index; }
}
