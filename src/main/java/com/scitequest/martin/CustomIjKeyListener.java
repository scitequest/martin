package com.scitequest.martin;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This class overrides the standard ImageJ KeyListener.
 * Except a few exceptions all key events are removed by this.
 */
public class CustomIjKeyListener implements KeyListener {

    private final KeyListener ijKeyListener;

    public CustomIjKeyListener(KeyListener ijKeyListener) {
        this.ijKeyListener = ijKeyListener;
    }

    /*
     * This removes all keyTyped events.
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /*
     * This removes all keyPressed events except the ones specified in the
     * if-statements below.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {
            // This sets e to only contain the up-key.
            e.setKeyCode(KeyEvent.VK_UP);
            ijKeyListener.keyPressed(e);
            return;
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            e.setKeyCode(KeyEvent.VK_DOWN);
            ijKeyListener.keyPressed(e);
            return;
        }
        if (keyCode == KeyEvent.VK_SPACE) {
            e.setKeyCode(KeyEvent.VK_SPACE);
            ijKeyListener.keyPressed(e);
            return;
        }
    }

    /*
     * This removes all keyReleased events.
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }
}