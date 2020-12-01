package com.xonix_new_edition.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class RedBall {
    private Texture texture;
    private Vector2 position;
    private int speed = 5;

    public enum RedBallDirection{
        DEFAULT,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    private RedBallDirection direction;

    RedBall(int speed){
        this.speed = speed;
        texture = new Texture("red_ball1.png");
        position = new Vector2(935, 645);
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

    public void setPosition(int x, int y){
        position.x = x;
        position.y = y;
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, position.x, position.y);
    }

    public void update(){
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            if(position.x <= 10)
                return;
            direction = RedBall.RedBallDirection.LEFT;
            position.x -= speed;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.D)){
            if(position.x >= 935)
                return;
            direction = RedBall.RedBallDirection.RIGHT;
            position.x += speed;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.W)){
            if(position.y >= 645)
                return;
            direction = RedBall.RedBallDirection.UP;
            position.y += speed;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.S)){
            if(position.y <= 10)
                return;
            direction = RedBall.RedBallDirection.DOWN;
            position.y -= speed;
        }
        else if(direction == RedBall.RedBallDirection.LEFT){
            if(position.x <= 10)
                return;
            position.x -= speed;
        }
        else if(direction == RedBall.RedBallDirection.RIGHT){
            if(position.x >= 935)
                return;
            position.x += speed;
        }
        else if(direction == RedBall.RedBallDirection.UP){
            if(position.y >= 645)
                return;
            position.y += speed;
        }
        else if(direction == RedBall.RedBallDirection.DOWN){
            if(position.y <= 10)
                return;
            position.y -= speed;
        }
    }
}

