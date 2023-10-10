package com.scitequest.martin;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CustomIjKeyListener implements KeyListener {

    private KeyListener ijKeyListener;

    public CustomIjKeyListener(KeyListener ijKeyListener) {
        this.ijKeyListener = ijKeyListener;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {
            ijKeyListener.keyPressed(e);
            return;
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            ijKeyListener.keyPressed(e);
            return;
        }
        if (keyCode == KeyEvent.VK_SPACE) {
            ijKeyListener.keyPressed(e);
            return;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}