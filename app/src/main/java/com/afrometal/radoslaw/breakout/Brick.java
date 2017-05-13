package com.afrometal.radoslaw.breakout;

import android.graphics.RectF;

/**
 * Created by radoslaw on 13.05.17.
 */

class Brick extends RectF {
    public int color;
    public int column;
    public int row;
    private boolean isVisible;

    Brick(int column, int row, float width, float height, int color) {
        super(
                column * width,
                row * height,
                column * width + width,
                row * height + height
        );

        left += 2;
        top += 2;
        right -= 2;
        bottom -= 2;

        this.column = column;
        this.row = row;
        this.color = color;

        isVisible = true;
    }

    void setInvisible() {
        isVisible = false;
    }

    boolean getVisibility() {
        return isVisible;
    }
}
