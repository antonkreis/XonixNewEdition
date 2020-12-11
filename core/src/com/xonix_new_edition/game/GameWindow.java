package com.xonix_new_edition.game;

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

import java.util.ArrayList;

public class GameWindow implements Screen {
    private final int FIELD_CELL_SIZE = 5;
    private final int FIELD_OFFSET = 15;
    private final int LIGHT_GRAY_COLOR = 3;
    private final int BLUE_TERRITORY_COLOR = 2;
    private final int RED_TERRITORY_COLOR = 12;
    private final int BLUE_TRACK_COLOR = 1;
    private final int RED_TRACK_COLOR = 11;
    private final int BLUE_TEMP_COLOR = 7;
    private final int RED_TEMP_COLOR = 17;
    private final int GRAY_BORDER_WIDTH = 7;
    private final int FIELD_SIZE_X = 980;
    private final int FIELD_SIZE_Y = 690;

    private int[][] fieldGrid;
    private Stage stage;
    private XonixNewEdition xonixNewEdition;
    private Button leaveButton;
    private SpriteBatch batch;
    private Texture background;
    //private OrthographicCamera camera;
    private BlueBall blueBall;
    private RedBall redBall;
    private EnemyBall enemyBall;
    private ShapeRenderer shapeRenderer;
    //int amountOfLines;
    //BlueBall.BlueBallDirection currentDirection;

    private boolean blueCaptureBegin;
    private boolean redCaptureBegin;
    private boolean redCaptureAborted;
    private boolean blueCaptureAborted;

    private ArrayList<Vector2> points;

    private Texture fieldTexture;
    private Pixmap fieldPixmap;

    private BitmapFont textFont;
    private String blueBallScore;
    private String redBallScore;
    private String scoreLabel;
    private String timeLabel;
    private String time;
    private String nicknameBlue;
    private String nicknameRed;

    private String timeout;
    private int timeoutInt;
    private int areaToWin;
    private float rawTimeSinceStart = 0;
    private int minutes;
    private int seconds;

    GameWindow(final XonixNewEdition xonixNewEdition, String timeout,
               String areaToWin, final String nicknameBlue, final String nickNameRed){
        this.xonixNewEdition = xonixNewEdition;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.areaToWin = Integer.parseInt(areaToWin.substring(0,2));
        this.timeout = timeout;
        this.timeoutInt = Integer.parseInt(timeout.substring(0, 1));
        this.nicknameBlue = nicknameBlue;
        this.nicknameRed = nickNameRed;

        batch = new SpriteBatch();
        background = new Texture("background_game_window_old.png");
        //camera = new OrthographicCamera();
        //camera.setToOrtho(false, 1280, 720);
        blueBall = new BlueBall(5);
        redBall = new RedBall(5);
        enemyBall = new EnemyBall(5);
        enemyBall.setRandomDirection();
        leaveButton = new Button(xonixNewEdition, "leave_button.png", "leave_button.png", 1070, 20);
        leaveButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("leaveButton");
                xonixNewEdition.setScreen(new MainWindow(xonixNewEdition, nicknameBlue, nicknameRed));
            }
        });
        stage.addActor(leaveButton.textButton);

        Gdx.gl.glLineWidth(5);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(Color.BLUE);

        //amountOfLines = 1;
        //currentDirection = BlueBall.BlueBallDirection.DEFAULT;

        fieldGrid = new int[FIELD_SIZE_X / FIELD_CELL_SIZE][690 / FIELD_CELL_SIZE];
        for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
            for(int j = 0; j < 35 / FIELD_CELL_SIZE; j++) {
                fieldGrid[i][j] = LIGHT_GRAY_COLOR;
            }

            for(int j = 0; j < 30; j++) {
                fieldGrid[i][(FIELD_SIZE_Y - j)/ FIELD_CELL_SIZE  - 1] = LIGHT_GRAY_COLOR;
            }
        }

        for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++){
            for(int i = 0; i < 35 / FIELD_CELL_SIZE; i++) {
                fieldGrid[i][j] = LIGHT_GRAY_COLOR;
            }

            for(int i = 0; i < 30; i++) {
                fieldGrid[(FIELD_SIZE_X - i)/ FIELD_CELL_SIZE  - 1][j] = LIGHT_GRAY_COLOR;
            }
        }

        fieldPixmap = new Pixmap(1280, 720, Pixmap.Format.RGBA8888);
        fieldPixmap.setColor(Color.BROWN);
        fieldTexture = new Texture(fieldPixmap);

        redCaptureBegin = false;
        blueCaptureBegin = false;
        redCaptureAborted = false;
        blueCaptureAborted = false;

        points = new ArrayList<>();

        blueBallScore = "0.0 %";
        redBallScore = "0.0 %";
        scoreLabel = "Score:";

        timeLabel = "Time:";
        time = timeout.substring(0, 1) + ".00";
        minutes = Integer.parseInt(timeout.substring(0, 1));
        seconds = 0;

        textFont = new BitmapFont(Gdx.files.internal("font2.fnt"));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
