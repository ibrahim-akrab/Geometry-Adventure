package com.actionteam.geometryadventures;

import com.actionteam.geometryadventures.components.CacheComponent;
import com.actionteam.geometryadventures.components.CollectibleComponent;
import com.actionteam.geometryadventures.components.CollectorComponent;
import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.ControlComponent;
import com.actionteam.geometryadventures.components.EnemyComponent;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.HealthComponent;
import com.actionteam.geometryadventures.components.LightComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.components.ScoreComponent;
import com.actionteam.geometryadventures.components.PortalComponent;
import com.actionteam.geometryadventures.components.WeaponComponent;
import com.actionteam.geometryadventures.ecs.ECSEvent;
import com.actionteam.geometryadventures.ecs.ECSManager;
import com.actionteam.geometryadventures.entities.Entities;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.actionteam.geometryadventures.model.CollectibleTile;
import com.actionteam.geometryadventures.model.EnemyTile;
import com.actionteam.geometryadventures.model.LightTile;
import com.actionteam.geometryadventures.model.Map;
import com.actionteam.geometryadventures.model.PlayerTile;
import com.actionteam.geometryadventures.model.PortalTile;
import com.actionteam.geometryadventures.model.Tile;
import com.actionteam.geometryadventures.systems.CacheSystem;
import com.actionteam.geometryadventures.systems.ClockSystem;
import com.actionteam.geometryadventures.systems.CollectionSystem;
import com.actionteam.geometryadventures.systems.CollisionSystem;
import com.actionteam.geometryadventures.systems.ControlSystem;
import com.actionteam.geometryadventures.systems.EnemySystem;
import com.actionteam.geometryadventures.systems.GraphicsSystem;
import com.actionteam.geometryadventures.systems.HealthSystem;
import com.actionteam.geometryadventures.systems.HudSystem;
import com.actionteam.geometryadventures.systems.LifetimeSystem;
import com.actionteam.geometryadventures.systems.LightSystem;
import com.actionteam.geometryadventures.systems.PhysicsSystem;
import com.actionteam.geometryadventures.systems.ScoreSystem;
import com.actionteam.geometryadventures.systems.SoundSystem;
import com.actionteam.geometryadventures.systems.VisionSystem;
import com.actionteam.geometryadventures.systems.WeaponSystem;
import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by theartful on 3/27/18.
 */

//TODO ADD TILES FOR PORTALS.

public abstract class GameUtils {
    public static AIUtils aiUtils;

    // used for txt files
    public abstract InputStream openFile(String fileName) throws IOException;

    public abstract File getFile(String fileName);

    private Map map;
    private static ECSManager ecsManager;
    private float initialPlayerX;
    private float initialPlayerY;

