package com.afrometal.radoslaw.breakout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Created by radoslaw on 13.05.17.
 */

public class BreakoutActivity extends Activity implements SensorEventListener {

    private GameView gameView;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean tilt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        tilt = intent.getBooleanExtra("tiltSwitch", true);
        int rows = intent.getIntExtra("rowsNumber", 5);
        int columns = intent.getIntExtra("columnsNumber", 8);

        gameView = new GameView(this, rows, columns);
        setContentView(gameView);

        if (tilt) {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Handler h = new Handler(Looper.getMainLooper());
                h.postDelayed(this, 15);
                h.post(new Runnable() {
                    public void run() {
                        gameView.invalidate();
                    }
                });
            }
        });

        thread.start();
    }

    @Override
    protected void onStop() {
        if (tilt) mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (tilt) mSensorManager.unregisterListener(this);

                switch (gameView.state) {
                    case GameView.STATE_PAUSED:
                        gameView.setState(GameView.STATE_PLAY);
                        break;
                    case GameView.STATE_PLAY:
                        if (motionEvent.getY() < gameView.screenHeight / 2) {
                            gameView.setState(GameView.STATE_PAUSED);
                        }
                        break;
                }

                if (motionEvent.getX() > gameView.screenWidth / 2) {
                    gameView.paddle.setState(Paddle.MOVE_RIGHT);
                } else {
                    gameView.paddle.setState(Paddle.MOVE_LEFT);
                }
                break;
            case MotionEvent.ACTION_UP:
                gameView.paddle.setState(Paddle.PAUSE_MOVEMENT);
                if (tilt) mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
                break;
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (tilt) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float y = event.values[1];

                if (y > 1.5) {
                    gameView.paddle.setState(Paddle.MOVE_RIGHT);
                } else if (y < -1.5) {
                    gameView.paddle.setState(Paddle.MOVE_LEFT);
                } else {
                    gameView.paddle.setState(Paddle.PAUSE_MOVEMENT);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putFloatArray(
                "paddleRect",
                new float[]{
                        gameView.paddle.left,
                        gameView.paddle.top,
                        gameView.paddle.right,
                        gameView.paddle.bottom
                });

        outState.putFloatArray(
                "ballRect",
                new float[]{
                        gameView.ball.left,
                        gameView.ball.top,
                        gameView.ball.right,
                        gameView.ball.bottom,
                        gameView.ball.velocityX,
                        gameView.ball.velocityY
                });

        boolean[] bricksVisibility = new boolean[gameView.bricks.length];
        for (int i = 0; i < gameView.bricks.length; i++) {
            bricksVisibility[i] = gameView.bricks[i].getVisibility();
        }
        outState.putBooleanArray("bricksVisibility", bricksVisibility);

        outState.putBoolean("tiltMode", tilt);

        outState.putInt("bricksRows", gameView.bricksRows);
        outState.putInt("bricksColumns", gameView.bricksColumns);

        outState.putInt("score", gameView.score);
        outState.putInt("lives", gameView.lives);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        float[] paddleRect = savedInstanceState.getFloatArray("paddleRect");
        if (paddleRect.length == 4) {
            gameView.paddle.set(paddleRect[0], paddleRect[1], paddleRect[2], paddleRect[3]);
        }

        float[] ballRect = savedInstanceState.getFloatArray("ballRect");
        if (ballRect.length == 6) {
            gameView.ball.set(ballRect[0], ballRect[1], ballRect[2], ballRect[3]);
            gameView.ball.velocityX = ballRect[4];
            gameView.ball.velocityY = ballRect[5];
        }

        boolean[] bricksVisibility = savedInstanceState.getBooleanArray("bricksVisibility");
        if (bricksVisibility.length == gameView.bricks.length) {
            for (int i = 0; i < gameView.bricks.length; i++) {
                if (!bricksVisibility[i]) {
                    gameView.bricks[i].setInvisible();
                }
            }
        }

        tilt = savedInstanceState.getBoolean("tiltMode", true);

        gameView.bricksRows = savedInstanceState.getInt("bricksRows", 5);
        gameView.bricksColumns = savedInstanceState.getInt("bricksColumns", 8);

        gameView.score = savedInstanceState.getInt("score", 0);
        gameView.lives = savedInstanceState.getInt("lives", 3);

        gameView.setState(GameView.STATE_PAUSED);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameView.setState(GameView.STATE_PAUSED);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        gameView.setState(GameView.STATE_PAUSED);
    }
}
