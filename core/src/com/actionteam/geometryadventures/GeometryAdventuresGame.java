package com.actionteam.geometryadventures;

import com.actionteam.geometryadventures.ecs.ECSManager;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GeometryAdventuresGame extends ApplicationAdapter {
	private ECSManager ecsManager;
	private GameUtils gameUtils;

	public GeometryAdventuresGame(GameUtils gameUtils){
		this.gameUtils = gameUtils;
	}

	@Override
	public void create () {
		//TexturePacker.process("mysprites/", "textureatlas/", "textures");
		gameUtils.loadLevel("map");
		ecsManager = ECSManager.getInstance();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		ecsManager.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose () {
		ecsManager.fireEvent(ECSEvents.disposeEvent());
	}

	@Override
	public void resize(int width, int height) {
		ecsManager.fireEvent(ECSEvents.resizeEvent(width,height));
	}

}
