package com.xonix_new_edition.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.xonix_new_edition.game.XonixNewEdition;

import java.io.IOException; //Source: Internet
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainWindow implements Screen {
    Stage stage;
    XonixNewEdition xonixNewEdition;
    Button joinButton;
    Button createButton;
    Button nicknameButton;
    SpriteBatch batch;
    Texture background;
    OrthographicCamera camera;
    BitmapFont nicknameTextFont;
    TextField nicknameField;
    String mainWindowNickname;
    String spaces;
    int nicknameLength;

    static private Socket socket; //Source: Internet
    static private ObjectInputStream inputStream;
    static private ObjectOutputStream outputStream;


    public MainWindow(final XonixNewEdition xonixNewEdition, final String nickname){
        this.mainWindowNickname = nickname;
        this.xonixNewEdition = xonixNewEdition;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        nicknameTextFont = new BitmapFont(Gdx.files.internal("font2.fnt"));
        nicknameTextFont.setColor(Color.BLACK);
        nicknameLength = nickname.length();

        //nicknameTextFont.getData().setScale(1);

        batch = new SpriteBatch();
       // skin = new Skin(Gdx.files.internal("skin.json"));
       // nicknameField = new TextField("Enter your nickname", skin);

        joinButton = new Button(xonixNewEdition, "create_button_up.png",
                "rect_button_down.png", 1000, 200);
        joinButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                xonixNewEdition.setScreen(new ConfigurationWindow(xonixNewEdition, mainWindowNickname));
            }
        });
        stage.addActor(joinButton.textButton);

        createButton = new Button(xonixNewEdition, "join_button_up.png",
                "rect_button_down.png", 1000, 300);
        createButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try { //Source: Internet
                    socket = new Socket("localhost", 19000);

                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    inputStream = new ObjectInputStream(socket.getInputStream());

                    String message = "Hello, World!";
                    outputStream.write(message.getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    xonixNewEdition.setScreen(new ErrorWindow(xonixNewEdition, mainWindowNickname, false));
                }
            }
        });
        stage.addActor(createButton.textButton);

        nicknameButton = new Button(xonixNewEdition, "nickname_button_up.png",
                "rect_button_down.png", 1000, 100);
        nicknameButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Input.TextInputListener textListener = new Input.TextInputListener()
                {
                    @Override
                    public void input(String input)
                    {
                        if(input.length() > 12)
                            input = input.substring(0, 12);
                        nicknameLength = input.length();
                        mainWindowNickname = input;
                        System.out.println(input);
                    }

                    @Override
                    public void canceled()
                    {
                    }
                };

                Gdx.input.getTextInput(textListener, "Enter your nickname (12 symbols): ", "", "");
            }
        });

        stage.addActor(nicknameButton.textButton);
        //Gdx.input.setInputProcessor(nicknameButton);

        background = new Texture("background_new.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        //batch.draw(img, 0, 0);
        batch.draw(background, 0, 0);
        spaces = "";
        for(int i = 0; i < 12 - nicknameLength; i++){
            spaces += "_";
        }
        nicknameTextFont.draw(batch,"Nickname: " + spaces + mainWindowNickname, 500, 155);
        batch.end();
        //joinButton.draw();
        stage.draw();
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
        nicknameTextFont.dispose();
    }
}
