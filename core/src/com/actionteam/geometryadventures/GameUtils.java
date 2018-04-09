package com.actionteam.geometryadventures;

import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.ControlComponent;
import com.actionteam.geometryadventures.components.EnemyComponent;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.components.WeaponComponent;
import com.actionteam.geometryadventures.components.WeaponFactory;
import com.actionteam.geometryadventures.ecs.ECSManager;
import com.actionteam.geometryadventures.model.Map;
import com.actionteam.geometryadventures.model.MapGraph;
import com.actionteam.geometryadventures.model.MapGraphNode;
import com.actionteam.geometryadventures.model.Tile;
import com.actionteam.geometryadventures.systems.CollisionSystem;
import com.actionteam.geometryadventures.systems.ControlSystem;
import com.actionteam.geometryadventures.systems.EnemySystem;
import com.actionteam.geometryadventures.systems.GraphicsSystem;
import com.actionteam.geometryadventures.systems.HudSystem;
import com.actionteam.geometryadventures.systems.LifetimeSystem;
import com.actionteam.geometryadventures.systems.PhysicsSystem;
import com.actionteam.geometryadventures.systems.VisionSystem;
import com.actionteam.geometryadventures.systems.WeaponSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
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
    public static AIUtils aiUtils;
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
        aiUtils = new AIUtils(map);
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
            collisionComponent.width = 0.9f;
            collisionComponent.height = 0.9f;
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

        WeaponComponent wc = WeaponFactory.createWeapon(WeaponComponent.RIOT_GUN);

        ecsManager.addComponent(pc, entity);
        ecsManager.addComponent(cc, entity);
        ecsManager.addComponent(gc, entity);
        ecsManager.addComponent(col, entity);
        ecsManager.addComponent(wc, entity);

        for(int i = 0; i < 1; i++)
        {
            // temporary, for enemy creation.
            int enemyEntity = ecsManager.createEntity();
            GraphicsComponent enemyGC = new GraphicsComponent();
            enemyGC.textureName = "man";
            enemyGC.textureIndex = -1;
            enemyGC.height = 1;
            enemyGC.width = 1;
            CollisionComponent enemyCC = new CollisionComponent();
            enemyCC.shapeType = CollisionComponent.RECTANGLE;
            enemyCC.width = 0.7f;
            enemyCC.height = 0.7f;
            enemyCC.radius = 0.7f;
            enemyCC.id = 0;
            enemyCC.mask = ~0;
            PhysicsComponent enemyPC = new PhysicsComponent();
            enemyPC.position.x = 5.01f;
            enemyPC.position.y = 5.01f;

            EnemyComponent enemyComponent = new EnemyComponent();
            // The enemy's path. Automate this!!
            Float[] v1 = new Float[] {5.0f, 5.0f, 0.0f, 3.0f};
            Float[] v2 = new Float[] {5.0f, 8.0f, 30.0f, 2.0f};
            Float[] v3 = new Float[] {3.0f, 7.0f, -10.0f, 1.0f};
            enemyComponent.pathPoints.add(v1);
            enemyComponent.pathPoints.add(v2);
            enemyComponent.pathPoints.add(v3);
            enemyComponent.remainingTime = (enemyComponent.pathPoints.get(0))[3].floatValue();
            ecsManager.addComponent(enemyPC, enemyEntity);
            ecsManager.addComponent(enemyGC, enemyEntity);
            ecsManager.addComponent(enemyCC, enemyEntity);
            ecsManager.addComponent(enemyComponent, enemyEntity);
        }

        // create systems
        GraphicsSystem graphicsSystem = new GraphicsSystem(this);
        PhysicsSystem physicsSystem = new PhysicsSystem();
        ControlSystem controlSystem = new ControlSystem();
        HudSystem hudSystem = new HudSystem();
        CollisionSystem collisionSystem = new CollisionSystem();
        Gdx.input.setInputProcessor(controlSystem);

        WeaponSystem weaponSystem = new WeaponSystem();
        LifetimeSystem lifetimeSystem = new LifetimeSystem();
        EnemySystem enemySystem = new EnemySystem();
        VisionSystem visionSystem = new VisionSystem();

        ecsManager.addSystem(graphicsSystem);
        ecsManager.addSystem(physicsSystem);
        ecsManager.addSystem(controlSystem);
        ecsManager.addSystem(hudSystem);
        ecsManager.addSystem(collisionSystem);
        ecsManager.addSystem(enemySystem);
        ecsManager.addSystem(weaponSystem);
        ecsManager.addSystem(lifetimeSystem);
        ecsManager.addSystem(visionSystem);
        return ecsManager;
    }
}
