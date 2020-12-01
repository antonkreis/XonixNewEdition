package com.xonix_new_edition.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.xonix_new_edition.game.XonixNewEdition;

public class Button{
    private Texture buttonImageUp;
    private Texture buttonImageDown;
    public TextButton textButton;
    private XonixNewEdition xonixNewEdition;
    private Vector2 position;

    public Button(final XonixNewEdition xonixNewEdition, String fileNameUp, String fileNameDown, int x, int y){
        this.xonixNewEdition = xonixNewEdition;
        buttonImageUp = new Texture(fileNameUp);
        buttonImageDown = new Texture(fileNameDown);

        BitmapFont bitmapFont = new BitmapFont();
        bitmapFont.getData().setScale(2);
        bitmapFont.setColor(Color.BLACK);
        Skin skin = new Skin();

        skin.add(fileNameUp.substring(0, fileNameUp.length()-4), buttonImageUp);
        skin.add(fileNameDown.substring(0, fileNameDown.length()-4), buttonImageDown);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable(fileNameUp.substring(0, fileNameUp.length()-4));
        textButtonStyle.down = skin.getDrawable(fileNameDown.substring(0, fileNameDown.length()-4));
        textButtonStyle.checked = skin.getDrawable(fileNameUp.substring(0, fileNameUp.length()-4));
        textButtonStyle.font = bitmapFont;
        textButtonStyle.fontColor = Color.BLACK;

        position = new Vector2(x, y);
        textButton = new TextButton("", textButtonStyle);
        textButton.setPosition(position.x, position.y);

//        textButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                System.out.println("Hi");
//                //xonixNewEdition.setScreen(new GameWindow(xonixNewEdition));
//            }
//        });
    }
}
