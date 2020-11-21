package com.xonix_new_edition.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class RedBall {
    Texture texture;
    Vector2 position;
    int speed = 5;

    public enum RedBallDirection{
        DEFAULT,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    RedBallDirection direction;

    RedBall(int speed){
        this.speed = speed;
        texture = new Texture("red_ball1.png");
        position = new Vector2(500, 400);
        direction = RedBallDirection.DEFAULT;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public RedBallDirection getDirection(){
        return direction;
    }

    public void setDefaultDirection(){
        direction = RedBallDirection.DEFAULT;
    }

    public Vector2 getPosition(){
        return position;
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, position.x, position.y);
    }

    public void update(){

    }
}

