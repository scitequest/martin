package com.scitequest.martin;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import com.scitequest.martin.view.Controlable;

/**
 * This class overrides the standard ImageJ KeyListener.
 * Except a few exceptions all key events are removed by this.
 */
public class CustomIjKeyListener implements KeyListener {

    private final KeyListener ijKeyListener;
    private final Controlable control;

    /**
     * List of key codes that are not filtered and passed to ImageJ as is.
     */
    private static final List<Integer> validKeyCodes = List.of(
            KeyEvent.VK_UP, KeyEvent.VK_DOWN,
            KeyEvent.VK_EQUALS, KeyEvent.VK_MINUS,
            KeyEvent.VK_SPACE);

    /**
     * List of key characters that are not filtered and passed to ImageJ as is.
     */
    private static final List<Character> validKeyChars = List.of('-', '+', '=');

    public CustomIjKeyListener(KeyListener ijKeyListener, Controlable control) {
        this.ijKeyListener = ijKeyListener;
        this.control = control;
    }

    /**
     * Checks a event if it is accepted and should be passed to the ImageJ key
     * listener for further processing. This is used to filter only for key events
     * that we want to let ImageJ process in order to prevent the user from
     * executing native ImageJ macros.
     *
     * @param e the key event to check
     * @return true if the key event should be passed to the ImageJ listener
     */
    private static boolean isAcceptedEvent(KeyEvent e) {
        return validKeyCodes.contains(e.getKeyCode())
                || validKeyChars.contains(e.getKeyChar());
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
            e.consume();
            return;
        }
        // Autofit
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
            control.measureFieldFit();
            e.consume();
            return;
        }
        // Filter
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
            control.toggleFilter();
            e.consume();
            return;
        }
        // Measure
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_M) {
            control.measure();
            e.consume();
            return;
        }
        // Standard ImageJ KeyEvents
        if (isAcceptedEvent(e)) {
            ijKeyListener.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (isAcceptedEvent(e)) {
            ijKeyListener.keyReleased(e);
        }
    }
}
