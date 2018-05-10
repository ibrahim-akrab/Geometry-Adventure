package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.ControlComponent;
import com.actionteam.geometryadventures.components.HealthComponent;
import com.actionteam.geometryadventures.components.ScoreComponent;
import com.actionteam.geometryadventures.ecs.ECSEvent;
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
 * <p>
 * This system is responsible for displaying the head up display
 * HUD currently contains:
 * 1 - Time
 * 2 - Health
 * 3 - Score
 * 4 - Control circles
 */

public class HudSystem extends System implements ECSEventListener {

    // the different components of the player
    private ControlComponent controlComponent;
    private HealthComponent healthComponent;
    private ScoreComponent scoreComponent;

    // used to draw control circles
    private ShapeRenderer shapeRenderer;
    private ScreenViewport viewport;

    // radius of the circle controlling the movement
    private float smallCircleRadius;

    // font for displaying score
    private BitmapFont font;

    // used for rendering
    private SpriteBatch batch;

    // Textures of the heart and coin
    private TextureRegion healthRegion;
    private TextureRegion coinRegion;

    // heart and coin size
    private float tileSize;

    // position of health
    private float healthX;
    private float healthY;
    private float healthPadding;

    // position of score
    private float coinX;
    private float coinY;
    private float scoreX;
    private float scoreY;
    private float scoreStringX;
    private float scoreStringY;

    // position of time
    private float timeX;
    private float timeY;
    private String scoreString = "Score";

    /**
     * HudSystem constructor
     * initializes class fields
     */
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
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    /**
     * Stores the cache component of the entity for further usage instead of querying the
     * ecsManager each time they are needed
     *
     * @param entityId the id of the new entity
     */
    @Override
    protected void entityAdded(int entityId) {
        controlComponent = (ControlComponent)
                ecsManager.getComponent(entityId, Components.CONTROL_COMPONENT_CODE);
        healthComponent = (HealthComponent)
                ecsManager.getComponent(entityId, Components.HEALTH_COMPONENT_CODE);
        scoreComponent = (ScoreComponent)
                ecsManager.getComponent(entityId, Components.SCORE_COMPONENT_CODE);
    }

    /**
     * Subscribes to resize event
     */
    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.RESIZE_EVENT, this);
        ecsManager.subscribe(ECSEvents.DISPOSE_EVENT, this);
    }

    /**
     * Draws hud
     *
     * @param dt delta time from last draw
     */
    @Override
    public void update(float dt) {
        Gdx.gl20.glLineWidth(3);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        viewport.apply();

        // if left side of the screen is touched, the left controller is drawn
        if (controlComponent.isLeftTouchDown) {
            drawController(controlComponent.leftInitialX, controlComponent.leftInitialY,
                    controlComponent.leftX, controlComponent.leftY, controlComponent.leftBigCircleRadius,
                    smallCircleRadius);
        }

        // if right side of the screen is touched, the right controller is drawn
        if (controlComponent.isRightTouchDown) {
            drawController(controlComponent.rightInitialX, controlComponent.rightInitialY,
                    controlComponent.rightX, controlComponent.rightY, controlComponent.rightBigCircleRadius,
                    smallCircleRadius);
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // draws "score" string
        font.draw(batch, scoreString, scoreStringX, scoreStringY);

        // draws number of hearts corresponding to the health
        for (int i = 0; i < healthComponent.health; i++) {
            batch.draw(healthRegion, (tileSize + healthPadding) * i + healthX, healthY, tileSize, tileSize);
        }

        // draws score
        batch.draw(coinRegion, coinX, coinY, tileSize * 2, tileSize * 2);
        font.draw(batch, String.format(Locale.getDefault(), "%03d", scoreComponent.score),
                scoreX, scoreY);

        // draw time
        font.draw(batch, String.format(Locale.getDefault(), "%02d",
                (int) ClockSystem.getGameMinutes()) + ":" + String.format(Locale.getDefault(), "%02d",
                (int) ClockSystem.getGameSeconds()), timeX, timeY);
        batch.end();
    }

    /**
     * Draws a controller given the specified parameters
     * A controller consists of a small and a big circle that is used to infer the direction
     * of the touch
     *
     * @param initialX    the x coordinate of the initial touch
     * @param initialY    the y coordinate of the initial touch
     * @param x           the x coordinate of the dragged touch
     * @param y           the y coordinate of the dragged touch
     * @param bigRadius   the radius of the big circle
     * @param smallRadius the radius of the small circle
     */
    private void drawController(float initialX, float initialY,
                                float x, float y, float bigRadius, float smallRadius) {

        // calculate dragged distance
        float deltaX = x - initialX;
        float deltaY = y - initialY;
        float r = deltaX * deltaX + deltaY * deltaY;

        // if the small circle gets outside of the big circle
        // then put a cap on the x and y to restore its position
        float alpha = 1;
        if (r > bigRadius * bigRadius) {
            alpha = (float) Math.sqrt(bigRadius * bigRadius / r);
        }
        x = x - deltaX + alpha * deltaX;
        y = y - deltaY + alpha * deltaY;

        // draw small circle and big circle outline
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(initialX, viewport.getScreenHeight() - initialY, bigRadius);
        shapeRenderer.circle(x, viewport.getScreenHeight() - y, smallRadius);
        shapeRenderer.end();

        // fill both small and big circle with semi transparent background
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
            case ECSEvents.DISPOSE_EVENT:
                dispose();
                break;
        }
        return false;
    }

    /**
     * Handles the resize event by updating the dimensions of the hud
     *
     * @param width  new screen width
     * @param height new screen height
     */
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

    /**
     * Sets the texture of the coin and heart
     *
     * @param atlas the texture atlas containing the texture of coin and heart
     */
    public void setTextureAtlas(TextureAtlas atlas) {
        this.healthRegion = atlas.findRegion("heart", 0);
        this.coinRegion = atlas.findRegion("coin", 0);
    }


    private void dispose() {
        font.dispose();
        shapeRenderer.dispose();
        batch.dispose();
    }
}
