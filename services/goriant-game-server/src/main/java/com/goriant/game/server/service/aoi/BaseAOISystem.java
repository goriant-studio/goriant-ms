package com.goriant.game.server.service.aoi;

import com.goriant.game.server.service.AOISystem;

public abstract class BaseAOISystem implements AOISystem {

    public int getCellIndex(float coordinate) {
        return (int) Math.floor(coordinate / getCellSize());
    }
}
