package com.afrometal.radoslaw.breakout;

import android.graphics.RectF;

/**
 * Created by radoslaw on 13.05.17.
 */

class Ball extends RectF {
    public final static int LEFT_BOUNCE = 0;
    public final static int RIGHT_BOUNCE = 1;
    public final static int TOP_BOUNCE = 2;
    public final static int BOTTOM_BOUNCE = 3;
    public final static int PADDLE_BOUNCE = 4;

    public float velocityX;
    public float velocityY;

    public int color;

    private int screenWidth;
    private int screenHeight;

    public Ball(float ballWidth, float ballHeight, float velocityX, float velocityY, int screenWidth, int screenHeight, int color) {
        super(
                screenWidth / 2 - ballWidth / 2,
                screenHeight - (GameView.PADDLE_HEIGHT * screenHeight) - ballHeight,
                screenWidth / 2 + ballWidth / 2,
                screenHeight - (GameView.PADDLE_HEIGHT * screenHeight)
        );

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.velocityX = velocityX;
        this.velocityY = velocityY;

        this.color = color;
    }

    public void update() {
        offset(velocityX, velocityY);
    }

    public void reset(float centerX) {
        float ballWidth = width();
        float ballHeight = height();

        left = centerX - ballWidth / 2;
        top = screenHeight - (GameView.PADDLE_HEIGHT * screenHeight) - ballHeight;
        right = centerX + ballWidth / 2;
        bottom = screenHeight - (GameView.PADDLE_HEIGHT * screenHeight);

        velocityY = GameView.BALL_SPEED_Y;
    }

    public void reverseYVelocity() {
        velocityY *= -1f;
    }

    public void reverseXVelocity() {
        velocityX *= -1f;
    }

    public void scaleXVelocity(float fraction) {
        velocityX = GameView.BALL_SPEED_X * fraction;
    }

    public void bounce(int side) {
        float height = height();
        float width = width();

        switch (side) {
            case LEFT_BOUNCE:
                left = 0;
                right = left + width;
                break;
            case TOP_BOUNCE:
                top = 0;
                bottom = top + height;
                break;
            case RIGHT_BOUNCE:
                right = screenWidth;
                left = right - width;
                break;
            case BOTTOM_BOUNCE:
                bottom = screenHeight;
                top = bottom - height;
                break;
            case PADDLE_BOUNCE:
                bottom = screenHeight - (GameView.PADDLE_HEIGHT * screenHeight);
                top = bottom - height;
                break;
        }
    }
}
