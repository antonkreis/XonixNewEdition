package com.xonix_new_edition.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class EnemyBall {
    Texture texture;
    Vector2 position;
    int speed = 5;

    public enum EnemyBallDirection {
        DEFAULT,
        LEFT,
        UP_LEFT,
        RIGHT,
        UP_RIGHT,
        UP,
        DOWN_RIGHT,
        DOWN,
        DOWN_LEFT,
    }

    EnemyBallDirection direction;

    EnemyBall(int speed){
        this.speed = speed;
        texture = new Texture("enemy_ball1.png");
        position = new Vector2(100, 100);
        direction = EnemyBallDirection.DEFAULT;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public EnemyBallDirection getDirection(){
        return direction;
    }

    public void setDefaultDirection(){
        direction = EnemyBallDirection.DEFAULT;
    }

    public Vector2 getPosition(){
        return position;
    }

    public void setRandomDirection(){
        Random random = new Random();
        int newDirection = random.nextInt(3);

        switch (newDirection){
            case 0: direction = EnemyBallDirection.DOWN_LEFT; break;
            case 1: direction = EnemyBallDirection.UP_LEFT; break;
            case 2: direction = EnemyBallDirection.UP_RIGHT; break;
            case 3: direction = EnemyBallDirection.DOWN_RIGHT; break;
        }
    }

    public void setNewDirectionVertical(){
        switch (direction){
            case UP_LEFT: direction = EnemyBallDirection.UP_RIGHT; break;
            case UP_RIGHT: direction = EnemyBallDirection.UP_LEFT; break;
            case DOWN_LEFT: direction = EnemyBallDirection.DOWN_RIGHT; break;
            case DOWN_RIGHT: direction = EnemyBallDirection.DOWN_LEFT; break;
        }
    }

    public void setNewDirectionHorizontal(){
        switch (direction){
            case UP_LEFT: direction = EnemyBallDirection.DOWN_LEFT; break;
            case UP_RIGHT: direction = EnemyBallDirection.DOWN_RIGHT; break;
            case DOWN_LEFT: direction = EnemyBallDirection.UP_LEFT; break;
            case DOWN_RIGHT: direction = EnemyBallDirection.UP_RIGHT; break;
        }
    }

    public void setPosition(int x, int y){
        position.x = x;
        position.y = y;
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, position.x, position.y);
    }

    public void update(){
        if(direction == EnemyBallDirection.UP_RIGHT){
            if(position.x >= 935 || position.y >= 645)
                return;
            position.x += speed;
            position.y += speed;
        }
        else if(direction == EnemyBallDirection.UP_LEFT){
            if(position.x <= 10 || position.y >= 645)
                return;
            position.x -= speed;
            position.y += speed;
        }
        else if(direction == EnemyBallDirection.DOWN_RIGHT){
            if(position.x >= 935 || position.y >= 645)
                return;
            position.x += speed;
            position.y -= speed;
        }
        else if(direction == EnemyBallDirection.DOWN_LEFT){
            if(position.x <= 10 || position.y <= 10)
                return;
            position.x -= speed;
            position.y -= speed;
        }
    }
}
