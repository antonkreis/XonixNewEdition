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

import java.io.IOException;
import java.io.ObjectInputStream; //Source: Internet
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameWindowClient implements Screen {
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
    RedBall.RedBallDirection currentDirection;

    boolean redCaptureBegin;
    boolean blueCaptureBegin;
    boolean redCaptureAborted;
    boolean blueCaptureAborted;

    ArrayList<Vector2> points;

    Texture fieldTexture;
    Pixmap fieldPixmap;

    BitmapFont textFont;
    String blueBallScore;
    String redBallScore;
    String blueBallScoreLabel;
    String redBallScoreLabel;
    String timeLabel;
    String time;
    String nickname;
    String partnerNickname;

    String timeout;
    int timeoutInt;
    int areaToWin;
    float rawTimeSinceStart = 0;
    int minutes;
    int seconds;

    private static ServerSocket serverSocket; //Source: Internet
    private static Socket socket;
    private static ObjectOutputStream outputStream;
    private static ObjectInputStream inputStream;
    private static int readBytes;

    byte[] configurationInputSequence;
    byte[] configurationOutputSequence;
    byte[] gameStatusInputSequence;
    byte[] gameStatusOutputSequence;

    int redBallPositionX;
    int redBallPositionY;
    int blueBallPositionX;
    int blueBallPositionY;

    GameWindowClient(final XonixNewEdition xonixNewEdition, final String nickname, final Socket socket){
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
                xonixNewEdition.setScreen(new MainWindow(xonixNewEdition, nickname));
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        stage.addActor(leaveButton.textButton);

        Gdx.gl.glLineWidth(5);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(Color.RED);

        amountOfLines = 1;
        currentDirection = RedBall.RedBallDirection.DEFAULT;

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

        redCaptureBegin = false;
        blueCaptureBegin = false;
        redCaptureAborted = false;
        blueCaptureAborted = false;

        points = new ArrayList<>();

        blueBallScore = "0.0 %";
        redBallScore = "0.0 %";
        blueBallScoreLabel = "Score:";
        redBallScoreLabel = "Score:";

        textFont = new BitmapFont(Gdx.files.internal("font2.fnt"));

        configurationInputSequence = new byte[14];
        configurationOutputSequence = new byte[12];

        configurationOutputSequence = nickname.getBytes();

        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            inputStream.read(configurationInputSequence);

            outputStream.write(configurationOutputSequence);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        areaToWin = configurationInputSequence[12];
        timeoutInt = configurationInputSequence[13];
        this.nickname = nickname;
        String partnerNicknameTemp = new String(configurationInputSequence);
        if(partnerNicknameTemp.length() > 12)
            partnerNicknameTemp = partnerNicknameTemp.substring(0, 12);
        if(partnerNicknameTemp.contains("\0"))
            partnerNicknameTemp = partnerNicknameTemp.substring(0, partnerNicknameTemp.indexOf("\0"));

        partnerNickname = partnerNicknameTemp;

        timeLabel = "Time:";
        time =  timeoutInt + ".00";
        minutes = timeoutInt;
        seconds = 0;

        gameStatusInputSequence = new byte[5];
        gameStatusOutputSequence = new byte[5];
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
                else if(fieldGrid[i][j] == 11){
                    fieldPixmap.setColor(Color.RED);
                    fieldPixmap.fillRectangle(FIELD_OFFSET + i * FIELD_CELL_SIZE, FIELD_OFFSET + j * FIELD_CELL_SIZE,
                            FIELD_CELL_SIZE, FIELD_CELL_SIZE);
                }
                else if(fieldGrid[i][j] == 12){
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

        currentDirection = redBall.getDirection();
        redBall.update();

        batch.begin();
        batch.draw(background, 0, 0);
        batch.draw(fieldTexture, 0, 0);
        textFont.setColor(Color.BLACK);
        textFont.draw(batch, blueBallScoreLabel, 1020, 700);
        textFont.setColor(Color.BLUE);
        textFont.draw(batch, partnerNickname + ":", 1020, 650);
        textFont.draw(batch, blueBallScore, 1090, 600);
        textFont.setColor(Color.RED);
        textFont.draw(batch, nickname + ":", 1020, 550);
        textFont.draw(batch, redBallScore, 1090, 500);
        textFont.setColor(Color.BLACK);
        textFont.draw(batch, timeLabel, 1020, 450);
        if(seconds < 10 && seconds >= 0)
            textFont.draw(batch, minutes + ":0" + seconds, 1090, 400);
        else
            textFont.draw(batch, minutes + ":" + seconds, 1090, 400);
        blueBall.render(batch);
        redBall.render(batch);
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

        redBallPositionX = (int)redBall.getPosition().x;
        redBallPositionY = (int)redBall.getPosition().y;
        System.out.println("a");


        for(int i = 0; i < 5; i++){
            gameStatusOutputSequence[i] = 0;
        }

        for (int i = 0; i < 8; i++){
            if ((redBallPositionX & (1 << (i + 8))) != 0)
                gameStatusOutputSequence[0] |= (1 << i);
            if ((redBallPositionX & (1 << i)) != 0)
                gameStatusOutputSequence[1] |= (1 << i);

            if ((redBallPositionY & (1 << (i + 8))) != 0)
                gameStatusOutputSequence[2] |= (1 << i);
            if ((redBallPositionY & (1 << i)) != 0)
                gameStatusOutputSequence[3] |= (1 << i);
        }

        System.out.println( gameStatusOutputSequence[0]);

        try {
            outputStream.write(gameStatusOutputSequence);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStream.read(gameStatusInputSequence);
        } catch (IOException e) {
            e.printStackTrace();
        }

        blueBallPositionX = 0;
        blueBallPositionY = 0;

        for (int i = 0; i < 8; i++){
            if ((gameStatusInputSequence[0] & (1 << i)) != 0)
                blueBallPositionX |= (1 << (i + 8));
            if ((gameStatusInputSequence[1] & (1 << i)) != 0)
                blueBallPositionX |= (1 << (i ));
            if ((gameStatusInputSequence[2] & (1 << i)) != 0)
                blueBallPositionY |= (1 << (i + 8));
            if ((gameStatusInputSequence[3] & (1 << i)) != 0)
                blueBallPositionY |= (1 << (i ));
        }

        blueBall.setPosition(blueBallPositionX, blueBallPositionY);


        Vector2 redBallPosition = redBall.getPosition();
        Vector2 blueBallPosition = blueBall.getPosition();
        float redCellsCounter = 0;
        float blueCellsCounter = 0;
        int seaAreaMaxSize = (980 / FIELD_CELL_SIZE - 7 - 7) * (690 / FIELD_CELL_SIZE - 7 - 7);
        Float redCapturedAreaPercent;
        Float blueCapturedAreaPercent;

        if(fieldGrid[((int)redBallPosition.x + 15) / 5][(659 - (int)redBallPosition.y + 15) / 5] == 11){
            redCaptureBegin = false;
            redCaptureAborted = true;
            for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == 11)
                        fieldGrid[i][j] = 0;
                }
            }
        }

        if((fieldGrid[((int)redBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - 15) / FIELD_CELL_SIZE] == 3
                || fieldGrid[((int)redBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - 15) / FIELD_CELL_SIZE] == 12
                || fieldGrid[((int)redBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - 15) / FIELD_CELL_SIZE] == 2)
                && redCaptureAborted) {
            redCaptureAborted = false;
            redBall.setDefaultDirection();
        }

        if(fieldGrid[((int)blueBallPosition.x + 15) / 5][(659 - (int)blueBallPosition.y + 15) / 5] == 1){
            blueCaptureBegin = false;
            for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == 1)
                        fieldGrid[i][j] = 0;
                }
            }
        }

        if(fieldGrid[((int)redBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - 15) / FIELD_CELL_SIZE] != 3
                && fieldGrid[((int)redBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - 15) / FIELD_CELL_SIZE] != 12
                && fieldGrid[((int)redBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - 15) / FIELD_CELL_SIZE] != 2
                && !redCaptureAborted){
            fieldGrid[((int)redBallPosition.x + 15) / 5][(689 - (int)redBallPosition.y - 15) / 5] = 11;
            redCaptureBegin = true;
        }
        else{
            if(redCaptureBegin){
                redBall.setDefaultDirection();
                if(fieldGrid[((int)blueBall.getPosition().x + 15) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - 15) / FIELD_CELL_SIZE] != 3
                        && fieldGrid[((int)blueBall.getPosition().x + 15) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - 15) / FIELD_CELL_SIZE] != 12
                        && fieldGrid[((int)blueBall.getPosition().x + 15) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - 15) / FIELD_CELL_SIZE] != 2)
                    fieldFill(((int)(blueBall.getPosition().x - 15) / FIELD_CELL_SIZE),
                            (int)((689 - blueBall.getPosition().y - 15) / FIELD_CELL_SIZE), true);
                else
                    for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
                        for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
                            if(fieldGrid[i][j] == 11)
                                fieldGrid[i][j] = 0;
                        }
                    }
            }

            redCaptureBegin = false;

            for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == 11)
                        fieldGrid[i][j] = 12;
                    if(fieldGrid[i][j] == 12)
                        redCellsCounter++;
                }
            }

            redCapturedAreaPercent = redCellsCounter / seaAreaMaxSize * 100;

            redBallScore = redCapturedAreaPercent.toString().
                    substring(0, redCapturedAreaPercent.toString().indexOf(".") + 2) + " %";

            if(redCapturedAreaPercent >= areaToWin || (minutes == 0 && seconds == 0))
                if(seconds < 10 && seconds >= 0)
                    xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition,
                            redCapturedAreaPercent.toString().substring(0,
                                    redCapturedAreaPercent.toString().indexOf(".") + 2) + " %", minutes + ":0" + seconds, nickname, " "));
                else
                    xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition,
                            redCapturedAreaPercent.toString().substring(0,
                                    redCapturedAreaPercent.toString().indexOf(".") + 2) + " %", minutes + ":" + seconds, nickname, " "));
        }

        if(fieldGrid[((int)blueBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - 15) / FIELD_CELL_SIZE] != 3
                && fieldGrid[((int)blueBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)blueBallPosition.y - 15) / FIELD_CELL_SIZE] != 2){
            fieldGrid[((int)blueBallPosition.x + 15) / 5][(689 - (int)blueBallPosition.y - 15) / 5] = 1;
            blueCaptureBegin = true;
        }
        else{
            if(blueCaptureBegin){
                blueBall.setDefaultDirection();
                if(fieldGrid[((int)redBall.getPosition().x + 15) / FIELD_CELL_SIZE][(689 - (int)redBall.getPosition().y - 15) / FIELD_CELL_SIZE] != 3
                        && fieldGrid[((int)redBall.getPosition().x + 15) / FIELD_CELL_SIZE][(689 - (int)redBall.getPosition().y - 15) / FIELD_CELL_SIZE] != 2
                        && fieldGrid[((int)redBall.getPosition().x + 15) / FIELD_CELL_SIZE][(689 - (int)redBall.getPosition().y - 15) / FIELD_CELL_SIZE] != 12)
                    fieldFill(((int)(redBall.getPosition().x - 15) / FIELD_CELL_SIZE),
                            (int)((689 - redBall.getPosition().y - 15) / FIELD_CELL_SIZE), false);
            }

            blueCaptureBegin = false;

            for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == 1)
                        fieldGrid[i][j] = 2;
                    if(fieldGrid[i][j] == 2)
                        blueCellsCounter++;
                }
            }

            redCapturedAreaPercent = redCellsCounter / seaAreaMaxSize * 100;
            blueCapturedAreaPercent = blueCellsCounter / seaAreaMaxSize * 100;

            redBallScore = redCapturedAreaPercent.toString().
                    substring(0, redCapturedAreaPercent.toString().indexOf(".") + 2) + " %";
            blueBallScore = blueCapturedAreaPercent.toString().
                    substring(0, blueCapturedAreaPercent.toString().indexOf(".") + 2) + " %";

            if(blueCapturedAreaPercent >= areaToWin || (minutes == 0 && seconds == 0))
                if(seconds < 10 && seconds >= 0)
                    xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition,
                            redCapturedAreaPercent.toString().substring(0,
                                    redCapturedAreaPercent.toString().indexOf(".") + 2) + " %", minutes + ":0" + seconds, nickname, " "));
                else
                    xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition,
                            redCapturedAreaPercent.toString().substring(0,
                                    redCapturedAreaPercent.toString().indexOf(".") + 2) + " %", minutes + ":" + seconds, nickname, " "));
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

        if(fieldGrid[x][y] == 0){
            if(isRed)
                fieldGrid[x][y] = 17;
            else
                fieldGrid[x][y] = 7;

            fieldFillIteration(x - 1, y, depth + 1, isRed);
            fieldFillIteration(x + 1, y, depth + 1, isRed);
            fieldFillIteration(x, y - 1, depth + 1, isRed);
            fieldFillIteration(x, y + 1, depth + 1, isRed);
        }
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

        for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
            for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++) {
                if(fieldGrid[i][j] == 0)
                    if(isRed)
                        fieldGrid[i][j] = 12;
                    else
                        fieldGrid[i][j] = 2;
                if(fieldGrid[i][j] == 17 && isRed)
                    fieldGrid[i][j] = 0;
                if(fieldGrid[i][j] == 7 && !isRed)
                    fieldGrid[i][j] = 0;
            }
        }
    }
}
