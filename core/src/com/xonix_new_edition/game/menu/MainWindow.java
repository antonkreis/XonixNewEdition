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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.xonix_new_edition.game.XonixNewEdition;

public class MainWindow implements Screen {
    Stage stage;
    XonixNewEdition xonixNewEdition;
    Button joinButton;
    Button nicknameButton;
    SpriteBatch batch;
    Texture background;
    OrthographicCamera camera;
    BitmapFont nicknameTextFont;
    TextField nicknameField;
    String nickname;
    String spaces;
    int nicknameLength;

    public MainWindow(final XonixNewEdition xonixNewEdition){
        nickname = "player";
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

        joinButton = new Button(xonixNewEdition, "join_button_up.png",
                "rect_button_down.png", 1000, 200);
        joinButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //System.out.println("Hi");
                xonixNewEdition.setScreen(new ConfigurationWindow(xonixNewEdition, nickname));
            }
        });
        stage.addActor(joinButton.textButton);

        nicknameButton = new Button(xonixNewEdition, "nickname_button_up.png",
                "rect_button_down.png", 1000, 100);
        nicknameButton.textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //System.out.println("Hi");
                Input.TextInputListener textListener = new Input.TextInputListener()
                {
                    @Override
                    public void input(String input)
                    {
                        if(input.length() > 16)
                            input = input.substring(0, 16);
                        nicknameLength = input.length();
                        nickname = input;
                        System.out.println(input);
                    }

                    @Override
                    public void canceled()
                    {
                        System.out.println("Aborted");
                    }
                };

                Gdx.input.getTextInput(textListener, "Enter your nickname: ", "", "");
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
        for(int i = 0; i < 16 - nicknameLength; i++){
            spaces += "_";
        }
        nicknameTextFont.draw(batch,"Nickname: " + spaces + nickname, 430, 155);
        //System.out.println(spaces+ nickname);
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
