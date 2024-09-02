package com.goriant.game.server.handler;

import com.goriant.game.models.PlayerActions;
import com.goriant.game.server.service.AOISystem;
import com.google.protobuf.GeneratedMessageLite;
import io.netty.channel.ChannelHandlerContext;

import static com.goriant.game.server.utils.ByteUtils.protoMsgToBytes;

public abstract class BaseHandler <T extends GeneratedMessageLite> {

    protected T msg;
    protected final AOISystem aoiSystem;

    protected BaseHandler(AOISystem aoiSystem, T msg) {
        this.aoiSystem = aoiSystem;
        this.msg = msg;
    }

    protected void response(ChannelHandlerContext ctx) {
        PlayerActions.PlayerMessage responseMsg = PlayerActions.PlayerMessage.newBuilder()
                .setResponse(PlayerActions.Response.newBuilder().setSuccess(Boolean.TRUE).build())
                .build();
        ctx.writeAndFlush(protoMsgToBytes(responseMsg));
    }
}
