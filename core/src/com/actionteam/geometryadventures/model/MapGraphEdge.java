package com.actionteam.geometryadventures.model;
/**
 * Created by rka97 on 4/7/2018.
 */

import com.badlogic.gdx.ai.pfa.Connection;

public class MapGraphEdge implements Connection<MapGraphNode> {
    public MapGraphNode fromNode;
    public MapGraphNode toNode;
    public float cost = 1;

    public MapGraphEdge(MapGraphNode start, MapGraphNode end) {
        fromNode = start;
        toNode = end;
    }
    public float getCost()
    {
        return cost;
    }
    public MapGraphNode getToNode()
    {
        return toNode;
    }
    public MapGraphNode getFromNode()
    {
        return fromNode;
    }
    public void setCost(float cost) { this.cost = cost; }
}
