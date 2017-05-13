package com.afrometal.radoslaw.breakout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by radoslaw on 13.05.17.
 */

public class MainActivity extends Activity {

    private Switch tiltSwitch;
    private SeekBar rowsBar;
    private SeekBar columnsBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView)findViewById(R.id.start_text)).setMovementMethod(new ScrollingMovementMethod());
        ((TextView)findViewById(R.id.controls_text)).setMovementMethod(new ScrollingMovementMethod());
        ((TextView)findViewById(R.id.bricks_text)).setMovementMethod(new ScrollingMovementMethod());

        tiltSwitch = (Switch) findViewById(R.id.tilt_switch);
        rowsBar = (SeekBar) findViewById(R.id.rows_picker);
        columnsBar = (SeekBar) findViewById(R.id.columns_picker);

        tiltSwitch.setChecked(true);
        rowsBar.setProgress(3);
        columnsBar.setProgress(6);
    }

    public void startGame(View view) {
        Intent intent = new Intent(MainActivity.this, BreakoutActivity.class);

        intent.putExtra("tiltSwitch", tiltSwitch.isChecked());
        intent.putExtra("rowsNumber", rowsBar.getProgress() + 2);
        intent.putExtra("columnsNumber", columnsBar.getProgress() + 3);

        MainActivity.this.startActivity(intent);
    }
}
