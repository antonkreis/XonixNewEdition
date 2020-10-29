package com.xonix_new_edition.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class BlueBall {
    Texture texture;
    Vector2 position;
    int step = 5;

    enum BlueBallDirection{
        DEFAULT,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    BlueBallDirection blueBallDirection;

    BlueBall(int step){
        this.step = step;
        texture = new Texture("blue_ball.png");
        position = new Vector2(10, 10);
        blueBallDirection = BlueBallDirection.DEFAULT;
    }

    public void setStep(int step){
        this.step = step;
    }

    public Vector2 getPosition(){
        return position;
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, position.x, position.y);
    }

    public void update(){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            if(position.x <= 10)
                return;
            blueBallDirection = BlueBallDirection.LEFT;
            position.x -= step;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            if(position.x >= 930)
                return;
            blueBallDirection = BlueBallDirection.RIGHT;
            position.x += step;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            if(position.y >= 640)
                return;
            blueBallDirection = BlueBallDirection.UP;
            position.y += step;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            if(position.y <= 10)
                return;
            blueBallDirection = BlueBallDirection.DOWN;
            position.y -= step;
        }
    }
}
