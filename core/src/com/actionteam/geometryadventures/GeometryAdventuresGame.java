package com.actionteam.geometryadventures;

import com.actionteam.geometryadventures.ecs.ECSManager;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GeometryAdventuresGame extends ApplicationAdapter {
    private enum ChosenScreen {
        SCREEN_MAIN_MENU,
        SCREEN_GAME_LEVEL
    }

	private ECSManager ecsManager;
	private GameUtils gameUtils;
    private MainMenuScreen mainMenu;
    private ChosenScreen currentScreen;

	public GeometryAdventuresGame(GameUtils gameUtils){
		this.gameUtils = gameUtils;
		currentScreen = ChosenScreen.SCREEN_MAIN_MENU;
	}

	@Override
	public void create () {
		//TexturePacker.process("mysprites/", "textureatlas/", "textures");
        switch(currentScreen)
        {
            case SCREEN_MAIN_MENU:
                mainMenu = new MainMenuScreen();
                break;
            case SCREEN_GAME_LEVEL:
                ecsManager = gameUtils.loadLevel("map");
                break;
            default:
                break;
        }
	}

	@Override
	public void render () {
	    Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch(currentScreen)
        {
            case SCREEN_MAIN_MENU:
                mainMenu.render(0);
                break;
            case SCREEN_GAME_LEVEL:
                ecsManager.update(Gdx.graphics.getDeltaTime());
                break;
            default:
                break;
        }
	}
	
	@Override
	public void dispose () {
		if(mainMenu != null)
		    mainMenu = null;
	    ecsManager.fireEvent(ECSEvents.disposeEvent());
	}

	@Override
	public void resize(int width, int height) {
	    mainMenu.resize(width, height);
		ecsManager.fireEvent(ECSEvents.resizeEvent(width,height));
	}

}
