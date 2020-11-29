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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.xonix_new_edition.game.XonixNewEdition;

public class MainWindow implements Screen {
    Stage stage;
    XonixNewEdition xonixNewEdition;
    Button joinButton;
    Button nicknameRedButton;
    Button nicknameBlueButton;
    SpriteBatch batch;
    Texture background;
    OrthographicCamera camera;
    BitmapFont nicknameTextFont;
    String mainWindowNicknameBlue;
    String mainWindowNicknameRed;
    String spaces;
    int nicknameLengthBlue;
    int nicknameLengthRed;


    public MainWindow(final XonixNewEdition xonixNewEdition, final String nickname){
        this.mainWindowNicknameBlue = nickname;
        this.mainWindowNicknameRed = nickname;
        this.xonixNewEdition = xonixNewEdition;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        nicknameTextFont = new BitmapFont(Gdx.files.internal("font2.fnt"));
        nicknameLengthBlue = nickname.length();
        nicknameLengthRed = nickname.length();

        batch = new SpriteBatch();

        joinButton = new Button(xonixNewEdition, "join_button_up.png",
                "rect_button_down.png", 1000, 300);
        joinButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                xonixNewEdition.setScreen(new ConfigurationWindow(xonixNewEdition, mainWindowNicknameBlue));
            }
        });
        stage.addActor(joinButton.textButton);

        nicknameRedButton = new Button(xonixNewEdition, "nickname_button_up.png",
                "rect_button_down.png", 1000, 200);
        nicknameRedButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Input.TextInputListener textListener = new Input.TextInputListener()
                {
                    @Override
                    public void input(String input)
                    {
                        if(input.length() > 12)
                            input = input.substring(0, 12);
                        nicknameLengthRed = input.length();
                        mainWindowNicknameRed = input;
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
        stage.addActor(nicknameRedButton.textButton);

        nicknameBlueButton = new Button(xonixNewEdition, "nickname_button_up.png",
                "rect_button_down.png", 1000, 100);
        nicknameBlueButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Input.TextInputListener textListener = new Input.TextInputListener()
                {
                    @Override
                    public void input(String input)
                    {
                        if(input.length() > 12)
                            input = input.substring(0, 12);
                        nicknameLengthBlue = input.length();
                        mainWindowNicknameBlue = input;
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

        stage.addActor(nicknameBlueButton.textButton);
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
        for(int i = 0; i < 12 - nicknameLengthRed; i++){
            spaces += "_";
        }
        nicknameTextFont.setColor(Color.RED);
        nicknameTextFont.draw(batch,"Nickname: " + spaces + mainWindowNicknameRed, 500, 255);
        spaces = "";
        for(int i = 0; i < 12 - nicknameLengthBlue; i++){
            spaces += "_";
        }
        nicknameTextFont.setColor(Color.BLUE);
        nicknameTextFont.draw(batch,"Nickname: " + spaces + mainWindowNicknameBlue, 500, 155);
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
