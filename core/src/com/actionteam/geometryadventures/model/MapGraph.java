package com.actionteam.geometryadventures.model;

/**
 * Created by rka97 on 4/7/2018.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;

import com.badlogic.gdx.utils.Array;

import java.io.Console;
import java.util.ArrayList;

public class MapGraph implements IndexedGraph<MapGraphNode> {
    public ArrayList<MapGraphNode> nodes;
    public Map map;
    private int numNodes;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int sizeX;
    private int sizeY;

    public MapGraph(Map map)
    {
        this.map = map;
        initializeGraph(map);
    }

    public int getIndex(MapGraphNode node)
    {
        return node.index;
    }

    public int getNodeCount()
    {
        return nodes.size();
    }

    public Array<Connection<MapGraphNode>> getConnections(MapGraphNode node)
    {
        return node.edges;
    }


    public int[] indexToXY(int index)
    {
        int[] XY = new int[2];
        XY[0] = index % sizeX + minX;
        XY[1] = (index - XY[0])/sizeX + minY;
        return XY;
    }
    public int xyToIndex(int x, int y) {
        if (x > maxX || x < minX || y > maxY || y < minY)
            return -1;
        return (x - minX) + (y - minY)*sizeX;
    }

    /* This part should be MOVED to the map creator, the result should be exported  *
     * to a file and read from memory.                                              */
    public void initializeGraph(Map map)
    {
        ArrayList<Tile> blockedTiles = map.getBlockedTiles();
        int[] dimensions = map.getMapDimensions();
        minX = dimensions[0];
        maxX = dimensions[1];
        minY = dimensions[2];
        maxY = dimensions[3];
        sizeX = maxX - minX + 1;
        sizeY = maxY - minY + 1;
        numNodes = sizeX*sizeY;
        nodes = new ArrayList<MapGraphNode>(numNodes);
        /* First create all the nodes. */
        for(int i = 0; i < numNodes; i++)
        {
            int[] nodeXY = indexToXY(i);
            nodes.add(new MapGraphNode(nodeXY[0], nodeXY[1], i, 4));
        }
        /* Now create all the edges. */
        for(MapGraphNode node : nodes)
        {
            int nodeIndex = node.getIndex();
            int[] nodeXY = indexToXY(nodeIndex);
            int upNodeIndex = nodeIndex - sizeX;
            if (upNodeIndex >= 0) {
                MapGraphNode upNode = nodes.get(upNodeIndex);
                node.addEdge(new MapGraphEdge(node, upNode));
            }
            int downNodeIndex = nodeIndex + sizeX;
            if (downNodeIndex < numNodes) {
                MapGraphNode downNode = nodes.get(downNodeIndex);
                node.addEdge(new MapGraphEdge(node, downNode));
            }
            int rightNodeIndex = xyToIndex(nodeXY[0] + 1, nodeXY[1]);
            if (rightNodeIndex >= 0) {
                MapGraphNode rightNode = nodes.get(rightNodeIndex);
                node.addEdge(new MapGraphEdge(node, rightNode));
            }
            int leftNodeIndex = xyToIndex(nodeXY[0] - 1, nodeXY[1]);
            if (leftNodeIndex >= 0) {
                MapGraphNode leftNode = nodes.get(leftNodeIndex);
                node.addEdge(new MapGraphEdge(node, leftNode));
            }
        }
        /* Now remove all the blocked edges. */
        for(Tile tile : blockedTiles)
        {
            int tileX = (int)tile.x;
            int tileY = (int)tile.y;
            for(MapGraphNode node : nodes)
            {
                /* Does this node belong to the blocked tile? */
                if (node.x == tileX && node.y == tileY && node.edges.size > 0)
                {
                    node.edges.removeRange(0, node.edges.size - 1);
                }
                /* Are any of this node's neighbor's the blocked tile? */
                for (int i = 0; i < node.edges.size; i++)
                {
                    MapGraphNode neighbor = node.edges.get(i).getToNode();
                    if (neighbor.x == tile.x && neighbor.y == tile.y)
                        node.edges.removeIndex(i);
                }
            }
        }
    }
}
