package com.scitequest.martin.view;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.scitequest.martin.CustomIjKeyListener;
import com.scitequest.martin.DrawOptions;
import com.scitequest.martin.settings.Settings;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import net.imagej.patcher.LegacyInjector;

/**
 * This class allows to fit a separately calculated slide mask over an image.
 * Both movement and rotation are possible by mouse drag. Zooming in and out is
 * also an option.
 *
 * This also uses double buffering to prevent flickering during drawing.
 */
public final class GuiDrawCanvas extends ImageCanvas implements KeyListener {

    static {
        LegacyInjector.preinit();
    }

    private static final long serialVersionUID = 1L;

    /** X coordinate when the mouse was pressed. */
    private int lastX = 0;
    /** Y coordinate when the mouse was pressed. */
    private int lastY = 0;

    private final Settings settings;
    private final Controlable control;
    private final GraphicsConfiguration config;

    private static boolean canvasActive;

    /**
     * Constructor of this class.
     *
     * @param control  bidirectional association with control
     * @param iPlus    image this canvas is based upon
     * @param settings settings including relevant slide parameters
     */
    public GuiDrawCanvas(Controlable control, ImagePlus iPlus, Settings settings) {
        super(iPlus);
        CustomIjKeyListener modifiedIjKeyListener = new CustomIjKeyListener(ij.getKeyListeners()[0]);
        removeKeyListener(ij);
        addKeyListener(modifiedIjKeyListener);
        addKeyListener(this);
        this.control = control;
        this.settings = settings;
        this.config = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        // Disables the right click menu of ImageJ
        this.disablePopupMenu(true);
    }

    public static void toggleCanvas(boolean toggle) {
        canvasActive = toggle;
    }

    /**
     * Draws the separately calculated elements of a slide mask.
     *
     * @param g instance of Graphics
     */
    public void drawElements(Graphics g) {
        DrawOptions drawOptions = new DrawOptions(true,
                settings.getDisplaySettings().isShowSpotfieldGrids(),
                false, true,
                settings.getMeasurementSettings().isSubtractBackground(),
                settings.getDisplaySettings().isShowMeasureCircles());
        control.drawElements(new GraphicsPen(g, this), drawOptions);
    }

    @Override
    public void paint(Graphics g) {
        int height = this.getImage().getHeight();
        int width = this.getImage().getWidth();
        BufferedImage buffy = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        Graphics2D fastGraphics = (Graphics2D) buffy.getGraphics();
        // Improve rendering with for example antialiasing
        fastGraphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        fastGraphics.setRenderingHint(
                RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        fastGraphics.setRenderingHint(
                RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        fastGraphics.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        fastGraphics.setRenderingHint(
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw the base image on the buffer
        super.paint(fastGraphics);
        // Draw our slide elements
        drawElements(fastGraphics);
        fastGraphics.dispose();

        // Draw the final buffer into the actual graphics
        g.drawImage(buffy, 0, 0, null);
        g.dispose();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!canvasActive) {
            return;
        }

        int offscreenX = offScreenX(e.getX());
        int offscreenY = offScreenY(e.getY());
        lastX = offscreenX;
        lastY = offscreenY;

        // Hack to only implement the panning and not call the super implementation
        if (IJ.spaceBarDown()) {
            // temporarily switch to "hand" tool of space bar down
            setupScroll(offscreenX, offscreenY);
            return;
        }

        control.updateClickedMeasureCircles(offscreenX, offscreenY);
        control.updateClickedRectPolygons(offscreenX, offscreenY);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!canvasActive) {
            return;
        }

        int offscreenX = offScreenX(e.getX());
        int offscreenY = offScreenY(e.getY());
        int prevX = lastX;
        int prevY = lastY;
        lastX = offscreenX;
        lastY = offscreenY;

        super.mouseDragged(e);
        // If we scrolled, don't move elements
        if (IJ.spaceBarDown()) {
            return;
        }

        if (!control.moveGrabbedElement(offscreenX, offscreenY, prevX, prevY, 1, 1, false)) {
            control.rotateSlideViaMouse(offscreenX, offscreenY, prevX, prevY);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        control.releaseMouseGrip();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Reset
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R) {
            control.repositionSlide();
            control.update();
            return;
        }
        // Autofit
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
            control.measureFieldFit();
            return;
        }
        // Filter
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
            control.toggleFilter();
            return;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