    private Map loadMap(String levelName) {
        try {
            InputStream fis = openFile(levelName);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

            RuntimeTypeAdapterFactory<Tile> rtaf = RuntimeTypeAdapterFactory.of(Tile.class, "type").
                    registerSubtype(Tile.class).registerSubtype(PortalTile.class).
                    registerSubtype(EnemyTile.class).registerSubtype(PlayerTile.class).
                    registerSubtype(LightTile.class).registerSubtype(CollectibleTile.class);

            Gson gson = new GsonBuilder().registerTypeAdapterFactory(rtaf).create();
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


    public void loadLevel(String levelName) throws Exception {
        map = loadMap(levelName);
        map.updateTiles();
        aiUtils = new AIUtils(map);
        ecsManager = ECSManager.getInstance();

        initFloorTiles(map.getFloorTiles());
        initWallTiles(map.getWallTiles());
        initMiscTiles(map.getMiscTiles());
        initEnemyTiles(map.getEnemyTiles());
        initPortalTiles(map.getPortalTiles());
        initPlayerTile(map.getPlayerTile());
        initLightTiles(map.getLightTiles());
        initCollectableTile(map.getCollectibleTiles());

        // create systems
        GraphicsSystem graphicsSystem = new GraphicsSystem(this);
        PhysicsSystem physicsSystem = new PhysicsSystem();
        ControlSystem controlSystem = new ControlSystem();
        HudSystem hudSystem = new HudSystem();
        CollisionSystem collisionSystem = new CollisionSystem();
        LightSystem lightSystem = new LightSystem(this);
        WeaponSystem weaponSystem = new WeaponSystem();
        LifetimeSystem lifetimeSystem = new LifetimeSystem();
        EnemySystem enemySystem = new EnemySystem();
        VisionSystem visionSystem = new VisionSystem();
        SoundSystem soundSystem = new SoundSystem();
        HealthSystem healthSystem = new HealthSystem();
        ScoreSystem scoreSystem = new ScoreSystem();
        ClockSystem clockSystem = new ClockSystem();
        CollectionSystem collectionSystem = new CollectionSystem();
        CacheSystem cacheSystem = new CacheSystem(initialPlayerX, initialPlayerY);
        hudSystem.setTextureAtlas(graphicsSystem.getTextureAtlas());
        Gdx.input.setInputProcessor(controlSystem);

        lightSystem.setAmbientIntensity(map.getConfig().ambientIntensity);
        lightSystem.setAmbientLight(map.getConfig().ambientLight);

        ecsManager.addSystem(graphicsSystem);
        ecsManager.addSystem(physicsSystem);
        ecsManager.addSystem(controlSystem);
        ecsManager.addSystem(hudSystem);
        ecsManager.addSystem(collisionSystem);
        ecsManager.addSystem(enemySystem);
        ecsManager.addSystem(weaponSystem);
        ecsManager.addSystem(lifetimeSystem);
        ecsManager.addSystem(visionSystem);
        ecsManager.addSystem(soundSystem);
        ecsManager.addSystem(healthSystem);
        ecsManager.addSystem(scoreSystem);
        ecsManager.addSystem(collectionSystem);
        ecsManager.addSystem(lightSystem);
        ecsManager.addSystem(clockSystem);
        ecsManager.addSystem(cacheSystem);
        graphicsSystem.setLightSystem(lightSystem);
        ecsManager.fireEvent(new ECSEvent(ECSEvents.LEVEL_STARTED, null));
    }

    private void initLightTiles(List<LightTile> lightTiles) {
        for (LightTile lightTile : lightTiles) {
            int entity = ecsManager.createEntity();
            PhysicsComponent physicsComponent = new PhysicsComponent();
            GraphicsComponent graphicsComponent = new GraphicsComponent();
            LightComponent lightComponent = new LightComponent();
            physicsComponent.position.set(lightTile.x, lightTile.y);
            graphicsComponent.textureName = lightTile.textureName;
            graphicsComponent.textureIndex = lightTile.textureIndex;
            graphicsComponent.isAnimated = lightTile.isAnimated;
            graphicsComponent.frames = lightTile.frames;
            graphicsComponent.interval = lightTile.speed;
            lightComponent.lightIntensity = lightTile.lightIntensity;
            lightComponent.radius = lightTile.innerRadius;
            if (lightTile.collidable) {
                CollisionComponent collisionComponent =
                        new CollisionComponent(Entities.PLAYER_COLLISION_ID,
                                Entities.ENEMY_COLLISION_ID);
                collisionComponent.shapeType = CollisionComponent.RECTANGLE;
                collisionComponent.width = 0.9f;
                collisionComponent.height = 0.9f;
                collisionComponent.id = Entities.ENVIRONMENT_COLLISION_ID;
                ecsManager.addComponent(collisionComponent, entity);
            }
            ecsManager.addComponent(physicsComponent, entity);
            ecsManager.addComponent(graphicsComponent, entity);
            ecsManager.addComponent(lightComponent, entity);
            ecsManager.addComponent(new CacheComponent(), entity);
        }
    }

    private void initPlayerTile(PlayerTile playerTile) {
        int entity = ecsManager.createEntity();
        GraphicsComponent gc = new GraphicsComponent();
        gc.textureName = "player";
        gc.textureIndex = 1;
        gc.isAnimated = false;
        gc.interval = 200;
        gc.height = 1.4f;
        gc.width = 1.4f;
        gc.offsetX = -0.2f;
        gc.offsetY = -0.2f;
        PhysicsComponent pc = new PhysicsComponent();
        ControlComponent cc = new ControlComponent();
        CollisionComponent col = new CollisionComponent();
        LightComponent lc = new LightComponent();
        col.width = 0.7f;
        col.height = 0.7f;
        col.shapeType = CollisionComponent.RECTANGLE;
        col.id = Entities.PLAYER_COLLISION_ID;
        col.mask = ~0;
        pc.position.set(playerTile.x, playerTile.y);
        initialPlayerX = playerTile.x;
        initialPlayerY = playerTile.y;
        lc.lightIntensity = 0.7f;
        lc.radius = 10.f;

        WeaponComponent wc = WeaponFactory.createWeapon(WeaponComponent.HAND_GUN);
        ScoreComponent sc = new ScoreComponent();
        HealthComponent healthComponent = new HealthComponent();
        healthComponent.health = playerTile.health;

        ecsManager.addComponent(pc, entity);
        ecsManager.addComponent(cc, entity);
        ecsManager.addComponent(gc, entity);
        ecsManager.addComponent(col, entity);
        ecsManager.addComponent(wc, entity);
        ecsManager.addComponent(sc, entity);
        ecsManager.addComponent(lc, entity);
        ecsManager.addComponent(healthComponent, entity);
        ecsManager.addComponent(new CacheComponent(), entity);
        ecsManager.addComponent(new CollectorComponent(), entity);
    }

    private void initPortalTiles(List<PortalTile> portalTiles) {
        for (PortalTile portalTile : portalTiles) {
            int entity = ecsManager.createEntity();
            PhysicsComponent physicsComponent = new PhysicsComponent();
            GraphicsComponent graphicsComponent = new GraphicsComponent();
            CollisionComponent collisionComponent = new CollisionComponent(Entities.PLAYER_COLLISION_ID);
            PortalComponent portalComponent = new PortalComponent();
            portalComponent.position.x = portalTile.toX;
            portalComponent.position.y = portalTile.toY;
            physicsComponent.position.set(portalTile.x, portalTile.y);

            graphicsComponent.textureName = portalTile.textureName;
            graphicsComponent.textureIndex = portalTile.textureIndex;
            graphicsComponent.isAnimated = portalTile.isAnimated;
            graphicsComponent.frames = portalTile.frames;
            graphicsComponent.interval = portalTile.speed;

            collisionComponent.shapeType = CollisionComponent.RECTANGLE;
            collisionComponent.height = 1;
            collisionComponent.width = 1;
            collisionComponent.id = Entities.ENVIRONMENT_COLLISION_ID;

            ecsManager.addComponent(physicsComponent, entity);
            ecsManager.addComponent(graphicsComponent, entity);
            ecsManager.addComponent(collisionComponent, entity);
            ecsManager.addComponent(portalComponent, entity);
            ecsManager.addComponent(new CacheComponent(), entity);
        }
    }

    private void initEnemyTiles(List<EnemyTile> enemyTiles) {
        for (EnemyTile enemyTile : enemyTiles) {
            // temporary, for enemy creation.
            int enemyEntity = ecsManager.createEntity();
            GraphicsComponent enemyGC = new GraphicsComponent();
            if (enemyTile.subtype.equals("green orc")) {
                enemyGC.textureName = "greenorc";
            } else if (enemyTile.subtype.equals("skeleton")) {
                enemyGC.textureName = "skeleton";
            } else {
                enemyGC.textureName = "greenorc";
            }
            enemyGC.textureIndex = 0;
            enemyGC.height = 1.7f;
            enemyGC.width = 1.7f;
            enemyGC.offsetX = -0.35f;
            enemyGC.offsetY = -0.35f;
            CollisionComponent enemyCC = new CollisionComponent();
            enemyCC.shapeType = CollisionComponent.RECTANGLE;
            enemyCC.width = 0.8f;
            enemyCC.height = 0.8f;
            enemyCC.radius = 0.8f;
            enemyCC.id = Entities.ENEMY_COLLISION_ID;
            enemyCC.mask = ~(1L << Entities.ENEMY_COLLISION_ID | 1L << Entities.COLLECTABLE_COLLISION_ID);
            PhysicsComponent enemyPC = new PhysicsComponent();
            enemyPC.position.x = enemyTile.x;
            enemyPC.position.y = enemyTile.y;
            HealthComponent enemyHC = new HealthComponent();
            enemyHC.health = enemyTile.health;
            /* Add enemy weapon here */
            WeaponComponent enemyWeapon;
            if (enemyTile.subtype.equals("green orc")) {
                enemyWeapon = WeaponFactory.createWeapon(WeaponComponent.HAND_GUN);
            } else if (enemyTile.subtype.equals("skeleton")) {
                enemyWeapon = WeaponFactory.createWeapon(WeaponComponent.MELEE);
            }
            else {
                enemyWeapon = WeaponFactory.createWeapon(WeaponComponent.HAND_GUN);
            }
            EnemyComponent enemyComponent = new EnemyComponent();
            ecsManager.addComponent(enemyPC, enemyEntity);
            ecsManager.addComponent(enemyGC, enemyEntity);
            ecsManager.addComponent(enemyCC, enemyEntity);
            ecsManager.addComponent(enemyHC, enemyEntity);
            ecsManager.addComponent(enemyWeapon, enemyEntity);
            ecsManager.addComponent(enemyComponent, enemyEntity);
            ecsManager.addComponent(new CacheComponent(), enemyEntity);
        }
    }

    private void initWallTiles(List<Tile> wallTiles) {

        for (Tile wallTile : wallTiles) {
            int entity = ecsManager.createEntity();
            PhysicsComponent physicsComponent = new PhysicsComponent();
            GraphicsComponent graphicsComponent = new GraphicsComponent();
            CollisionComponent collisionComponent = new CollisionComponent();

            physicsComponent.position.set(wallTile.x, wallTile.y);
            graphicsComponent.textureName = wallTile.textureName;
            graphicsComponent.textureIndex = wallTile.textureIndex;
            graphicsComponent.isAnimated = wallTile.isAnimated;
            graphicsComponent.frames = wallTile.frames;
            graphicsComponent.width = 1;
            graphicsComponent.height = 1;
            graphicsComponent.interval = wallTile.speed;

            collisionComponent.shapeType = CollisionComponent.RECTANGLE;
            collisionComponent.width = 0.9f;
            collisionComponent.height = 0.9f;
            collisionComponent.id = Entities.ENVIRONMENT_COLLISION_ID;
            collisionComponent.mask = ~0;
            //TODO: Collision component needs id and mask

            ecsManager.addComponent(physicsComponent, entity);
            ecsManager.addComponent(graphicsComponent, entity);
            ecsManager.addComponent(collisionComponent, entity);
            ecsManager.addComponent(new CacheComponent(), entity);
        }
    }

    private void initFloorTiles(List<Tile> floorTiles) {
        for (Tile floorTile : floorTiles) {
            int entity = ecsManager.createEntity();
            PhysicsComponent physicsComponent = new PhysicsComponent();
            GraphicsComponent graphicsComponent = new GraphicsComponent();

            physicsComponent.position.set(floorTile.x, floorTile.y);
            graphicsComponent.textureName = floorTile.textureName;
            graphicsComponent.textureIndex = floorTile.textureIndex;
            graphicsComponent.isAnimated = floorTile.isAnimated;
            graphicsComponent.frames = floorTile.frames;
            graphicsComponent.interval = floorTile.speed;

            ecsManager.addComponent(physicsComponent, entity);
            ecsManager.addComponent(graphicsComponent, entity);
            ecsManager.addComponent(new CacheComponent(), entity);
        }
    }


    private void initMiscTiles(List<Tile> miscTiles) {
        for (Tile miscTile : miscTiles) {
            int entity = ecsManager.createEntity();
            PhysicsComponent physicsComponent = new PhysicsComponent();
            GraphicsComponent graphicsComponent = new GraphicsComponent();
            physicsComponent.position.set(miscTile.x, miscTile.y);
            graphicsComponent.textureName = miscTile.textureName;
            graphicsComponent.textureIndex = miscTile.textureIndex;
            graphicsComponent.isAnimated = miscTile.isAnimated;
            graphicsComponent.frames = miscTile.frames;
            graphicsComponent.interval = miscTile.speed;
            if(miscTile.textureName.equals("endportal"))
            {
                CollisionComponent cc = new CollisionComponent(Entities.PLAYER_COLLISION_ID);
                cc.radius = 0.7f;
                cc.height = 0.7f;
                cc.width = 0.7f;
                cc.shapeType = CollisionComponent.RECTANGLE;
                cc.id = Entities.END_PORTAL_COLLISION_ID;
                ecsManager.addComponent(cc, entity);
            }

            ecsManager.addComponent(physicsComponent, entity);
            ecsManager.addComponent(graphicsComponent, entity);
            ecsManager.addComponent(new CacheComponent(), entity);
        }
    }

    private void initCollectableTile(List<CollectibleTile> collectibleTiles) {
        for (CollectibleTile collectibleTile : collectibleTiles) {
            int entity = ecsManager.createEntity();
            PhysicsComponent physicsComponent = new PhysicsComponent();
            CollisionComponent collisionComponent = new CollisionComponent(Entities.PLAYER_COLLISION_ID);
            GraphicsComponent graphicsComponent = new GraphicsComponent();
            CollectibleComponent collectibleComponent = new CollectibleComponent();

            physicsComponent.position.set(collectibleTile.x, collectibleTile.y);
            graphicsComponent.textureIndex = collectibleTile.textureIndex;
            graphicsComponent.textureName = collectibleTile.textureName;
            graphicsComponent.isAnimated = collectibleTile.isAnimated;
            graphicsComponent.frames = collectibleTile.frames;
            graphicsComponent.interval = collectibleTile.speed;
            collisionComponent.height = 0.8f;
            collisionComponent.width = 0.8f;
            collisionComponent.radius = 0.8f;
            collisionComponent.id = Entities.COLLECTABLE_COLLISION_ID;
            collisionComponent.shapeType = CollisionComponent.RECTANGLE;
            if (collectibleTile.subtype.equals("heart"))
                collectibleComponent.type = CollectibleComponent.HEART;
            else if (collectibleTile.subtype.equals("coin"))
                collectibleComponent.type = CollectibleComponent.COIN;
            else
                collectibleComponent.type = CollectibleComponent.KEY;
            collectibleComponent.value = 1;
            ecsManager.addComponent(physicsComponent, entity);
            ecsManager.addComponent(graphicsComponent, entity);
            ecsManager.addComponent(collisionComponent, entity);
            ecsManager.addComponent(collectibleComponent, entity);
            ecsManager.addComponent(new CacheComponent(), entity);
        }
    }

    public static void createStandardCoin(float x, float y) {
        int entity = ecsManager.createEntity();
        PhysicsComponent physicsComponent = new PhysicsComponent();
        CollisionComponent collisionComponent = new CollisionComponent(Entities.PLAYER_COLLISION_ID);
        GraphicsComponent graphicsComponent = new GraphicsComponent();
        CollectibleComponent collectibleComponent = new CollectibleComponent();

        physicsComponent.position.set(x, y);
        graphicsComponent.textureIndex = 0;
        graphicsComponent.textureName = "coin";
        graphicsComponent.isAnimated = true;
        graphicsComponent.frames = 9;
        graphicsComponent.interval = 150;
        collisionComponent.height = 0.8f;
        collisionComponent.width = 0.8f;
        collisionComponent.radius = 0.8f;
        collisionComponent.id = Entities.COLLECTABLE_COLLISION_ID;
        collisionComponent.shapeType = CollisionComponent.RECTANGLE;
        collectibleComponent.type = CollectibleComponent.COIN;
        collectibleComponent.value = 1;
        ecsManager.addComponent(physicsComponent, entity);
        ecsManager.addComponent(graphicsComponent, entity);
        ecsManager.addComponent(collisionComponent, entity);
        ecsManager.addComponent(collectibleComponent, entity);
        ecsManager.addComponent(new CacheComponent(), entity);
    }
}