package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.ControlComponent;
import com.actionteam.geometryadventures.components.HealthComponent;
import com.actionteam.geometryadventures.components.ScoreComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Locale;

/**
 * Created by theartful on 3/28/18.
 */

public class HudSystem extends System implements ECSEventListener {

    private ControlComponent controlComponent;
    private HealthComponent healthComponent;
    private ScoreComponent scoreComponent;
    private ShapeRenderer shapeRenderer;
    private ScreenViewport viewport;
    private float smallCircleRadius;
    private BitmapFont font;
    private SpriteBatch batch;
    private TextureAtlas atlas;
    private TextureRegion healthRegion;
    private TextureRegion coinRegion;
    private float tileSize;
    private float healthX;
    private float healthY;
    private float healthPadding;
    private float coinX;
    private float coinY;
    private float scoreX;
    private float scoreY;
    private float timeX;
    private float timeY;
    private String scoreString = "Score";
    private float scoreStringX;
    private float scoreStringY;
    private int fontSize;

    public HudSystem() {
        super(Components.CONTROL_COMPONENT_CODE, Components.HEALTH_COMPONENT_CODE,
                Components.SCORE_COMPONENT_CODE);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        viewport = new ScreenViewport();
        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/mono-font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (Gdx.graphics.getWidth() * 0.03);
        fontSize = parameter.size;
        font = generator.generateFont(parameter); // font size 12 pixels
        // resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        generator.dispose();
    }

    @Override
    protected void entityAdded(int entityId) {
        controlComponent = (ControlComponent)
                ecsManager.getComponent(entityId, Components.CONTROL_COMPONENT_CODE);
        healthComponent = (HealthComponent)
                ecsManager.getComponent(entityId, Components.HEALTH_COMPONENT_CODE);
        scoreComponent = (ScoreComponent)
                ecsManager.getComponent(entityId, Components.SCORE_COMPONENT_CODE);
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.RESIZE_EVENT, this);
    }

    @Override
    public void update(float dt) {
        Gdx.gl20.glLineWidth(3);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        viewport.apply();

        if (controlComponent.isLeftTouchDown) {
            drawController(controlComponent.leftInitialX, controlComponent.leftInitialY,
                    controlComponent.leftX, controlComponent.leftY, controlComponent.leftBigCircleRadius,
                    smallCircleRadius);
        }
        if (controlComponent.isRightTouchDown) {
            drawController(controlComponent.rightInitialX, controlComponent.rightInitialY,
                    controlComponent.rightX, controlComponent.rightY, controlComponent.rightBigCircleRadius,
                    smallCircleRadius);
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        font.draw(batch, scoreString, scoreStringX, scoreStringY);
        for (int i = 0; i < healthComponent.health; i++) {
            batch.draw(healthRegion, (tileSize + healthPadding) * i + healthX, healthY, tileSize, tileSize);
        }
        batch.draw(coinRegion, coinX, coinY, tileSize * 2, tileSize * 2);
        font.draw(batch, String.format(Locale.getDefault(), "%03d", scoreComponent.score),
                scoreX, scoreY);
        font.draw(batch, String.format(Locale.getDefault(),
                "%02d", (int) ClockSystem.gameMinutes) + ":" +
                        String.format(Locale.getDefault(), "%02d",
                                (int) ClockSystem.gameSeconds),
                timeX, timeY);
        batch.end();
    }

    private void drawController(float initialX, float initialY,
                                float x, float y, float bigRadius, float smallRadius) {
        float deltaX = x - initialX;
        float deltaY = y - initialY;
        float r = deltaX * deltaX + deltaY * deltaY;
        float alpha = 1;
        if (r > bigRadius * bigRadius) {
            alpha = (float) Math.sqrt(bigRadius * bigRadius / r);
        }
        x = x - deltaX + alpha * deltaX;
        y = y - deltaY + alpha * deltaY;

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(initialX,
                viewport.getScreenHeight() - initialY, bigRadius);
        shapeRenderer.circle(x,
                viewport.getScreenHeight() - y,
                smallRadius);
        shapeRenderer.end();

        shapeRenderer.setColor(new Color(1, 1, 1, 0.1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(initialX,
                viewport.getScreenHeight() - initialY, bigRadius);
        shapeRenderer.circle(x, viewport.getScreenHeight() - y, smallRadius);
        shapeRenderer.end();
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.RESIZE_EVENT:
                int[] size = (int[]) message;
                resize(size[0], size[1]);
                return true;
        }
        return false;
    }

    private void resize(int width, int height) {
        viewport.update(width, height, true);
        smallCircleRadius = 0.7f * controlComponent.leftBigCircleRadius;
        tileSize = width / 40.0f;
        healthPadding = tileSize / 10.0f;
        healthX = width / 100.0f;
        healthY = height * 0.9f;
        coinX = width - healthX - tileSize * 2;
        coinY = healthY - tileSize / 2;
        scoreX = coinX - 2.5f * font.getSpaceWidth() - healthPadding;
        scoreY = healthY + font.getCapHeight();
        timeX = width / 2.0f - 2.5f * font.getSpaceWidth();
        timeY = healthY + font.getCapHeight();
    }

    public void setTextureAtlas(TextureAtlas atlas) {
        this.atlas = atlas;
        this.healthRegion = atlas.findRegion("heart", 0);
        this.coinRegion = atlas.findRegion("coin", 0);
    }
}
