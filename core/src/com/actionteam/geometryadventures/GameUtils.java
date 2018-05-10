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
import com.actionteam.geometryadventures.ecs.Component;
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

public abstract class GameUtils {
    public static AIUtils aiUtils;

    // used for txt files
    public abstract InputStream openFile(String fileName) throws IOException;

    public abstract File getFile(String fileName);

    // the ecs manager
    private static ECSManager ecsManager;

    // initial position of the player
    private float initialPlayerX;
    private float initialPlayerY;

    /**
     * Loads a map from a file
     *
     * @param levelName file name to be loaded
     * @return map model
     */
    private Map loadMap(String levelName) {
        try {
            // load file into memory
            InputStream fis = openFile(levelName);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

            // deserialize the file
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

    /**
     * Loads a map from a file, and initializes all its entities and components
     *
     * @param levelName level file name
     * @throws FileNotFoundException if @levelName file is not found
     */
    void loadLevel(String levelName) throws FileNotFoundException {
        Map map = loadMap(levelName);
        map.updateTiles();
        aiUtils = new AIUtils(map);
        ecsManager = ECSManager.getInstance();

        // init entities and components
        initFloorTiles(map.getFloorTiles());
        initWallTiles(map.getWallTiles());
        initMiscTiles(map.getMiscTiles());
        initEnemyTiles(map.getEnemyTiles());
        initPortalTiles(map.getPortalTiles());
        initPlayerTile(map.getPlayerTile());
        initLightTiles(map.getLightTiles());
        initCollectableTile(map.getCollectibleTiles());

        // create systems
        createSystems(map);
        ecsManager.fireEvent(new ECSEvent(ECSEvents.LEVEL_STARTED, null));
    }

    /**
     * Creates all the systems for the ecs manager
     *
     * @throws FileNotFoundException if the shader in light system is not found
     */
    private void createSystems(Map map) throws FileNotFoundException {
        GraphicsSystem graphicsSystem = new GraphicsSystem(this);
        ControlSystem controlSystem = new ControlSystem();
        HudSystem hudSystem = new HudSystem();
        LightSystem lightSystem = new LightSystem(this);

        hudSystem.setTextureAtlas(graphicsSystem.getTextureAtlas());
        Gdx.input.setInputProcessor(controlSystem);
        lightSystem.setAmbientIntensity(map.getConfig().ambientIntensity);
        graphicsSystem.setLightSystem(lightSystem);

        ecsManager.addSystem(graphicsSystem);
        ecsManager.addSystem(controlSystem);
        ecsManager.addSystem(hudSystem);
        ecsManager.addSystem(lightSystem);
        ecsManager.addSystem(new PhysicsSystem());
        ecsManager.addSystem(new CollisionSystem());
        ecsManager.addSystem(new EnemySystem());
        ecsManager.addSystem(new WeaponSystem());
        ecsManager.addSystem(new LifetimeSystem());
        ecsManager.addSystem(new VisionSystem());
        ecsManager.addSystem(new SoundSystem());
        ecsManager.addSystem(new HealthSystem());
        ecsManager.addSystem(new ScoreSystem());
        ecsManager.addSystem(new CollectionSystem());
        ecsManager.addSystem(new ClockSystem());
        ecsManager.addSystem(new CacheSystem(initialPlayerX, initialPlayerY));
    }

    /**
     * Initializes player entity and its components
     *
     * @param playerTile player information
     */
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
        ControlComponent cc = new ControlComponent();
        CollisionComponent col = new CollisionComponent();
        LightComponent lc = new LightComponent();
        col.width = 0.7f;
        col.height = 0.7f;
        col.shapeType = CollisionComponent.RECTANGLE;
        col.id = Entities.PLAYER_COLLISION_ID;
        col.mask = ~0;
        initialPlayerX = playerTile.x;
        initialPlayerY = playerTile.y;
        lc.lightIntensity = 0.7f;
        lc.radius = 10.f;

        WeaponComponent wc = WeaponFactory.createWeapon(WeaponComponent.HAND_GUN);
        HealthComponent healthComponent = new HealthComponent();
        healthComponent.health = playerTile.health;

        ecsManager.addComponentNow(createPhysicsComponent(playerTile), entity);
        ecsManager.addComponentNow(cc, entity);
        ecsManager.addComponentNow(gc, entity);
        ecsManager.addComponentNow(col, entity);
        ecsManager.addComponentNow(wc, entity);
        ecsManager.addComponentNow(new ScoreComponent(), entity);
        ecsManager.addComponentNow(lc, entity);
        ecsManager.addComponentNow(healthComponent, entity);
        ecsManager.addComponentNow(new CacheComponent(), entity);
        ecsManager.addComponentNow(new CollectorComponent(), entity);
    }

    /**
     * Initializes portal entities and their components
     *
     * @param portalTiles portals information
     */
    private void initPortalTiles(List<PortalTile> portalTiles) {
        for (PortalTile portalTile : portalTiles) {
            int entity = ecsManager.createEntity();
            CollisionComponent collisionComponent = new CollisionComponent(Entities.PLAYER_COLLISION_ID);

            collisionComponent.shapeType = CollisionComponent.RECTANGLE;
            collisionComponent.height = 1;
            collisionComponent.width = 1;
            collisionComponent.id = Entities.ENVIRONMENT_COLLISION_ID;

            ecsManager.addComponentNow(createPhysicsComponent(portalTile), entity);
            ecsManager.addComponentNow(createGraphicsComponent(portalTile), entity);
            ecsManager.addComponentNow(collisionComponent, entity);
            ecsManager.addComponentNow(createPortalComponent(portalTile), entity);
            ecsManager.addComponentNow(new CacheComponent(), entity);
        }
    }

    /**
     * Initializes enemy entities and their components
     *
     * @param enemyTiles enemies information
     */
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

            EnemyComponent enemyComponent = new EnemyComponent();
            /* Add enemy weapon here */
            WeaponComponent enemyWeapon;
            if (enemyTile.subtype.equals("green orc")) {
                enemyWeapon = WeaponFactory.createWeapon(WeaponComponent.HAND_GUN);
                enemyComponent.speed = 1.0f;
            } else if (enemyTile.subtype.equals("skeleton")) {
                enemyWeapon = WeaponFactory.createWeapon(WeaponComponent.MELEE);
                enemyComponent.speed = 1.5f;
            } else {
                enemyWeapon = WeaponFactory.createWeapon(WeaponComponent.HAND_GUN);
            }
            ecsManager.addComponentNow(createPhysicsComponent(enemyTile), enemyEntity);
            ecsManager.addComponentNow(enemyGC, enemyEntity);
            ecsManager.addComponentNow(enemyCC, enemyEntity);
            ecsManager.addComponentNow(new HealthComponent(enemyTile.health), enemyEntity);
            ecsManager.addComponentNow(enemyWeapon, enemyEntity);
            ecsManager.addComponentNow(enemyComponent, enemyEntity);
            ecsManager.addComponentNow(new CacheComponent(), enemyEntity);
        }
    }

    /**
     * Initializes environment floor entities and their components
     *
     * @param floorTiles floor information
     */
    private void initFloorTiles(List<Tile> floorTiles) {
        for (Tile floorTile : floorTiles) {
            int entity = ecsManager.createEntity();
            ecsManager.addComponentNow(createPhysicsComponent(floorTile), entity);
            ecsManager.addComponentNow(createGraphicsComponent(floorTile), entity);
            ecsManager.addComponentNow(new CacheComponent(), entity);
        }
    }

    /**
     * Initializes miscellaneous entities and their components
     */
    private void initMiscTiles(List<Tile> miscTiles) {
        for (Tile miscTile : miscTiles) {
            int entity = ecsManager.createEntity();
            if (miscTile.textureName.equals("endportal")) {
                CollisionComponent cc = new CollisionComponent(Entities.PLAYER_COLLISION_ID);
                cc.radius = 0.7f;
                cc.height = 0.7f;
                cc.width = 0.7f;
                cc.shapeType = CollisionComponent.RECTANGLE;
                cc.id = Entities.END_PORTAL_COLLISION_ID;
                ecsManager.addComponentNow(cc, entity);
            }
            ecsManager.addComponentNow(createPhysicsComponent(miscTile), entity);
            ecsManager.addComponentNow(createGraphicsComponent(miscTile), entity);
            ecsManager.addComponentNow(new CacheComponent(), entity);
        }
    }

    /**
     * Initializes collectible entities and their components
     *
     * @param collectibleTiles collectibles' information
     */
    private void initCollectableTile(List<CollectibleTile> collectibleTiles) {
        for (CollectibleTile collectibleTile : collectibleTiles) {
            int entity = ecsManager.createEntity();
            CollisionComponent collisionComponent =
                    new CollisionComponent(Entities.PLAYER_COLLISION_ID);
            collisionComponent.height = 0.8f;
            collisionComponent.width = 0.8f;
            collisionComponent.radius = 0.8f;
            collisionComponent.id = Entities.COLLECTABLE_COLLISION_ID;
            collisionComponent.shapeType = CollisionComponent.RECTANGLE;
            ecsManager.addComponentNow(createPhysicsComponent(collectibleTile), entity);
            ecsManager.addComponentNow(createGraphicsComponent(collectibleTile), entity);
            ecsManager.addComponentNow(collisionComponent, entity);
            ecsManager.addComponentNow(createCollectibleComponent(collectibleTile), entity);
            ecsManager.addComponentNow(new CacheComponent(), entity);
        }
    }

    /**
     * Initializes wall entities and its components
     *
     * @param wallTiles portals information
     */
    private void initWallTiles(List<Tile> wallTiles) {
        for (Tile wallTile : wallTiles) {
            int entity = ecsManager.createEntity();
            CollisionComponent collisionComponent = new CollisionComponent();
            collisionComponent.shapeType = CollisionComponent.RECTANGLE;
            collisionComponent.width = 0.9f;
            collisionComponent.height = 0.9f;
            collisionComponent.id = Entities.ENVIRONMENT_COLLISION_ID;
            collisionComponent.mask = ~0;

            ecsManager.addComponentNow(createPhysicsComponent(wallTile), entity);
            ecsManager.addComponentNow(createGraphicsComponent(wallTile), entity);
            ecsManager.addComponentNow(collisionComponent, entity);
            ecsManager.addComponentNow(new CacheComponent(), entity);
        }
    }

    /**
     * Initializes light entities and its components
     *
     * @param lightTiles light sources information
     */
    private void initLightTiles(List<LightTile> lightTiles) {
        for (LightTile lightTile : lightTiles) {
            int entity = ecsManager.createEntity();
            if (lightTile.collidable) {
                CollisionComponent collisionComponent =
                        new CollisionComponent(Entities.PLAYER_COLLISION_ID,
                                Entities.ENEMY_COLLISION_ID);
                collisionComponent.shapeType = CollisionComponent.RECTANGLE;
                collisionComponent.width = 0.9f;
                collisionComponent.height = 0.9f;
                collisionComponent.id = Entities.ENVIRONMENT_COLLISION_ID;
                ecsManager.addComponentNow(collisionComponent, entity);
            }
            ecsManager.addComponentNow(createPhysicsComponent(lightTile), entity);
            ecsManager.addComponentNow(createGraphicsComponent(lightTile), entity);
            ecsManager.addComponentNow(createLightComponent(lightTile), entity);
            ecsManager.addComponentNow(new CacheComponent(), entity);
        }
    }

    /**
     * Creates a portal component based on the sent tile
     *
     * @param portalTile portal component information
     * @return new portal component corresponding to @portalTile
     */
    private PortalComponent createPortalComponent(PortalTile portalTile) {
        PortalComponent portalComponent = new PortalComponent();
        portalComponent.position.x = portalTile.toX;
        portalComponent.position.y = portalTile.toY;
        return portalComponent;
    }

    /**
     * Creates a graphics component based on the sent tile
     *
     * @param tile graphics component information
     * @return new graphics component corresponding to @tile
     */
    private GraphicsComponent createGraphicsComponent(Tile tile) {
        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.textureName = tile.textureName;
        graphicsComponent.textureIndex = tile.textureIndex;
        graphicsComponent.isAnimated = tile.isAnimated;
        graphicsComponent.frames = tile.frames;
        graphicsComponent.interval = tile.speed;
        return graphicsComponent;
    }

    /**
     * Creates a physics component based on the sent tile
     *
     * @param tile physics component information
     * @return new physics component corresponding to @tile
     */
    private PhysicsComponent createPhysicsComponent(Tile tile) {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.position.set(tile.x, tile.y);
        return physicsComponent;
    }

    /**
     * Creates a collectible component based on the sent tile
     *
     * @param tile collectible component information
     * @return new collectible component corresponding to @tile
     */
    private CollectibleComponent createCollectibleComponent(CollectibleTile tile) {
        CollectibleComponent collectibleComponent = new CollectibleComponent();
        if (tile.subtype.equals("heart"))
            collectibleComponent.type = CollectibleComponent.HEART;
        else if (tile.subtype.equals("coin"))
            collectibleComponent.type = CollectibleComponent.COIN;
        else
            collectibleComponent.type = CollectibleComponent.KEY;
        collectibleComponent.value = 1;
        return collectibleComponent;
    }

    /**
     * Creates a tile component based on the sent tile
     *
     * @param tile light component information
     * @return new light component corresponding to @tile
     */
    private LightComponent createLightComponent(LightTile tile) {
        LightComponent lightComponent = new LightComponent();
        lightComponent.lightIntensity = tile.lightIntensity;
        lightComponent.radius = tile.innerRadius;
        return lightComponent;
    }

    /**
     * Creates a standard gold coin
     *
     * @param x the x coordinate of the position of the coin
     * @param y the y coordinate of the position of the coin
     */
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