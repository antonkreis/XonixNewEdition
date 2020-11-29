package com.xonix_new_edition.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.xonix_new_edition.game.XonixNewEdition;

public class StatisticsWindow implements Screen {
    Stage stage;
    XonixNewEdition xonixNewEdition;
    SpriteBatch batch;
    Texture background;
    Button backButton;
    OrthographicCamera camera;
    BitmapFont textFont;
    String timeout;
    String winnerCapturedAreaPercent;
    String place2CapturedAreaPercent;
    String winnerLabel;
    String place2Label;
    String timeSpentLabel;
    String winnerNickname;
    String place2Nickname;
    String drawLabel;
    boolean isBlueNicknameWinner;
    boolean isDraw;

    StatisticsWindow(final XonixNewEdition xonixNewEdition, String winnerCapturedAreaPercent,
                     String place2CapturedAreaPercent, String timeout, final String winnerNickname,
                     final String place2Nickname, boolean isBlueNicknameWinner){
        this.xonixNewEdition = xonixNewEdition;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        this.winnerCapturedAreaPercent = winnerCapturedAreaPercent;
        this.place2CapturedAreaPercent = place2CapturedAreaPercent;
        this.timeout = timeout;
        this.winnerNickname = winnerNickname;
        this.place2Nickname = place2Nickname;
        this.isBlueNicknameWinner = isBlueNicknameWinner;

        if(winnerCapturedAreaPercent.equals(place2CapturedAreaPercent) )
            this.isDraw = true;
        else
            this.isDraw = false;

        batch = new SpriteBatch();
        background = new Texture("statistics_background.png");

        backButton = new Button(xonixNewEdition, "back_button.png",
                "rect_button_down.png", 1000, 100);

        backButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                xonixNewEdition.setScreen(new MainWindow(xonixNewEdition, winnerNickname, place2Nickname));
            }
        });
        stage.addActor(backButton.textButton);

        textFont = new BitmapFont(Gdx.files.internal("font2.fnt"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        winnerLabel = "The winner:";
        place2Label = "The second place:";
        timeSpentLabel = "Time spent: ";
        drawLabel = "DRAW!";
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0);
        textFont.setColor(Color.BLACK);
        if(isDraw)
            textFont.draw(batch, drawLabel, 800, 560);
        else{
            textFont.draw(batch, winnerLabel, 800, 560);
            textFont.draw(batch, place2Label, 800, 460);
        }


        if(isBlueNicknameWinner)
            textFont.setColor(Color.BLUE);
        else
            textFont.setColor(Color.RED);
        textFont.draw(batch, winnerNickname + " - " + winnerCapturedAreaPercent, 810, 510);

        if(isBlueNicknameWinner)
            textFont.setColor(Color.RED);
        else
            textFont.setColor(Color.BLUE);
        textFont.draw(batch, place2Nickname + " - " + place2CapturedAreaPercent, 810, 410);
        textFont.setColor(Color.BLACK);
        textFont.draw(batch, timeSpentLabel + timeout, 700, 310);

        batch.end();

        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

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
    }
}
