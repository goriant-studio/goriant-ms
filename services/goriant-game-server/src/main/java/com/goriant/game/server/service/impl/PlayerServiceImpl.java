package com.goriant.game.server.service.impl;

import com.goriant.game.server.model.Player;
import com.goriant.game.server.service.PlayerService;

public class PlayerServiceImpl implements PlayerService {

    @Override
    public void handlePlayerDisconnect(Player player) {
        if (player.getChannel() != null) {
            player.getChannel().close();
            player.setChannel(null);
        }
    }
}
