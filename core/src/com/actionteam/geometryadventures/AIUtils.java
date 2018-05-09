package com.actionteam.geometryadventures;

import com.actionteam.geometryadventures.model.Map;
import com.actionteam.geometryadventures.model.MapGraph;
import com.actionteam.geometryadventures.model.MapGraphNode;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;

import static java.lang.Math.abs;

/**
 * Created by rka97 on 4/7/2018.
 */

public class AIUtils {
    /**
     *  Implements the Manhattan or Taxicab Distance Heuristic.
     */
    private class ManhattanDistanceHeuristic implements Heuristic<MapGraphNode> {
        @Override
        public float estimate(MapGraphNode node, MapGraphNode endNode) {
            float deltaX = endNode.x - node.x;
            float deltaY = endNode.y - node.y;
            return abs(deltaX) + abs(deltaY);
        }
    }
    /**
     *  Implements the Euclidean Heuristic.
     */
    private class EuclideanDistanceHeurestic implements Heuristic<MapGraphNode> {
        @Override
        public float estimate(MapGraphNode node, MapGraphNode endNode) {
            return (endNode.x - node.x)*(endNode.x - node.x) + (endNode.y - node.y)*(endNode.y - node.y);
        }
    }
    public MapGraph mapGraph;
    public IndexedAStarPathFinder<MapGraphNode> pathFinder;
    public DefaultGraphPath<MapGraphNode> graphPath;
    public Heuristic<MapGraphNode> distanceHeuristic;

    /**
     *  Constructs AIUtils, its mapGraph and pathfinding mechanism.
     *  @param map The map from which the graph is constructed.
     */
    public AIUtils(Map map) {
        mapGraph = new MapGraph(map);
        pathFinder = new IndexedAStarPathFinder<MapGraphNode>(mapGraph);
        graphPath = new DefaultGraphPath<MapGraphNode>();
        distanceHeuristic = new ManhattanDistanceHeuristic();
    }

    /**
     *  Checks if node (x, y) can be visited.
     *  @param x the x-coordinate.
     *  @param y the y-coordinate.
     */
    public boolean checkNodeIsVisitable(float x, float y)
    {
        int ix = (int)Math.floor(x);
        int iy = (int)Math.floor(y);
        MapGraphNode node = mapGraph.nodes.get(mapGraph.xyToIndex(ix, iy));
        return !(node.edges.size == 0);
    }
    /**
     *  Checks if the line between start and end points is blocked by anything using a Bresenham-like algorithm.
     *  @param start the start point.
     *  @param end the end point.
     */
    public boolean checkLineSegmentCollision(Vector2 start, Vector2 end)
    {
        int j = 0;
        if (Math.floor(end.x) == Math.floor(start.x))
        {
            if(end.y == start.y)
            {
                return true;
            }
            else
            {
                int ymin = (int)Math.min(Math.floor(start.y), Math.floor(end.y));
                int ymax = (int)Math.max(Math.floor(start.y), Math.floor(end.y));
                int x = (int)Math.floor(start.x);
                for(int i = ymin; i <= ymax; i++)
                {
                    MapGraphNode node = mapGraph.nodes.get(mapGraph.xyToIndex(x, i));
                    if (node.edges.size == 0)
                        return true;
                }
            }
        }
        else
        {
            float m = (end.y - start.y)/(end.x - start.x);
            float c = start.y - m * start.x;
            if(Math.abs(m) < 1)
            {
                int xmin = (int)Math.min(Math.floor(start.x), Math.floor(end.x));
                int xmax = (int)Math.max(Math.floor(start.x), Math.floor(end.x));
                for(int i = xmin; i <= xmax; i++)
                {
                    int yi = (int)Math.floor(m*i + c + 0.5);
                    int index = mapGraph.xyToIndex(i, yi);
                    if (index == -1)
                        continue;
                    MapGraphNode node = mapGraph.nodes.get(index);
                    if (node.edges.size == 0)
                        return true;
                }
            }
            else
            {
                int ymin = (int)Math.min(Math.floor(start.y), Math.floor(end.y));
                int ymax = (int)Math.max(Math.floor(start.y), Math.floor(end.y));
                for(int y = ymin; y <= ymax; y++)
                {
                    // y = mx + c, x = (y-c)/
                    int x = (int)Math.floor((y - c)/m + 0.5);
                    int index = mapGraph.xyToIndex(x, y);
                    if (index == -1)
                        continue;
                    MapGraphNode node = mapGraph.nodes.get(index);
                    if (node.edges.size == 0)
                        return true;
                }
            }
        }
        return false;
    }
    /**
     *  Finds the path between two points.
     *  @param startX the starting x-coordinate.
     *  @param startY the starting y-coordinate.
     */
    public float[] calculatePath(int startX, int startY, int endX, int endY)
    {
        MapGraphNode startNode = mapGraph.nodes.get(mapGraph.xyToIndex(startX, startY));
        MapGraphNode endNode = mapGraph.nodes.get(mapGraph.xyToIndex(endX, endY));
        if (startNode.edges.size == 0 || endNode.edges.size == 0) {
            //Gdx.app.log("Path", "We should NEVER be here.");
            return new float[]{(float) startNode.x, (float) startNode.y};
        }
        graphPath.clear();
        pathFinder.searchNodePath(startNode, endNode, distanceHeuristic, graphPath);
        /*
        for(MapGraphNode node : graphPath.nodes)
        {
            // Gdx.app.log("Path", "(" + node.x + ", " + node.y + ").");
        }
        */
        MapGraphNode nextNode = (graphPath.nodes.size > 1) ? graphPath.get(1) : endNode;
        float[] nextNodePosition = new float[] {(float)nextNode.x, (float)nextNode.y};
        return nextNodePosition;
    }

    /**
     *  Gets node XY position from two points in the real map.
     *  @param x the x-coordinate.
     *  @param x they-coordinate.
     */
    public Vector2 getNodeXYFromPosition(float x, float y)
    {
        return new Vector2( (float)Math.floor(x), (float)Math.floor(y) );
    }
}
