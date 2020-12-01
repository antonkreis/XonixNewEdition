package com.xonix_new_edition.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class BlueBall {
    private Texture texture;
    private Vector2 position;
    private int speed = 5;

    public enum BlueBallDirection{
        DEFAULT,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    private BlueBallDirection direction;

    BlueBall(int speed){
        this.speed = speed;
        texture = new Texture("blue_ball1.png");
        position = new Vector2(10, 10);
        direction = BlueBallDirection.DEFAULT;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public BlueBallDirection getDirection(){
        return direction;
    }

    public void setDefaultDirection(){
        direction = BlueBallDirection.DEFAULT;
    }

    public Vector2 getPosition(){
        return position;
    }

    public void setPosition(int x, int y){
        position.x = x;
        position.y = y;
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, position.x, position.y);
    }

    public void update(){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            if(position.x <= 10)
                return;
            direction = BlueBallDirection.LEFT;
            position.x -= speed;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            if(position.x >= 935)
                return;
            direction = BlueBallDirection.RIGHT;
            position.x += speed;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            if(position.y >= 645)
                return;
            direction = BlueBallDirection.UP;
            position.y += speed;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            if(position.y <= 10)
                return;
            direction = BlueBallDirection.DOWN;
            position.y -= speed;
        }
        else if(direction == BlueBallDirection.LEFT){
            if(position.x <= 10)
                return;
            position.x -= speed;
        }
        else if(direction == BlueBallDirection.RIGHT){
            if(position.x >= 935)
                return;
            position.x += speed;
        }
        else if(direction == BlueBallDirection.UP){
            if(position.y >= 645)
                return;
            position.y += speed;
        }
        else if(direction == BlueBallDirection.DOWN){
            if(position.y <= 10)
                return;
            position.y -= speed;
        }
    }
}
