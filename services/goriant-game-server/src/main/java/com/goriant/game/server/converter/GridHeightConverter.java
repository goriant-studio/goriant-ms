package com.goriant.game.server.converter;

public final class GridHeightConverter {
    private static final int HEIGHT = 1000;

    private GridHeightConverter() {}

    public static int unityToAoiY(float unityY) {
        return HEIGHT - Math.round(unityY);
    }

    public static float aoiToUnityY(float aoiY) {
        return HEIGHT - aoiY;
    }
}
