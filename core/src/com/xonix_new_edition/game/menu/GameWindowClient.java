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
import java.io.InputStream;
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

    boolean captureBegin;
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

        captureBegin = false;

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

        try {
            outputStream.write(configurationOutputSequence);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Vector2 redBallPosition = redBall.getPosition();
        float redCellsCounter = 0;
        int seaAreaMaxSize = (980 / FIELD_CELL_SIZE - 7 - 7) * (690 / FIELD_CELL_SIZE - 7 - 7);
        Float capturedAreaPercent;

        if(fieldGrid[((int)redBallPosition.x + 15) / 5][(659 - (int)redBallPosition.y + 15) / 5] == 11){
            captureBegin = false;
            for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == 11)
                        fieldGrid[i][j] = 0;
                }
            }
        }

        if(fieldGrid[((int)redBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - 15) / FIELD_CELL_SIZE] != 3
                && fieldGrid[((int)redBallPosition.x + 15) / FIELD_CELL_SIZE][(689 - (int)redBallPosition.y - 15) / FIELD_CELL_SIZE] != 12){
            fieldGrid[((int)redBallPosition.x + 15) / 5][(689 - (int)redBallPosition.y - 15) / 5] = 11;
            captureBegin = true;
        }

        else{
            if(captureBegin){
                redBall.setDefaultDirection();
                if(fieldGrid[((int)blueBall.getPosition().x + 15) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - 15) / FIELD_CELL_SIZE] != 3
                        && fieldGrid[((int)blueBall.getPosition().x + 15) / FIELD_CELL_SIZE][(689 - (int)blueBall.getPosition().y - 15) / FIELD_CELL_SIZE] != 12)
                    fieldFill(((int)(redBall.getPosition().x - 15) / FIELD_CELL_SIZE),
                            (int)((689 - redBall.getPosition().y - 15) / FIELD_CELL_SIZE));
            }

            captureBegin = false;

            for(int i = 0; i < 980 / FIELD_CELL_SIZE; i++){
                for(int j = 0; j < 690 / FIELD_CELL_SIZE; j++){
                    if(fieldGrid[i][j] == 11)
                        fieldGrid[i][j] = 12;
                    if(fieldGrid[i][j] == 12)
                        redCellsCounter++;
                }
            }

            capturedAreaPercent = redCellsCounter / seaAreaMaxSize * 100;

            redBallScore = capturedAreaPercent.toString().
                    substring(0, capturedAreaPercent.toString().indexOf(".") + 2) + " %";

            if(capturedAreaPercent >= areaToWin || (minutes == 0 && seconds == 0))
                if(seconds < 10 && seconds >= 0)
                    xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition,
                            capturedAreaPercent.toString().substring(0,
                                    capturedAreaPercent.toString().indexOf(".") + 2) + " %", minutes + ":0" + seconds, nickname, " "));
                else
                    xonixNewEdition.setScreen(new StatisticsWindow(xonixNewEdition,
                            capturedAreaPercent.toString().substring(0,
                                    capturedAreaPercent.toString().indexOf(".") + 2) + " %", minutes + ":" + seconds, nickname, " "));
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

    private void fieldFillIteration(int x, int y, int depth){ // Idea from YouTube https://www.youtube.com/watch?v=_5W5sYjDBnA
        // and https://lodev.org/cgtutor/floodfill.html#Introduction_
        if(depth == 10){
            points.add(new Vector2(x, y));
            return;
        }

        if(fieldGrid[x][y] == 0){
            fieldGrid[x][y] = 17;

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
                    fieldGrid[i][j] = 12;
                if(fieldGrid[i][j] == 17)
                    fieldGrid[i][j] = 0;
            }
        }
    }
}
