package com.goriant.game.server.handler;

import io.netty.channel.ChannelHandlerContext;

public interface Handler {
    void handle(ChannelHandlerContext ctx);
}
