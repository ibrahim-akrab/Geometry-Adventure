package com.actionteam.geometryadventures;

import com.actionteam.geometryadventures.ecs.ECSManager;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class GeometryAdventuresGame extends ApplicationAdapter {

    private ECSManager ecsManager;
    private GameUtils gameUtils;
    private MainMenuScreen mainMenu;
    public static ChosenScreen currentScreen;
    private Music M;
    private long time;
    private int clockRate = 300;

    public enum ChosenScreen {
        SCREEN_MAIN_MENU,
        SCREEN_GAME_LEVEL
    }

    public GeometryAdventuresGame(GameUtils gameUtils) {
        this.gameUtils = gameUtils;
        ecsManager = null;
        currentScreen = ChosenScreen.SCREEN_GAME_LEVEL;
        time = TimeUtils.millis();
    }

    @Override
    public void create() {
        //TexturePacker.process("mysprites/", "textureatlas/", "textures");
        M = Gdx.audio.newMusic(Gdx.files.internal("0935.ogg"));
        switch (currentScreen) {
            case SCREEN_MAIN_MENU:
                mainMenu = new MainMenuScreen();
                break;
            case SCREEN_GAME_LEVEL:
                gameUtils.loadLevel("map");
                ecsManager = ECSManager.getInstance();
                break;
            default:
                break;
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        long t = TimeUtils.millis();
        if(t - time > clockRate) {
            Clock.clock++;
            time = t;
        }

        switch (currentScreen) {
            case SCREEN_MAIN_MENU:
                mainMenu.render(0);
                M.play();
                break;
            case SCREEN_GAME_LEVEL:
                if (ecsManager == null) {
                    M.stop();
                    this.create();
                    // to update the viewport
                    this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                }
                ecsManager.update(Gdx.graphics.getDeltaTime());
                break;
            default:
                break;
        }
    }

    @Override
    public void dispose() {
        if (mainMenu != null) {
            mainMenu.dispose();
            mainMenu = null;
        }
        if (ecsManager != null) {
            ecsManager.fireEvent(ECSEvents.disposeEvent());
        }
    }

    @Override
    public void resize(int width, int height) {
        switch (currentScreen) {
            case SCREEN_MAIN_MENU:
                mainMenu.resize(width, height);
                break;
            case SCREEN_GAME_LEVEL:
                ecsManager.fireEvent(ECSEvents.resizeEvent(width, height));
                break;
            default:
                break;
        }
    }

}
