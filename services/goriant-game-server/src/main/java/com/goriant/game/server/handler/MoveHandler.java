package com.goriant.game.server.handler;

import com.goriant.game.server.model.Player;
import com.goriant.game.models.PlayerActions;
import com.goriant.game.server.service.AOISystem;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

import static com.goriant.game.server.converter.GridHeightConverter.aoiToUnityY;
import static com.goriant.game.server.converter.GridHeightConverter.unityToAoiY;
import static com.goriant.game.server.utils.ByteUtils.protoMsgToBytes;

@Slf4j
public class MoveHandler extends BaseHandler<PlayerActions.Move> implements Handler {

    private MoveHandler(AOISystem aoiSystem, PlayerActions.Move msg) {
        super(aoiSystem, msg);
    }

    public static MoveHandler from(AOISystem aoiSystem, PlayerActions.Move move) {
        return new MoveHandler(aoiSystem, move);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (msg instanceof PlayerActions.Move move) {
            Player player = aoiSystem.getPlayerById(move.getId());
            if (player == null) {
                log.warn("This player `{}` is not join & initial properly - so it is null", move.getId());
                return;
            }
            player.move(move.getX(),
                    player.isUnity() ? unityToAoiY(move.getY()) : move.getY(),
                    move.getDirection().ordinal());
            aoiSystem.movePlayer(player);
            broadcastMove(player);
            response(ctx);
        }
    }

    private void broadcastMove(Player player) {
        Set<Player> otherPlayers = aoiSystem.getPlayersInAOI(player);
        if (otherPlayers.size() == 1) return;

        for (Player otherPlayer : otherPlayers) {
            if (otherPlayer.getId() == player.getId()) continue; // exclude current player
            Objects.requireNonNull(otherPlayer.getChannel(), "Other player Id `" + otherPlayer.getId() + "` channel should not be null");
            try {
                BinaryWebSocketFrame data = protoMsgToBytes(player.moveMsg(
                        otherPlayer.isUnity()
                                ? aoiToUnityY(player.getPosition().getY())
                                : player.getPosition().getY()
                ));
                otherPlayer.getChannel().writeAndFlush(data)
                        .addListener((ChannelFutureListener) future -> {
                            if (!future.isSuccess()) {
                                log.error("Failed to send move message to player ID {}, {}", otherPlayer.getId(), future.cause().getStackTrace());
                            }
                        });
            } catch (Exception e) {
                log.error("broadcast move failed", e);
            }

        }
    }
}
