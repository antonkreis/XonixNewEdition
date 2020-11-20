package com.xonix_new_edition.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.xonix_new_edition.game.XonixNewEdition;

import java.util.ArrayList;

public class GameWindow implements Screen {
    private final int FIELD_CELL_SIZE = 5;
    private final int SEA_OFFSET = 40;

    private int[][] fieldGrid;
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

    boolean captureBegin;

    Texture fieldTexture;
    Pixmap fieldPixmap;

    GameWindow(final XonixNewEdition xonixNewEdition){
        this.xonixNewEdition = xonixNewEdition;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        batch = new SpriteBatch();
        background = new Texture("background_game_window_old.png");
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

        fieldGrid = new int[930 / FIELD_CELL_SIZE][640 / FIELD_CELL_SIZE];
        for(int i = 0; i < 930 / FIELD_CELL_SIZE; i++){
            for(int j = 0; j < 10 / FIELD_CELL_SIZE; j++) {
                fieldGrid[i][j] = 3;
            }
        }

        fieldPixmap = new Pixmap(1280, 720, Pixmap.Format.RGBA8888);
        fieldPixmap.setColor(Color.BROWN);
        fieldTexture = new Texture(fieldPixmap);

        captureBegin = false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for(int i = 0; i < 930 / FIELD_CELL_SIZE; i++){
            for(int j = 0; j < 640 / FIELD_CELL_SIZE; j++){
                if(fieldGrid[i][j] == 1){
                    fieldPixmap.setColor(Color.BROWN);
                    fieldPixmap.fillRectangle(SEA_OFFSET + i * FIELD_CELL_SIZE, SEA_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else if(fieldGrid[i][j] == 3){
                    fieldPixmap.setColor(Color.LIGHT_GRAY);
                    fieldPixmap.fillRectangle(SEA_OFFSET + i * FIELD_CELL_SIZE, SEA_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else{
                    fieldPixmap.setColor(Color.WHITE);
                    fieldPixmap.fillRectangle(SEA_OFFSET + i * FIELD_CELL_SIZE, SEA_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
            }
        }

        fieldTexture.draw(fieldPixmap, 0, 0);

        currentDirection = blueBall.getDirection();
        blueBall.update();

        batch.begin();
        batch.draw(background, 0, 0);
        batch.draw(fieldTexture, 0, 0);

        blueBall.render(batch);
        batch.end();
        stage.draw();
        update();

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        for (int i = 0; i < lineStartPositionArrayList.size(); i++) {
//            shapeRenderer.line(lineStartPositionArrayList.get(i), lineEndPositionArrayList.get(i));
//        }
//        shapeRenderer.end();
    }

    public void update(){
//        if(lineStartPositionArrayList.size() == 0)
//            return;
        Vector2 blueBallPosition = blueBall.getPosition();

        if((((int)blueBallPosition.x - 10) / FIELD_CELL_SIZE) != 0
                && ((629 - (int)blueBallPosition.y + 20) / FIELD_CELL_SIZE) != 0
                && (((int)blueBallPosition.x - 10) / FIELD_CELL_SIZE) != ((930)/ FIELD_CELL_SIZE - 1)
                && ((629 - (int)blueBallPosition.y + 20) / FIELD_CELL_SIZE) != ((640)/ FIELD_CELL_SIZE - 1)){
            fieldGrid[((int)blueBallPosition.x - 10) / 5][(629 - (int)blueBallPosition.y + 20) / 5] = 1;
            if(!captureBegin){
                System.out.println("bla");
                captureBegin = true;
            }

        }
        else{
            captureBegin = false;
            System.out.println("ble");
            for(int i = 0; i < 930 / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < 640 / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == 1)
                        fieldGrid[i][j] = 0;
                }
            }
        }



//        if(lineStartPositionArrayList.size() > 1){
//            if(blueBallPosition.x <= 10 || blueBallPosition.x >= 930
//                    || blueBallPosition.y <= 10 || blueBallPosition.y >= 640){
//                for(int i = 1; i < lineStartPositionArrayList.size(); i++){
//                    lineStartPositionArrayList.remove(lineStartPositionArrayList.size() - 1);
//                    lineEndPositionArrayList.remove(lineEndPositionArrayList.size() - 1);
//                }
//                lineStartPositionArrayList.get(0).x = blueBallPosition.x + 35;
//                lineStartPositionArrayList.get(0).y = blueBallPosition.y + 35;
//                lineEndPositionArrayList.get(0).x = blueBallPosition.x + 35;
//                lineEndPositionArrayList.get(0).y = blueBallPosition.y + 35;
//
//                blueBall.setDefaultDirection();
//
//                return;
//            }
//        }
//
//
//        if(currentDirection != blueBall.getDirection()){
//            System.out.println("a");
//            lineEndPositionArrayList.add(new Vector2());
//
//            lineStartPositionArrayList.add(
//                    new Vector2(lineEndPositionArrayList.get(lineEndPositionArrayList.size() - 1).x,
//                            lineEndPositionArrayList.get(lineEndPositionArrayList.size() - 1).y));
//
//        }
//
//        if(lineStartPositionArrayList.size() == 1){
//            lineStartPositionArrayList.get(0).x = blueBallPosition.x + 35;
//            lineStartPositionArrayList.get(0).y = blueBallPosition.y + 35;
//        }
//        else{
//            lineStartPositionArrayList.get(lineStartPositionArrayList.size() - 1).x =
//                    lineEndPositionArrayList.get(lineStartPositionArrayList.size() - 2).x;
//            lineStartPositionArrayList.get(lineStartPositionArrayList.size() - 1).y =
//                    lineEndPositionArrayList.get(lineStartPositionArrayList.size() - 2).y;
//        }
//
//        lineEndPositionArrayList.get(lineStartPositionArrayList.size() - 1).x = blueBallPosition.x + 35;
//        lineEndPositionArrayList.get(lineStartPositionArrayList.size() - 1).y = blueBallPosition.y + 35;
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
        fieldPixmap.dispose();
        batch.dispose();
        background.dispose();
        shapeRenderer.dispose();
    }
}
