package com.afrometal.radoslaw.breakout;

import android.graphics.RectF;

/**
 * Created by radoslaw on 13.05.17.
 */

class Paddle extends RectF {
    public final static int PAUSE_MOVEMENT = 0;
    public final static int MOVE_LEFT = 1;
    public final static int MOVE_RIGHT = 2;
    public int color;
    private int screenWidth;
    private int screenHeight;
    private int state = PAUSE_MOVEMENT;
    private int move;

    Paddle(float paddleWidth, float paddleHeight, int screenWidth, int screenHeight, int color) {
        super(
                screenWidth / 2 - paddleWidth / 2,
                screenHeight - paddleHeight,
                screenWidth / 2 - paddleWidth / 2 + paddleWidth,
                screenHeight
        );

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.color = color;

        move = screenWidth/100;
    }

    void setState(int state) {
        this.state = state;
    }

    void update() {
        switch (state) {
            case MOVE_LEFT:
                if (left - move >= 0) {
                    left -= move;
                    right -= move;
                } else {
                    right -= left;
                    left = 0;
                }
                break;
            case MOVE_RIGHT:
                if (right + move <= screenWidth) {
                    right += move;
                    left += move;
                } else {
                    left += screenWidth - right;
                    right = screenWidth;
                }
                break;
            case PAUSE_MOVEMENT:
                return;
        }
    }
}
