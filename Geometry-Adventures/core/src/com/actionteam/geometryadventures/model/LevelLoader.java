package com.actionteam.geometryadventures.model;

import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSManager;
import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by theartful on 3/27/18.
 */

public class LevelLoader {

    private static Map loadMap(String levelName){
        try {
            File file = Gdx.files.internal(levelName).file();
            if(!file.exists()) throw new FileNotFoundException();
            Gson gson = new Gson();
            return gson.fromJson(new FileReader(file), Map.class);
        } catch (FileNotFoundException e) {
            Gdx.app.log("Error in Level Loader", "File not found");
        }
        return null;
    }

    public static ECSManager loadLevel(String levelName){
        Map map = loadMap(levelName);
        ECSManager ecsManager = new ECSManager();

        for(Tile floorTile : map.getFloorTiles()){
            int entity = ecsManager.createEntity();
            PhysicsComponent physicsComponent = new PhysicsComponent();
            GraphicsComponent graphicsComponent = new GraphicsComponent();

            physicsComponent.position.set(floorTile.x, floorTile.y);
            graphicsComponent.width = 1;
            graphicsComponent.height = 1;
            graphicsComponent.textureName = floorTile.textureName;
            graphicsComponent.textureIndex = floorTile.textureIndex;

            ecsManager.addComponent(physicsComponent, entity);
            ecsManager.addComponent(graphicsComponent, entity);
        }

        for(Tile wallTile : map.getWallTiles()){
            int entity = ecsManager.createEntity();
            PhysicsComponent physicsComponent = new PhysicsComponent();
            GraphicsComponent graphicsComponent = new GraphicsComponent();
            CollisionComponent collisionComponent = new CollisionComponent();

            physicsComponent.position.set(wallTile.x, wallTile.y);
            graphicsComponent.width = 1;
            graphicsComponent.height = 1;
            graphicsComponent.textureName = wallTile.textureName;
            graphicsComponent.textureIndex = wallTile.textureIndex;
            collisionComponent.shapeType = CollisionComponent.RECTANGLE;
            collisionComponent.width = 1;
            collisionComponent.height = 1;

            //TODO: Collision component needs id and mask

            ecsManager.addComponent(physicsComponent, entity);
            ecsManager.addComponent(graphicsComponent, entity);
            ecsManager.addComponent(collisionComponent, entity);
        }

        return ecsManager;
    }


}
