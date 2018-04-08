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
            return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
        }
    }
    public MapGraph mapGraph;
    public IndexedAStarPathFinder<MapGraphNode> pathFinder;
    public DefaultGraphPath<MapGraphNode> graphPath;
    public ManhattanDistanceHeuristic distanceHeuristic;

    public AIUtils(Map map) {
        mapGraph = new MapGraph(map);
        pathFinder = new IndexedAStarPathFinder<MapGraphNode>(mapGraph);
        graphPath = new DefaultGraphPath<MapGraphNode>();
        distanceHeuristic = new ManhattanDistanceHeuristic();
    }

    public float[] calculatePath(int startX, int startY, int endX, int endY)
    {
        MapGraphNode startNode = mapGraph.nodes.get(mapGraph.xyToIndex(startX, startY));
        MapGraphNode endNode = mapGraph.nodes.get(mapGraph.xyToIndex(endX, endY));
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
}
