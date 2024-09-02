package com.goriant.game.server.config.adapter;

import lombok.extern.slf4j.Slf4j;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@Slf4j
public final class MouseListenerAdapter {

    private MouseListenerAdapter() {
    }

    public static MouseListener adapter(MouseAction action) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.execute(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // fix sonar: no need implement for now
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // fix sonar: no need implement for now
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // fix sonar: no need implement for now
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // fix sonar: no need implement for now
            }
        };
    }
}
