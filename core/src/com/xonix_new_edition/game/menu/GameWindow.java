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
    private final int FIELD_OFFSET = 15;

    private int[][] fieldGrid;
    Stage stage;
    XonixNewEdition xonixNewEdition;
    Button leaveButton;
    SpriteBatch batch;
    Texture background;
    OrthographicCamera camera;
    BlueBall blueBall;
    RedBall redBall;
    ShapeRenderer shapeRenderer;
    ArrayList<Vector2> lineStartPositionArrayList;
    ArrayList<Vector2> lineEndPositionArrayList;
    int amountOfLines;
    BlueBall.BlueBallDirection currentDirection;

    boolean captureBegin;
    ArrayList<Vector2> points;

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
        redBall = new RedBall(5);
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

        fieldGrid = new int[980 / FIELD_CELL_SIZE][690 / FIELD_CELL_SIZE];
        for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
            for(int j = 0; j < 35 / FIELD_CELL_SIZE; j++) {
                fieldGrid[i][j] = 3;
            }

            for(int j = 0; j < 30; j++) {
                fieldGrid[i][(690 - j)/ FIELD_CELL_SIZE  - 1] = 3;
            }
        }

        for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
            for(int i = 0; i < 35 / FIELD_CELL_SIZE; i++) {
                fieldGrid[i][j] = 3;
            }

            for(int i = 0; i < 30; i++) {
                fieldGrid[(980 - i)/ FIELD_CELL_SIZE  - 1][j] = 3;
            }
        }

        fieldPixmap = new Pixmap(1280, 720, Pixmap.Format.RGBA8888);
        fieldPixmap.setColor(Color.BROWN);
        fieldTexture = new Texture(fieldPixmap);

        captureBegin = false;

        points = new ArrayList<>();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //fieldGrid[((int)redBall.getPosition().x + 15) / 5][(659 - (int)redBall.getPosition().y + 15) / 5] = 2;

        for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
            for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
                if(fieldGrid[i][j] == 1){
                    fieldPixmap.setColor(Color.BLUE);
                    fieldPixmap.fillRectangle(FIELD_OFFSET + i * FIELD_CELL_SIZE, FIELD_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else if(fieldGrid[i][j] == 3){
                    fieldPixmap.setColor(Color.LIGHT_GRAY);
                    fieldPixmap.fillRectangle(FIELD_OFFSET + i * FIELD_CELL_SIZE, FIELD_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else if(fieldGrid[i][j] == 2){
                    fieldPixmap.setColor(Color.BLUE);
                    fieldPixmap.fillRectangle(FIELD_OFFSET + i * FIELD_CELL_SIZE, FIELD_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else{
                    fieldPixmap.setColor(Color.WHITE);
                    fieldPixmap.fillRectangle(FIELD_OFFSET + i * FIELD_CELL_SIZE, FIELD_OFFSET + j * FIELD_CELL_SIZE,
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
        redBall.render(batch);
        batch.end();
        stage.draw();
        update();

    }

    public void update(){
        Vector2 blueBallPosition = blueBall.getPosition();

        if(fieldGrid[((int)blueBallPosition.x + 15) / 5][(659 - (int)blueBallPosition.y + 15) / 5] == 1){
            captureBegin = false;
            //System.out.println("blu");
            for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == 1)
                        fieldGrid[i][j] = 0;
                }
            }
        }

        if(fieldGrid[((int)blueBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - 15) / FIELD_CELL_SIZE] != 3
                && fieldGrid[((int)blueBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - 15) / FIELD_CELL_SIZE] != 2){
            fieldGrid[((int)blueBallPosition.x + 15) / 5][(689 - (int)blueBallPosition.y - 15) / 5] = 1;
            captureBegin = true;
        }

        else{

            if(captureBegin){
                blueBall.setDefaultDirection();
                fieldFill(((int)(redBall.getPosition().x - 15) / FIELD_CELL_SIZE),
                        (int)((689 - redBall.getPosition().y - 15) / FIELD_CELL_SIZE));
            }

            captureBegin = false;


            for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == 1)
                        fieldGrid[i][j] = 2;
                }
            }
        }
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

    private boolean iter(int x, int y, int depth){ // Idea from YouTube
        boolean ret1 = true, ret2 = true, ret3 = true, ret4 = true;

        fieldGrid[x][y] = 7;

        if(depth == 10){
            points.add(new Vector2(x, y));
            return false;
        }

        if(fieldGrid[x - 1][y] == 0){
            ret1 = iter(x - 1, y, depth + 1);
        }

        if(fieldGrid[x + 1][y] == 0){
            ret2 = iter(x + 1, y, depth + 1);
        }

        if(fieldGrid[x][y - 1] == 0){
            ret3 = iter(x, y - 1, depth + 1);
        }

        if(fieldGrid[x][y + 1] == 0){
            ret4 = iter(x, y + 1, depth + 1);
        }

        return ret1 && ret2 && ret3 && ret4;
    }


    private void fieldFill(int x, int y) { // Idea from YouTube

        System.out.println(points.size());
        //fieldGrid[x][y] = 2;
        points.add(new Vector2(x, y));

        for(int i = 0; i < points.size(); i++){
            iter((int)points.get(i).x, (int)points.get(i).y, 0);
            i = 0;
            points.remove(0);
        }

        points.remove(0);

        for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
            for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++) {
                if(fieldGrid[i][j] == 0)
                    fieldGrid[i][j] = 2;
                if(fieldGrid[i][j] == 7)
                    fieldGrid[i][j] = 0;
            }
        }
    }
}
