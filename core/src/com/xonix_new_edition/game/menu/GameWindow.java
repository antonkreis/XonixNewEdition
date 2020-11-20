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

import java.util.ArrayList;

public class GameWindow implements Screen {
    Stage stage;
    XonixNewEdition xonixNewEdition;
    Button leaveButton;
    SpriteBatch batch;
    Texture background;
    OrthographicCamera camera;
    BlueBall blueBall;
    ShapeRenderer shapeRenderer;
    ArrayList<Vector2> lineStartPositionArrayList;
    ArrayList<Vector2> lineEndPositionArrayList;
    int amountOfLines;
    BlueBall.BlueBallDirection currentDirection;

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

        amountOfLines = 1;
        currentDirection = BlueBall.BlueBallDirection.DEFAULT;

        lineStartPositionArrayList = new ArrayList<>();
        lineEndPositionArrayList = new ArrayList<>();
        for (int i = 0; i < amountOfLines; i++) {
            lineStartPositionArrayList.add(new Vector2(45, 45));
            lineEndPositionArrayList.add(new Vector2());
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        currentDirection = blueBall.getDirection();
        blueBall.update();

        batch.begin();
        batch.draw(background, 0, 0);
        blueBall.render(batch);
        batch.end();
        stage.draw();
        update();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < lineStartPositionArrayList.size(); i++) {
            shapeRenderer.line(lineStartPositionArrayList.get(i), lineEndPositionArrayList.get(i));
        }
        shapeRenderer.end();
    }

    public void update(){
//        if(lineStartPositionArrayList.size() == 0)
//            return;
        Vector2 blueBallPosition = blueBall.getPosition();

        if(lineStartPositionArrayList.size() > 1){
            if(blueBallPosition.x <= 10 || blueBallPosition.x >= 930
                    || blueBallPosition.y <= 10 || blueBallPosition.y >= 640){
                for(int i = 1; i < lineStartPositionArrayList.size(); i++){
                    lineStartPositionArrayList.remove(lineStartPositionArrayList.size() - 1);
                    lineEndPositionArrayList.remove(lineEndPositionArrayList.size() - 1);
                }
                lineStartPositionArrayList.get(0).x = blueBallPosition.x + 35;
                lineStartPositionArrayList.get(0).y = blueBallPosition.y + 35;
                lineEndPositionArrayList.get(0).x = blueBallPosition.x + 35;
                lineEndPositionArrayList.get(0).y = blueBallPosition.y + 35;

                blueBall.setDefaultDirection();

                return;
            }
        }


        if(currentDirection != blueBall.getDirection()){
            System.out.println("a");
            lineEndPositionArrayList.add(new Vector2());

            lineStartPositionArrayList.add(
                    new Vector2(lineEndPositionArrayList.get(lineEndPositionArrayList.size() - 1).x,
                            lineEndPositionArrayList.get(lineEndPositionArrayList.size() - 1).y));

        }

        if(lineStartPositionArrayList.size() == 1){
            lineStartPositionArrayList.get(0).x = blueBallPosition.x + 35;
            lineStartPositionArrayList.get(0).y = blueBallPosition.y + 35;
        }
        else{
            lineStartPositionArrayList.get(lineStartPositionArrayList.size() - 1).x =
                    lineEndPositionArrayList.get(lineStartPositionArrayList.size() - 2).x;
            lineStartPositionArrayList.get(lineStartPositionArrayList.size() - 1).y =
                    lineEndPositionArrayList.get(lineStartPositionArrayList.size() - 2).y;
        }

        lineEndPositionArrayList.get(lineStartPositionArrayList.size() - 1).x = blueBallPosition.x + 35;
        lineEndPositionArrayList.get(lineStartPositionArrayList.size() - 1).y = blueBallPosition.y + 35;
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
        shapeRenderer.dispose();
    }
}
