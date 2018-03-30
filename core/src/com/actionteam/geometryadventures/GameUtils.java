package com.actionteam.geometryadventures;

import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.ControlComponent;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSManager;
import com.actionteam.geometryadventures.model.Map;
import com.actionteam.geometryadventures.model.Tile;
import com.actionteam.geometryadventures.systems.CollisionSystem;
import com.actionteam.geometryadventures.systems.ControlSystem;
import com.actionteam.geometryadventures.systems.GraphicsSystem;
import com.actionteam.geometryadventures.systems.HudSystem;
import com.actionteam.geometryadventures.systems.PhysicsSystem;
import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by theartful on 3/27/18.
 */

public abstract class GameUtils {
    // used for txt files
    public abstract InputStream openFile(String fileName) throws IOException;
    public abstract File getFile(String fileName);

    private Map loadMap(String levelName){
        try {
            InputStream fis = openFile(levelName);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
                sb.append('\n');
            }
            Gson gson = new Gson();
            return gson.fromJson(sb.toString(), Map.class);
        } catch (FileNotFoundException e) {
            Gdx.app.log("Error in LevelLoader", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Gdx.app.log("Error in LevelLoader", "IO Exception");
            e.printStackTrace();
        }
        return null;
    }

    public ECSManager loadLevel(String levelName){
        Map map = loadMap(levelName);
        ECSManager ecsManager = new ECSManager();

        for(Tile floorTile : map.getFloorTiles()){
            int entity = ecsManager.createEntity();
            PhysicsComponent physicsComponent = new PhysicsComponent();
            GraphicsComponent graphicsComponent = new GraphicsComponent();

            physicsComponent.position.set(floorTile.x, floorTile.y);
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
            graphicsComponent.textureName = wallTile.textureName;
            graphicsComponent.textureIndex = wallTile.textureIndex;
            collisionComponent.shapeType = CollisionComponent.RECTANGLE;
            collisionComponent.width = 1;
            collisionComponent.height = 1;
            collisionComponent.id = 0;
            collisionComponent.mask = ~0;
            //TODO: Collision component needs id and mask

            ecsManager.addComponent(physicsComponent, entity);
            ecsManager.addComponent(graphicsComponent, entity);
            ecsManager.addComponent(collisionComponent, entity);
        }

        // temporary
        int entity = ecsManager.createEntity();
        GraphicsComponent gc = new GraphicsComponent();
        gc.textureName = "man";
        gc.textureIndex = -1;
        gc.height = 1;
        gc.width = 1;
        PhysicsComponent pc = new PhysicsComponent();
        ControlComponent cc = new ControlComponent();
        CollisionComponent col = new CollisionComponent();
        col.width = 0.7f;
        col.height = 0.7f;
        col.shapeType = CollisionComponent.RECTANGLE;
        col.id = 0;
        col.mask = ~0;
        ecsManager.addComponent(pc, entity);
        ecsManager.addComponent(cc, entity);
        ecsManager.addComponent(gc, entity);
        ecsManager.addComponent(col, entity);

        // create systems
        GraphicsSystem graphicsSystem = new GraphicsSystem(this);
        PhysicsSystem physicsSystem = new PhysicsSystem();
        ControlSystem controlSystem = new ControlSystem();
        HudSystem hudSystem = new HudSystem();
        CollisionSystem collisionSystem = new CollisionSystem();
        Gdx.input.setInputProcessor(controlSystem);

        ecsManager.addSystem(graphicsSystem);
        ecsManager.addSystem(physicsSystem);
        ecsManager.addSystem(controlSystem);
        ecsManager.addSystem(hudSystem);
        ecsManager.addSystem(collisionSystem);

        return ecsManager;
    }
}
