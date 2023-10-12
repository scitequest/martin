package com.scitequest.martin;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import com.scitequest.martin.view.Controlable;

/**
 * This class overrides the standard ImageJ KeyListener.
 * Except a few exceptions all key events are removed by this.
 */
public class CustomIjKeyListener implements KeyListener {

    private final KeyListener ijKeyListener;
    private final Controlable control;

    /*
     * Array of allowed keycodes
     */
    private final int[] validKeys = new int[] { KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_SPACE, KeyEvent.VK_PLUS,
            KeyEvent.VK_MINUS, KeyEvent.VK_ADD, KeyEvent.VK_SUBTRACT };
    /*
     * List of chars that represent the keycodes of validKeys.
     * This mainly exists to catch to allow the use of the plus sign without any
     * annoyances. Without this the key char of plus changes depending on the key
     * modifier held down at the same time.
     */
    private final char[] keyChars = new char[] { KeyEvent.CHAR_UNDEFINED, KeyEvent.CHAR_UNDEFINED, ' ', '+',
            '-', '+', '-' };

    public CustomIjKeyListener(KeyListener ijKeyListener, Controlable control) {
        this.ijKeyListener = ijKeyListener;
        this.control = control;
    }

    /*
     * This method checks if the KeyEvent uses an accepted key.
     * If valid a Key event without any modifiers is returned.
     * If the user typed = a key event for + is returned, this is a special case to
     * adress ANSI-Keyboards.
     */
    private KeyEvent getValidatedKey(KeyEvent e) {
        for (int i = 0; i < validKeys.length; i++) {
            if (validKeys[i] == e.getKeyCode()) {
                return new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), 0, validKeys[i],
                        keyChars[i], e.getKeyLocation());
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
            return new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), 0, KeyEvent.VK_PLUS,
                    '+', e.getKeyLocation());
        }
        return null;
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
        // Measure
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_M) {
            control.measure();
            return;
        }
        // Standard ImageJ KeyEvents
        e = getValidatedKey(e);
        if (e != null) {
            ijKeyListener.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        e = getValidatedKey(e);
        if (e != null) {
            ijKeyListener.keyReleased(e);
        }
    }
}