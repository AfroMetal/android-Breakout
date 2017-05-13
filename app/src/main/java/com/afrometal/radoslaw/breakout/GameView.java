package com.afrometal.radoslaw.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by radoslaw on 13.05.17.
 */

public class GameView extends View {

    protected final static float PADDLE_WIDTH = 0.2f;
    protected final static float PADDLE_HEIGHT = 0.03f;
    protected final static float BALL_WIDTH = 0.03f;
    protected final static float BALL_HEIGHT = 0.03f;
    protected final static float BALL_SPEED_X = 5f;
    protected final static float BALL_SPEED_Y = -10f;

    protected final static int STATE_PAUSED = 0;
    protected final static int STATE_PLAY = 1;

    private final static int NONE = -1;
    private final static int HIT = 0;
    private final static int SIDE_HIT = 1;

    private Random rand;

    public int state;
    public Paddle paddle;
    public Ball ball;
    public Brick[] bricks;
    private Paint paint;
    private Paint textPaint;
    public int bricksColumns;
    public int bricksRows;
    private int[] colors;

    public int score = 0;
    public int lives = 3;

    protected int screenWidth;
    protected int screenHeight;

    private BreakoutActivity activity;

    // constructor method
    public GameView(Context context, int rows, int columns) {
        super(context);

        activity = (BreakoutActivity) context;

        // Get a Display object to access screen details
        Display display = activity.getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        bricksRows = rows;
        bricksColumns = columns;

        screenWidth = size.x;
        screenHeight = size.y;

        paint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(activity.getColor(R.color.colorText));

        ball = new Ball(
                BALL_WIDTH * screenWidth,
                BALL_HEIGHT * screenWidth,
                BALL_SPEED_X,
                BALL_SPEED_Y,
                screenWidth,
                screenHeight,
                activity.getColor(R.color.colorBall)
        );
        Log.d("ball", ball.toString());

        paddle = new Paddle(
                PADDLE_WIDTH * (float)screenWidth,
                PADDLE_HEIGHT * (float)screenHeight,
                screenWidth,
                screenHeight,
                activity.getColor(R.color.colorPaddle)
        );
        Log.d("paddle", paddle.toString());

        colors = getResources().getIntArray(R.array.brickColors);
        state = STATE_PAUSED;

        prepareGame();

        Log.d("ball", ball.toString());
        Log.d("paddle", paddle.toString());
    }

    public void prepareGame() {
        state = STATE_PAUSED;

        int brickWidth = screenWidth / bricksColumns;
        int brickHeight = (screenHeight / 3) / bricksRows;

        // Build a wall of bricks
        bricks = new Brick[bricksColumns * bricksRows];

        int[] indexes = new int[bricksRows];
        for (int i=0; i<bricksRows; i++) {
            indexes[i] = i % colors.length;
        }

        for (int column = 0, i = 0; column < bricksColumns; column++) {
            for (int row = 0; row < bricksRows; row++) {
                bricks[i++] = new Brick(
                        column,
                        row,
                        brickWidth,
                        brickHeight,
                        colors[indexes[row]]
                );
            }
        }

        ball.reset(paddle.centerX());
    }

    public void update() {
        RectF preBall = new RectF(ball);
        boolean gameEnd = true;

        paddle.update();
        ball.update();

        int hit = NONE;

        // Check for ball colliding with a brick
        for (Brick brick : bricks) {
            if (brick.getVisibility()) {
                if (RectF.intersects(brick, ball)) {
                    if (preBall.right < brick.left || preBall.left > brick.right) {
                        // hit side
                        hit = SIDE_HIT;
                    } else {
                        hit = HIT;
                    }
                    if (brick.color == activity.getColor(R.color.brickGreen)) {
                        // green, slime, slow down
                        ball.velocityY *= 0.85;
                    } else if (brick.color == activity.getColor(R.color.brickBlue)) {
                        // blue, ice, next live
                        brick.color = activity.getColor(R.color.brickBlueHit);
                        gameEnd = false;
                        continue;
                    } else if (brick.color == activity.getColor(R.color.brickYellow)) {
                        // yellow, fire, speed up
                        ball.velocityY *= 1.2;
                    }
                    brick.setInvisible();
                    score = score + 1;
                } else {
                    gameEnd = false;
                }
            }
        }

        if (hit == SIDE_HIT) {
            ball.reverseXVelocity();
        } else if (hit == HIT) {
            ball.reverseYVelocity();
        }

        if (gameEnd) {
            if (paddle.width() > screenWidth * 0.14f) {
                paddle.left += screenWidth * 0.02f;
                paddle.right -= screenWidth * 0.02f;
            }

            ball.velocityY += 1f;

            Toast.makeText(activity, "Congratulations! Now won't be so easy", Toast.LENGTH_SHORT).show();

            prepareGame();
            return;
        }

        // Bounce the ball back when it hits the bottom of screen
        if (ball.bottom > screenHeight) {
            ball.reverseYVelocity();
            ball.bounce(Ball.BOTTOM_BOUNCE);

            lives--;

            if (lives < 0) {
                Toast.makeText(activity, "Game Over", Toast.LENGTH_SHORT).show();
                score = 0;
                lives = 3;
                prepareGame();
                return;
            } else {
                Toast.makeText(activity, "Ouch!", Toast.LENGTH_SHORT).show();
            }
        }

        // If the ball hits left wall bounce
        if (ball.left < 0) {
            ball.reverseXVelocity();
            ball.bounce(Ball.LEFT_BOUNCE);
        }
        // Bounce the ball back when it hits the top of screen
        if (ball.top < 0) {
            ball.reverseYVelocity();
            ball.bounce(Ball.TOP_BOUNCE);
        }

        // If the ball hits right wall bounce
        if (ball.right > screenWidth) {
            ball.reverseXVelocity();
            ball.bounce(Ball.RIGHT_BOUNCE);
        }

        // Check for ball colliding with paddle
        if (RectF.intersects(paddle, ball)) {
            float fraction = (ball.centerX()-paddle.centerX()) / (paddle.width()/2);
//            fraction = fraction<0 ? fraction-1f : fraction>0 ? fraction+1f : 1f;

            ball.reverseYVelocity();
            ball.scaleXVelocity(fraction);
            ball.bounce(Ball.PADDLE_BOUNCE);
        }
    }

    // Draw the newly updated scene
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // background color
        int backgroundColor = activity.getColor(R.color.colorBackground);
        canvas.drawRGB(Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));

        if (state == STATE_PLAY) {
            update();
        } else {
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(screenHeight / 8);
            canvas.drawText(
                    "PAUSED",
                    screenWidth / 2,
                    screenHeight / 2 + textPaint.getTextSize() / 2,
                    textPaint
            );
        }

        // Draw the paddle and ball
        paint.setColor(paddle.color);
        canvas.drawRect(paddle, paint);

        paint.setColor(ball.color);
        canvas.drawOval(ball, paint);

        // Draw the bricks if visible
        for (Brick brick : bricks) {
            if (brick.getVisibility()) {
                paint.setColor(brick.color);
                canvas.drawRect(brick, paint);
            }
        }

        // Draw the score and lives
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(screenHeight / 15);
        canvas.drawText(
                String.format("Score: %d\nLives: %d", score, lives),
                16,
                screenHeight / 3 + textPaint.getTextSize(),
                textPaint
        );
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        this.state = STATE_PAUSED;
        super.onRestoreInstanceState(state);
    }
}