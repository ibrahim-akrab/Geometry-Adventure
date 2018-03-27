package com.actionteam.geometryadventures;

import com.actionteam.geometryadventures.model.LevelLoader;
import com.actionteam.geometryadventures.model.Map;
import com.actionteam.geometryadventures.model.Tile;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Arrays;

public class GeometryAdventuresGame extends ApplicationAdapter {
	private FitViewport viewport;

	@Override
	public void create () {
		viewport = new FitViewport(10,10);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	@Override
	public void dispose () {
	}
}
