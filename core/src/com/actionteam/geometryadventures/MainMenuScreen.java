package com.actionteam.geometryadventures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
        ImageTextButton imageTextButton = new ImageTextButton("Yep", skin);
        table.add(imageTextButton).expandX().right();
        imageTextButton = new ImageTextButton("Nope", skin);
        table.add(imageTextButton).padLeft(10.0f);
        stage.addActor(table);
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
    }
}
