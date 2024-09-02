package com.goriant.game.server.handler;

import com.goriant.game.server.model.Player;
import com.goriant.game.models.PlayerActions;
import com.goriant.game.server.service.AOISystem;
import com.goriant.game.server.service.PlayerService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import static com.goriant.game.server.converter.GridHeightConverter.unityToAoiY;

@Slf4j
public class JoinHandler extends BaseHandler<PlayerActions.Join> implements Handler {

    private JoinHandler(AOISystem aoiSystem, PlayerActions.Join msg) {
        super(aoiSystem, msg);
    }

    public static JoinHandler from(AOISystem aoiSystem, PlayerActions.Join msg) {
        return new JoinHandler(aoiSystem, msg);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (msg instanceof PlayerActions.Join join) {
            Player player = PlayerService.nextPlayer();
            player.setId(join.getId());
            player.setName(join.getName());
            player.setPosition(Player.Position.from(join.getX(), join.getY()));
            player.setChannel(ctx.channel());
            if (join.getUnity()) {
                player.setUnity(Boolean.TRUE);
                player.setMain(Boolean.TRUE);
                player.setPositionY(unityToAoiY(join.getY()));
            }
            // temp condition for main player instead of bot
            if (join.getId() == 123456) // louis
                player.setMain(Boolean.TRUE);
            log.info("Player join server `{}`", player);
            aoiSystem.addPlayer(player);
            response(ctx);
        }
    }
}
