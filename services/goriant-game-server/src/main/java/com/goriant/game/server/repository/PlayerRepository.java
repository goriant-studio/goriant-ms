package com.goriant.game.server.repository;

import com.goriant.game.server.model.Player;

public interface PlayerRepository extends Repository {

    void savePlayer(Player player);

    Player getPlayer(int id);
}
