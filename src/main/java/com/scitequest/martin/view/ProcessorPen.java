package com.scitequest.martin.view;

import java.awt.Color;
import java.awt.Polygon;

import ij.process.ImageProcessor;
import net.imagej.patcher.LegacyInjector;

public final class ProcessorPen implements Drawable {

    static {
        LegacyInjector.preinit();
    }

    private final ImageProcessor iProc;

    public ProcessorPen(ImageProcessor iProc) {
        this.iProc = iProc;
    }

    @Override
    public void setColor(Color colour) {
        iProc.setColor(colour);
    }

    @Override
    public void drawPolygon(int[] polygonX, int[] polygonY) {
        // we could add an error message if xLength != yLenght
        iProc.drawPolygon(new Polygon(polygonX, polygonY, polygonY.length));
    }

    @Override
    public void drawString(String text, int x, int y) {
        iProc.setJustification(ImageProcessor.CENTER_JUSTIFY);
        iProc.drawString(text, x, y);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        iProc.drawOval(x, y, width, height);
    }
}
