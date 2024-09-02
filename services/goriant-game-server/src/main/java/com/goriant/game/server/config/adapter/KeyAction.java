package com.goriant.game.server.config.adapter;

import java.awt.event.KeyEvent;

@FunctionalInterface
public interface KeyAction {
    void execute(KeyEvent e);
}
