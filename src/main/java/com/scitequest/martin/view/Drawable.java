package com.scitequest.martin.view;

import java.awt.Color;

public interface Drawable {
    void setColor(Color colour);

    void drawPolygon(int[] polygonX, int[] polygonY);

    void drawString(String text, int x, int y);

    void drawOval(int x, int y, int width, int height);

}
