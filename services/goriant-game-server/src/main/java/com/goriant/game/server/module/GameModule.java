package com.goriant.game.server.module;

import com.goriant.game.server.GameServer;
import com.goriant.game.server.config.AppConfig;
import com.goriant.game.server.exception.LoadAppConfigException;
import com.goriant.game.server.model.Player;
import com.goriant.game.server.service.AOISystem;
import com.goriant.game.server.service.PlayerService;
import com.goriant.game.server.service.aoi.AOISystemImpl;
import com.goriant.game.server.service.impl.PlayerServiceImpl;
import com.goriant.game.server.visualizer.AOIVisualizer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.awt.Color;
import java.io.InputStream;

import static com.goriant.game.server.constants.Constants.CELL_SIZE;
import static com.goriant.game.server.constants.Constants.GRID_SIZE;
import static com.goriant.game.server.constants.Constants.RADIUS;

@Slf4j
public class GameModule extends AbstractModule {

    public static final String APP_CONFIG_PATH = "application_%s.yml";

    private String profile;

    public GameModule(String profile) {
        this.profile = profile;
    }

    public static GameModule from(String profile) {
        return new GameModule(profile);
    }

    @Provides
    private AppConfig provideAppConfig() {
        Yaml yaml = new Yaml();
        log.info("Yaml loader is loading application config from `{}` profile to AppConfig", profile);
        String path = String.format(APP_CONFIG_PATH, profile);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            return yaml.loadAs(inputStream, AppConfig.class);
        } catch (Exception e) {
            throw new LoadAppConfigException("Failed to load configuration", e);
        }
    }

    @Provides
    @Singleton
    private GameServer provideGameServer(AOISystem aoiSystem, AppConfig config, PlayerService playerService) {
        return new GameServer(config.getServer().getPort(), aoiSystem, playerService);
    }

    @Provides
    @Singleton
    private AOISystem provideAOISystem() {
        return new AOISystemImpl(GRID_SIZE, CELL_SIZE);
    }

    @Provides
    @Singleton
    private Player provideMainPlayer() {
        return Player.builder()
                .id(123456).name("Louis").position(Player.Position.from(430, 625))
                .speed(200).radius(RADIUS).direction(200)
                .color(Color.RED)
                .build();
    }

    @Provides
    @Singleton
    private AOIVisualizer provideAOIVisualizer(AOISystem aoiSystem, Player mainPlayer, AppConfig config) {
        aoiSystem.addPlayer(mainPlayer);
        return new AOIVisualizer(aoiSystem, mainPlayer, config.serverHost());
    }

    @Provides
    @Singleton
    private PlayerService providePlayerService() {
        return new PlayerServiceImpl();
    }
}

