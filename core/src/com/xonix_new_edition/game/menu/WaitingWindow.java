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

import java.io.IOException; //Source: Internet
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WaitingWindow implements Screen {
    Stage stage;
    XonixNewEdition xonixNewEdition;
    Button backButton;
    SpriteBatch batch;
    Texture background;
    OrthographicCamera camera;
    BitmapFont textFont;
    String timeout;
    String areaToWin;
    String nickname;
    int i;

    private static ServerSocket serverSocket; //Source: Internet
    private static Socket socket;
    private static ObjectOutputStream outputStream;
    private static ObjectInputStream inputStream;
    private static int readBytes;

    WaitingWindow(final XonixNewEdition xonixNewEdition, String timeout, String areaToWin, final String nickname){
        this.xonixNewEdition = xonixNewEdition;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        this.nickname = nickname;
        this.timeout = timeout;
        this.areaToWin = areaToWin;

        batch = new SpriteBatch();
        background = new Texture("background_waiting_window.png");

        backButton = new Button(xonixNewEdition, "back_button.png",
                "rect_button_down.png", 1000, 100);

        backButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });
        stage.addActor(backButton.textButton);

        textFont = new BitmapFont(Gdx.files.internal("font2.fnt"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
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
        batch.end();

        stage.draw();

        if(i == 1){
            try { //Source: Internet
                serverSocket = new ServerSocket(19000);
                serverSocket.setSoTimeout(3000);
                socket = serverSocket.accept();

                System.out.println("Connection established");

                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

                byte[] buffer = new byte[20];
                readBytes = inputStream.read(buffer);
                String result = new String(buffer, 0, readBytes);
                System.out.println(result);

                serverSocket.close();

                xonixNewEdition.setScreen(new GameWindow(xonixNewEdition, timeout, areaToWin, nickname));

            } catch (IOException exception1) {
                try{
                    serverSocket.close();
                } catch (IOException exception2){

                }
                xonixNewEdition.setScreen(new ErrorWindow(xonixNewEdition, nickname, true));
            }
        }

        i++;
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
