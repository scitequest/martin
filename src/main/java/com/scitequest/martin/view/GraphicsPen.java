package com.scitequest.martin.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;

public final class GraphicsPen implements Drawable {
    private final Graphics g;
    private final GuiDrawCanvas gCanvas;

    public GraphicsPen(Graphics g, GuiDrawCanvas gCanvas) {
        this.g = g;
        this.gCanvas = gCanvas;
    }

    @Override
    public void setColor(Color colour) {
        g.setColor(colour);
    }

    @Override
    public void drawPolygon(int[] polygonX, int[] polygonY) {
        for (int i = 0; i < polygonY.length; i++) {
            polygonX[i] = gCanvas.screenX(polygonX[i]);
            polygonY[i] = gCanvas.screenY(polygonY[i]);
        }
        // we could add an error message if xLength != yLenght

        g.drawPolygon(new Polygon(polygonX, polygonY, polygonY.length));
    }

    @Override
    public void drawString(String text, int x, int y) {
        // Graphics does not have a method like setJustification, so we have to
        // live with the kind of ugly implementation below.
        int fontSize = (int) Math.round(10 * gCanvas.getMagnification());
        g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

        x = gCanvas.screenX(x) - fontSize / 2;
        y = gCanvas.screenY(y) - fontSize / 2;

        g.drawString(text, x, y);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        x = gCanvas.screenX(x);
        y = gCanvas.screenY(y);

        width = (int) Math.round(width * gCanvas.getMagnification());
        height = (int) Math.round(height * gCanvas.getMagnification());

        g.drawOval(x, y, width, height);
    }

}