//        Gdx.gl.glClearColor(1, 1, 1, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //fieldGrid[((int)redBall.getPosition().x + 15) / 5][(659 - (int)redBall.getPosition().y + 15) / 5] = 2;

        for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
            for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++){
                if(fieldGrid[i][j] == BLUE_TRACK_COLOR){
                    fieldPixmap.setColor(Color.BLUE);
                    fieldPixmap.fillRectangle(FIELD_OFFSET + i * FIELD_CELL_SIZE, FIELD_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else if(fieldGrid[i][j] == LIGHT_GRAY_COLOR){
                    fieldPixmap.setColor(Color.LIGHT_GRAY);
                    fieldPixmap.fillRectangle(FIELD_OFFSET + i * FIELD_CELL_SIZE, FIELD_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else if(fieldGrid[i][j] == BLUE_TERRITORY_COLOR){
                    fieldPixmap.setColor(Color.BLUE);
                    fieldPixmap.fillRectangle(FIELD_OFFSET + i * FIELD_CELL_SIZE, FIELD_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else if(fieldGrid[i][j] == 30){
                    fieldPixmap.setColor(Color.YELLOW);
                    fieldPixmap.fillRectangle(FIELD_OFFSET + i * FIELD_CELL_SIZE, FIELD_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else if(fieldGrid[i][j] == RED_TRACK_COLOR){
                    fieldPixmap.setColor(Color.RED);
                    fieldPixmap.fillRectangle(FIELD_OFFSET + i * FIELD_CELL_SIZE, FIELD_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else if(fieldGrid[i][j] == RED_TERRITORY_COLOR){
                    fieldPixmap.setColor(Color.RED);
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

        //currentDirection = blueBall.getDirection();
        blueBall.update();
        redBall.update();
        enemyBall.update();

        batch.begin();
        batch.draw(background, 0, 0);
        batch.draw(fieldTexture, 0, 0);
        textFont.setColor(Color.BLACK);
        textFont.draw(batch, scoreLabel, 1020, 700);
        textFont.setColor(Color.BLUE);
        textFont.draw(batch, nicknameBlue + ":", 1020, 650);
        textFont.draw(batch, blueBallScore, 1090, 600);
        textFont.setColor(Color.RED);
        textFont.draw(batch, nicknameRed + ":", 1020, 550);
        textFont.draw(batch, redBallScore, 1090, 500);
        textFont.setColor(Color.BLACK);
        textFont.draw(batch, timeLabel, 1020, 450);
        if(seconds < 10 && seconds >= 0)
            textFont.draw(batch, minutes + ":0" + seconds, 1090, 400);
        else
            textFont.draw(batch, minutes + ":" + seconds, 1090, 400);
        blueBall.render(batch);
        redBall.render(batch);
        enemyBall.render(batch);
        batch.end();
        stage.draw();
        update();

        rawTimeSinceStart += Gdx.graphics.getRawDeltaTime(); //Info about time counting: https://stackoverrun.com/ru/q/11987174

        if(rawTimeSinceStart >= 1){
            rawTimeSinceStart--;

            if(seconds == 0){
                seconds = 59;
                minutes--;
            }
            else
                seconds--;
        }
    }

    public void update(){
        Vector2 redBallPosition = redBall.getPosition();
        Vector2 blueBallPosition = blueBall.getPosition();
        float redCellsCounter = 0;
        float blueCellsCounter = 0;
        int seaAreaMaxSize = (FIELD_SIZE_X / FIELD_CELL_SIZE - GRAY_BORDER_WIDTH - GRAY_BORDER_WIDTH) * (FIELD_SIZE_Y / FIELD_CELL_SIZE - GRAY_BORDER_WIDTH - GRAY_BORDER_WIDTH);
        Float redCapturedAreaPercent;
        Float blueCapturedAreaPercent;

        if(fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(659 - (int)enemyBall.getPosition().y + FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TRACK_COLOR
                || fieldGrid[((int)blueBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(659 - (int)blueBall.getPosition().y + FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TRACK_COLOR){
            for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == RED_TRACK_COLOR)
                        fieldGrid[i][j] = 0;
                }
            }

            redCaptureBegin = false;
            redCaptureAborted = true;
        }


        if(fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(659 - (int)enemyBall.getPosition().y + FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TRACK_COLOR
                || fieldGrid[((int)redBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(659 - (int)redBall.getPosition().y + FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TRACK_COLOR){
            for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == BLUE_TRACK_COLOR)
                        fieldGrid[i][j] = 0;
                }
            }

            blueCaptureBegin = false;
            blueCaptureAborted = true;
        }


        if(fieldGrid[((int)redBallPosition.x + FIELD_OFFSET) / 5][(659 - (int)redBallPosition.y + FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TRACK_COLOR){
            redCaptureBegin = false;
            redCaptureAborted = true;
            for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == RED_TRACK_COLOR)
                        fieldGrid[i][j] = 0;
                }
            }
        }

        if((fieldGrid[((int)redBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                || fieldGrid[((int)redBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                || fieldGrid[((int)redBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                && redCaptureAborted) {
            redCaptureAborted = false;
            redBall.setDefaultDirection();
        }

        if(fieldGrid[((int)blueBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(659 - (int)blueBallPosition.y + FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TRACK_COLOR){
            blueCaptureBegin = false;
            blueCaptureAborted = true;
            for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == BLUE_TRACK_COLOR)
                        fieldGrid[i][j] = 0;
                }
            }
        }

        if((fieldGrid[((int)blueBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                || fieldGrid[((int)blueBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                || fieldGrid[((int)blueBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                && blueCaptureAborted) {
            blueCaptureAborted = false;
            blueBall.setDefaultDirection();
        }

        if(fieldGrid[((int)redBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] != LIGHT_GRAY_COLOR
                && fieldGrid[((int)redBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] != RED_TERRITORY_COLOR
                && fieldGrid[((int)redBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] != BLUE_TERRITORY_COLOR
                && !redCaptureAborted){
            fieldGrid[((int)redBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] = RED_TRACK_COLOR;
            redCaptureBegin = true;
        }
        else{
            if(redCaptureBegin){
                redBall.setDefaultDirection();
                fieldFill(((int)(enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE),
                        (int)((689 - enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE), true);
                if(fieldGrid[((int)blueBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] != LIGHT_GRAY_COLOR
                        && fieldGrid[((int)blueBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] != RED_TERRITORY_COLOR
                        && fieldGrid[((int)blueBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] != BLUE_TERRITORY_COLOR){
                    fieldFill(((int)(blueBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE),
                            (int)((689 - blueBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE), true);
                }
                else if((blueCaptureBegin && (
                        fieldGrid[((int)blueBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                                || fieldGrid[((int)blueBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                                || fieldGrid[((int)blueBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR))){
                    //System.out.println("ble");
                    fieldFill(((int)(blueBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE),
                            (int)((689 - blueBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE), true);
                }

                //System.out.println("blue");
                //System.out.println((blueBall.getPosition().x + 15) / FIELD_CELL_SIZE);

                for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
                    for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++) {
                        if(fieldGrid[i][j] == 0)
                            fieldGrid[i][j] = RED_TERRITORY_COLOR;
                        if(fieldGrid[i][j] == RED_TEMP_COLOR)
                            fieldGrid[i][j] = 0;
                    }
                }
            }

            redCaptureBegin = false;

            for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == RED_TRACK_COLOR)
                        fieldGrid[i][j] = RED_TERRITORY_COLOR;
                }
            }
        }

        if(fieldGrid[((int)blueBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] != LIGHT_GRAY_COLOR
                && fieldGrid[((int)blueBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] != RED_TERRITORY_COLOR
                && fieldGrid[((int)blueBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] != BLUE_TERRITORY_COLOR
                && !blueCaptureAborted){
            fieldGrid[((int)blueBallPosition.x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - FIELD_OFFSET) / FIELD_CELL_SIZE] = BLUE_TRACK_COLOR;
            blueCaptureBegin = true;
        }
        else{
            if(blueCaptureBegin){
                blueBall.setDefaultDirection();
                fieldFill(((int)(enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE),
                        (int)((689 - enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE), false);
                if(fieldGrid[((int)redBall.getPosition().x - FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] != LIGHT_GRAY_COLOR
                        && fieldGrid[((int)redBall.getPosition().x - FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] != BLUE_TERRITORY_COLOR
                        && fieldGrid[((int)redBall.getPosition().x - FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] != RED_TERRITORY_COLOR){

                    //System.out.println("bla");
                    fieldFill(((int)(redBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE),
                            (int)((689 - redBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE), false);
                }
                else if((redCaptureBegin && (
                        fieldGrid[((int)redBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                                || fieldGrid[((int)redBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                                || fieldGrid[((int)redBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)redBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)))
                {
                    //System.out.println("bla");
                    fieldFill(((int)(redBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE),
                            (int)((689 - redBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE), false);
                }

                //System.out.println("red");
                //System.out.println((redBall.getPosition().x + 15) / FIELD_CELL_SIZE);
                for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
                    for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++) {
                        if(fieldGrid[i][j] == 0)

                            fieldGrid[i][j] = BLUE_TERRITORY_COLOR;

                        if(fieldGrid[i][j] == BLUE_TEMP_COLOR)
                            fieldGrid[i][j] = 0;
                    }
                }
            }

            blueCaptureBegin = false;

            for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == BLUE_TRACK_COLOR)
                        fieldGrid[i][j] = BLUE_TERRITORY_COLOR;
                }
            }
        }

        for(int i = 0; i < FIELD_SIZE_X / FIELD_CELL_SIZE; i++){
            for(int j = 0; j < FIELD_SIZE_Y / FIELD_CELL_SIZE; j++){
                if(fieldGrid[i][j] == BLUE_TERRITORY_COLOR)
                    blueCellsCounter++;
                if(fieldGrid[i][j] == RED_TERRITORY_COLOR)
                    redCellsCounter++;
            }
        }



        redCapturedAreaPercent = redCellsCounter / seaAreaMaxSize * 100;
        blueCapturedAreaPercent = blueCellsCounter / seaAreaMaxSize * 100;

        redBallScore = redCapturedAreaPercent.toString().
                substring(0, redCapturedAreaPercent.toString().indexOf(".") + 2) + " %";
        blueBallScore = blueCapturedAreaPercent.toString().
                substring(0, blueCapturedAreaPercent.toString().indexOf(".") + 2) + " %";

        if(redCapturedAreaPercent >= areaToWin)
            if(seconds < 10 && seconds >= 0)
                xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition,
                        redCapturedAreaPercent.toString().substring(0, redCapturedAreaPercent.toString().indexOf(".") + 2) + " %",  blueCapturedAreaPercent.toString().substring(0,
                        blueCapturedAreaPercent.toString().indexOf(".") + 2) + " %", minutes + ":0" + seconds, nicknameRed, nicknameBlue, false));
            else
                xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition,
                        redCapturedAreaPercent.toString().substring(0, redCapturedAreaPercent.toString().indexOf(".") + 2) + " %",
                        blueCapturedAreaPercent.toString().substring(0, blueCapturedAreaPercent.toString().indexOf(".") + 2) + " %",
                        minutes + ":" + seconds, nicknameRed, nicknameBlue, false));

        if(blueCapturedAreaPercent >= areaToWin)
            if(seconds < 10 && seconds >= 0)
                xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition, blueCapturedAreaPercent.toString().substring(0,
                        blueCapturedAreaPercent.toString().indexOf(".") + 2) + " %",
                        redCapturedAreaPercent.toString().substring(0,
                                redCapturedAreaPercent.toString().indexOf(".") + 2) + " %", minutes + ":0" + seconds, nicknameBlue, nicknameRed, true));
            else
                xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition, blueCapturedAreaPercent.toString().substring(0,
                        blueCapturedAreaPercent.toString().indexOf(".") + 2) + " %",
                        redCapturedAreaPercent.toString().substring(0,
                                redCapturedAreaPercent.toString().indexOf(".") + 2) + " %", minutes + ":" + seconds, nicknameBlue, nicknameRed, true));

        if(minutes == 0 && seconds == 0)
            xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition,
                    redCapturedAreaPercent.toString().substring(0,
                            redCapturedAreaPercent.toString().indexOf(".") + 2) + " %", redCapturedAreaPercent.toString().substring(0,
                    redCapturedAreaPercent.toString().indexOf(".") + 2) + " %", minutes + ":" + seconds, nicknameBlue, nicknameRed, true));

        if((fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE  - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y  - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y  - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y  - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR)
                || (fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                && fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR)){
            enemyBall.setNewDirectionEdge();
        }
        else
        if(fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                || fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                || fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                || fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                || fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                || fieldGrid[((int)enemyBall.getPosition().x + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y + FIELD_CELL_SIZE - FIELD_OFFSET) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR){
            enemyBall.setNewDirectionHorizontal();
        }
        else if(fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - 15) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                || fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - 15) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                || fieldGrid[((int)enemyBall.getPosition().x + FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - 15) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR
                || fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - 15) / FIELD_CELL_SIZE] == LIGHT_GRAY_COLOR
                || fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - 15) / FIELD_CELL_SIZE] == RED_TERRITORY_COLOR
                || fieldGrid[((int)enemyBall.getPosition().x - FIELD_CELL_SIZE + FIELD_OFFSET) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - 15) / FIELD_CELL_SIZE] == BLUE_TERRITORY_COLOR){
            enemyBall.setNewDirectionVertical();
        }


        //fieldGrid[((int)enemyBall.getPosition().x + 15) / FIELD_CELL_SIZE][(689 - (int)enemyBall.getPosition().y - 15) / FIELD_CELL_SIZE] = 30;
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
        textFont.dispose();
        fieldPixmap.dispose();
        batch.dispose();
        background.dispose();
        shapeRenderer.dispose();
    }

    private void fieldFillIteration(int x, int y, int depth, boolean isRed){ // Idea from YouTube https://www.youtube.com/watch?v=_5W5sYjDBnA
        // and https://lodev.org/cgtutor/floodfill.html#Introduction_
        if(depth == 10){
            points.add(new Vector2(x, y));
            return;
        }

        if(fieldGrid[x][y] == 0) {
            if (isRed)
                fieldGrid[x][y] = RED_TEMP_COLOR;
            else
                fieldGrid[x][y] = BLUE_TEMP_COLOR;
        }
        if(fieldGrid[x- 1][y] == 0)
            fieldFillIteration(x - 1, y, depth + 1, isRed);
        if(fieldGrid[x + 1][y] == 0)
            fieldFillIteration(x + 1, y, depth + 1, isRed);
        if(fieldGrid[x][y - 1] == 0)
            fieldFillIteration(x, y - 1, depth + 1, isRed);
        if(fieldGrid[x][y + 1] == 0)
            fieldFillIteration(x, y + 1, depth + 1, isRed);
        if(fieldGrid[x- 1][y -1] == 0)
            fieldFillIteration(x - 1, y - 1, depth + 1, isRed);
        if(fieldGrid[x + 1][y + 1] == 0)
            fieldFillIteration(x + 1, y + 1, depth + 1, isRed);
        if(fieldGrid[x + 1][y - 1] == 0)
            fieldFillIteration(x + 1, y - 1, depth + 1, isRed);
        if(fieldGrid[x - 1][y + 1] == 0)
            fieldFillIteration(x - 1, y + 1, depth + 1, isRed);

    }

    private void fieldFill(int x, int y, boolean isRed) { // Idea from YouTube https://www.youtube.com/watch?v=_5W5sYjDBnA
        // and https://lodev.org/cgtutor/floodfill.html#Introduction_
        //System.out.println(points.size());
        //fieldGrid[x][y] = 2;
        points.add(new Vector2(x, y));

        for(int i = 0; i < points.size(); i++){
            fieldFillIteration((int)points.get(i).x, (int)points.get(i).y, 0, isRed);
            i = 0;
            points.remove(0);
        }

        if(points.size() != 0)
            points.remove(0);
    }
}

