package com.actionteam.geometryadventures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by rka97 on 4/10/2018.
 */

public class MainMenuScreen implements Screen {
    Stage stage;
    Skin skin;

    public MainMenuScreen() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("terra-skin/terra-mother-ui.json"));
        Table table = new Table();
        table.setBackground(skin.getTiledDrawable("tile-a"));
        table.setFillParent(true);
        table.setDebug(true);
        ImageTextButton startButton = new ImageTextButton("Start Game", skin);
        table.add(startButton).expand(500, 500).right();
        table.row();
        ImageTextButton quitButton = new ImageTextButton("Quit", skin);
        table.add(quitButton).expand(500,500).left();
        stage.addActor(table);

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GeometryAdventuresGame.currentScreen = GeometryAdventuresGame.ChosenScreen.SCREEN_GAME_LEVEL;
            }
        });
    }

    @Override
    public void render(float dt) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void show()
    {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        stage = null;
        skin = null;
    }
}
