package com.xonix_new_edition.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    int amountOfLines;
    BlueBall.BlueBallDirection currentDirection;

    boolean captureBegin;
    ArrayList<Vector2> points;

    Texture fieldTexture;
    Pixmap fieldPixmap;

    BitmapFont scoreTextFont;
    String blueBallScore;
    String redBallScore;
    String scoreLabel;

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

        blueBallScore = "0.0 %";
        redBallScore = "0.0 %";
        scoreLabel = "Score:";
        scoreTextFont = new BitmapFont(Gdx.files.internal("font2.fnt"));

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
        scoreTextFont.setColor(Color.BLACK);
        scoreTextFont.draw(batch, scoreLabel, 1090, 700);
        scoreTextFont.setColor(Color.BLUE);
        scoreTextFont.draw(batch, blueBallScore, 1090, 650);
        scoreTextFont.setColor(Color.RED);
        scoreTextFont.draw(batch, redBallScore, 1090, 600);
        blueBall.render(batch);
        redBall.render(batch);
        batch.end();
        stage.draw();
        update();
    }

    public void update(){
        Vector2 blueBallPosition = blueBall.getPosition();
        float blueCellsCounter = 0;
        int seaAreaMaxSize = (980 / FIELD_CELL_SIZE - 7 - 7) * (690 / FIELD_CELL_SIZE - 7 - 7);
        Float capturedAreaPercent;

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
                    if(fieldGrid[i][j] == 2)
                        blueCellsCounter++;
                }
            }

            capturedAreaPercent = blueCellsCounter / seaAreaMaxSize * 100;

            blueBallScore = capturedAreaPercent.toString().
                    substring(0, capturedAreaPercent.toString().indexOf(".") + 2) + " %";
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
        scoreTextFont.dispose();
        fieldPixmap.dispose();
        batch.dispose();
        background.dispose();
        shapeRenderer.dispose();
    }

//    private boolean iter(int x, int y, int depth){ // Idea from YouTube https://www.youtube.com/watch?v=_5W5sYjDBnA
//        boolean ret1 = true, ret2 = true, ret3 = true, ret4 = true;
//
//        fieldGrid[x][y] = 7;
//
//        if(depth == 10){
//            points.add(new Vector2(x, y));
//            return false;
//        }
//
//        if(fieldGrid[x - 1][y] == 0){
//            ret1 = iter(x - 1, y, depth + 1);
//        }
//
//        if(fieldGrid[x + 1][y] == 0){
//            ret2 = iter(x + 1, y, depth + 1);
//        }
//
//        if(fieldGrid[x][y - 1] == 0){
//            ret3 = iter(x, y - 1, depth + 1);
//        }
//
//        if(fieldGrid[x][y + 1] == 0){
//            ret4 = iter(x, y + 1, depth + 1);
//        }
//
//        return ret1 && ret2 && ret3 && ret4;
//    }

    private void fieldFillIteration(int x, int y, int depth){ // Idea from YouTube https://www.youtube.com/watch?v=_5W5sYjDBnA
                                                              // and https://lodev.org/cgtutor/floodfill.html#Introduction_
        if(depth == 10){
            points.add(new Vector2(x, y));
            return;
        }

        if(fieldGrid[x][y] == 0){
            fieldGrid[x][y] = 7;

            fieldFillIteration(x - 1, y, depth + 1);
            fieldFillIteration(x + 1, y, depth + 1);
            fieldFillIteration(x, y - 1, depth + 1);
            fieldFillIteration(x, y + 1, depth + 1);
        }
    }

    private void fieldFill(int x, int y) { // Idea from YouTube https://www.youtube.com/watch?v=_5W5sYjDBnA
                                           // and https://lodev.org/cgtutor/floodfill.html#Introduction_
        //System.out.println(points.size());
        //fieldGrid[x][y] = 2;
        points.add(new Vector2(x, y));

        for(int i = 0; i < points.size(); i++){
            fieldFillIteration((int)points.get(i).x, (int)points.get(i).y, 0);
            i = 0;
            points.remove(0);
        }

        if(points.size() != 0)
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
