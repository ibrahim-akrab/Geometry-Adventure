package com.actionteam.geometryadventures;

import com.actionteam.geometryadventures.model.Map;
import com.actionteam.geometryadventures.model.MapGraph;
import com.actionteam.geometryadventures.model.MapGraphNode;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by rka97 on 4/7/2018.
 */

public class AIUtils {
    private class ManhattanDistanceHeuristic implements Heuristic<MapGraphNode> {
        @Override
        public float estimate(MapGraphNode node, MapGraphNode endNode) {
            float deltaX = endNode.x - node.x;
            float deltaY = endNode.y - node.y;
            return Math.abs(deltaX) + Math.abs(deltaY);
        }
    }
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

    public AIUtils(Map map) {
        mapGraph = new MapGraph(map);
        pathFinder = new IndexedAStarPathFinder<MapGraphNode>(mapGraph);
        graphPath = new DefaultGraphPath<MapGraphNode>();
        distanceHeuristic = new ManhattanDistanceHeuristic();
    }

    public boolean checkLineSegmentCollision(Vector2 start, Vector2 end)
    {
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
                for(int i = ymin; i < ymax; i++)
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
            int xmin = (int)Math.min(Math.floor(start.x), Math.floor(end.x));
            int xmax = (int)Math.max(Math.floor(start.x), Math.floor(end.x));
            for(int i = xmin; i < xmax; i++)
            {
                int yi = (int)Math.floor(m*i + c);
                MapGraphNode node = mapGraph.nodes.get(mapGraph.xyToIndex(i, yi));
                if (node.edges.size == 0)
                    return true;
            }
        }
        return false;
    }

    public float[] calculatePath(int startX, int startY, int endX, int endY)
    {
        MapGraphNode startNode = mapGraph.nodes.get(mapGraph.xyToIndex(startX, startY));
        MapGraphNode endNode = mapGraph.nodes.get(mapGraph.xyToIndex(endX, endY));
        if (startNode.edges.size == 0 || endNode.edges.size == 0) {
            Gdx.app.log("Path", "We should NEVER be here.");
            return new float[]{(float) startNode.x, (float) startNode.y};
        }
        graphPath.clear();
        pathFinder.searchNodePath(startNode, endNode, distanceHeuristic, graphPath);
        for(MapGraphNode node : graphPath.nodes)
        {
            Gdx.app.log("Path", "(" + node.x + ", " + node.y + ").");
        }
        MapGraphNode nextNode = (graphPath.nodes.size > 1) ? graphPath.get(1) : endNode;
        float[] nextNodePosition = new float[] {(float)nextNode.x, (float)nextNode.y};
        return nextNodePosition;
    }

    public Vector2 getNodeXYFromPosition(float x, float y)
    {
        return new Vector2( (float)Math.floor(x), (float)Math.floor(y) );
    }
}
