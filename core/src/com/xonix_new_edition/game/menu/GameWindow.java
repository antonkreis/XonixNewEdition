package com.xonix_new_edition.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.xonix_new_edition.game.XonixNewEdition;

public class GameWindow implements Screen {
    Stage stage;
    XonixNewEdition xonixNewEdition;
    Button leaveButton;
    SpriteBatch batch;
    Texture background;
    OrthographicCamera camera;
    BlueBall blueBall;
    ShapeRenderer shapeRenderer;
    Vector2 lineStartPosition;
    Vector2 lineEndPosition;

    GameWindow(final XonixNewEdition xonixNewEdition){
        this.xonixNewEdition = xonixNewEdition;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        batch = new SpriteBatch();
        background = new Texture("background_game_window.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        blueBall = new BlueBall(5);
        leaveButton = new Button(xonixNewEdition, "leave_button.png", "leave_button.png", 1070, 20);
        leaveButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("leaveButton");
                xonixNewEdition.setScreen(new MainWindow(xonixNewEdition));
            }
        });
        stage.addActor(leaveButton.textButton);

        Gdx.gl.glLineWidth(5);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(Color.BLUE);

        lineStartPosition = new Vector2();
        lineEndPosition = new Vector2();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        blueBall.update();
        batch.begin();
        batch.draw(background, 0, 0);
        blueBall.render(batch);
        batch.end();
        stage.draw();
        update();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(lineStartPosition, lineEndPosition);
        shapeRenderer.end();
    }

    public void update(){
        Vector2 blueBallPosition = blueBall.getPosition();
        lineStartPosition.x = 45;
        lineStartPosition.y = 10;
        lineEndPosition.x = blueBallPosition.x + 35;
        lineEndPosition.y = blueBallPosition.y;
    }

    @Override
    public void resize(int width, int height) {

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
        batch.dispose();
        background.dispose();
    }
}
