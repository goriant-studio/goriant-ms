package com.goriant.game.server.service;

import com.goriant.game.server.model.Player;
import com.goriant.game.server.service.aoi.GridCell;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AOISystem {

    List<Player> getPlayers();

    Map<Integer, Player> getPlayerMap();

    void addPlayer(Player player);

    Player getPlayerById(int playerId);

    void removePlayer(int id);

    Set<Player> getPlayersInAOI(Player player);

    Map<Integer, Map<Integer, GridCell>> getGrid();

    int getCellIndex(float v);

    int getGridSize();

    int getCellSize();

    void movePlayer(Player player);
}
