package com.scitequest.martin;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This class overrides the standard ImageJ KeyListener.
 * Except a few exceptions all key events are removed by this.
 */
public class CustomIjKeyListener implements KeyListener {

    private final KeyListener ijKeyListener;
    private final int[] validKeys = new int[] { KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_SPACE, KeyEvent.VK_PLUS,
            KeyEvent.VK_MINUS, KeyEvent.VK_ADD, KeyEvent.VK_SUBTRACT, KeyEvent.VK_EQUALS };

    public CustomIjKeyListener(KeyListener ijKeyListener) {
        this.ijKeyListener = ijKeyListener;
    }

    /*
     * This method checks if the KeyEvent uses an accepted key.
     * If valid the key code is returned if invalid an empty OptionalInt is
     * returned.
     */
    private boolean validateKey(KeyEvent e) {
        for (int i : validKeys) {
            if (i == e.getKeyCode()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (validateKey(e)) {
            ijKeyListener.keyTyped(new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), 0, e.getKeyCode(),
                    e.getKeyChar(), e.getKeyLocation()));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (validateKey(e)) {
            ijKeyListener.keyPressed(new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), 0, e.getKeyCode(),
                    e.getKeyChar(), e.getKeyLocation()));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (validateKey(e)) {
            ijKeyListener.keyReleased(new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), 0, e.getKeyCode(),
                    e.getKeyChar(), e.getKeyLocation()));
        }
    }
}