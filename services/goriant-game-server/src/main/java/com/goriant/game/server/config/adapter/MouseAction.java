package com.goriant.game.server.config.adapter;

import java.awt.event.MouseEvent;

@FunctionalInterface
public interface MouseAction {
    void execute(MouseEvent e);
}
