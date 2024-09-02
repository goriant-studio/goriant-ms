package com.goriant.game.server.service;

import com.goriant.game.server.model.Player;
import com.goriant.game.server.random.MyRandomizer;
import com.goriant.game.server.utils.ColorUtil;

public interface PlayerService extends Service {

    static Player nextPlayer() {
        Player player = MyRandomizer.nextObject(Player.class);
        player.setName(MyRandomizer.fullName());
        player.setColor(ColorUtil.getRandomColor());
        return player;
    }

    void handlePlayerDisconnect(Player player);
}
