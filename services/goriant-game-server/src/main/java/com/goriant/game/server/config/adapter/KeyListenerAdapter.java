package com.goriant.game.server.config.adapter;

import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Slf4j
public final class KeyListenerAdapter {

    private KeyListenerAdapter() {
    }

    public static KeyListener adapter(KeyAction action) {
        return new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                action.execute(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // no need implement for now
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // no need implement for now
            }
        };
    }
}
