package com.goriant.game.server.handler;

import com.goriant.game.server.service.AOISystem;
import com.goriant.game.server.service.PlayerService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private final AOISystem aoiSystem;
    private final PlayerService playerService;

    public static HttpServerHandler from(AOISystem aoiSystem, PlayerService playerService) {
        return new HttpServerHandler(aoiSystem, playerService);
    }

    private HttpServerHandler(AOISystem aoiSystem, PlayerService playerService) {
        this.aoiSystem = aoiSystem;
        this.playerService = playerService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof HttpRequest httpRequest) {

                HttpHeaders headers = httpRequest.headers();
                if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION)) &&
                        "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {

                    //Adding new handler to the existing pipeline to handle WebSocket Messages
                    ctx.pipeline().replace(this, "websocketHandler", PlayerHandler.from(aoiSystem, playerService));

                    log.info("Handshaking....");
                    //Do the Handshake to upgrade connection from HTTP to WebSocket protocol
                    handleHandshake(ctx, httpRequest);
                    log.info("Handshake is done");
                }
            } else {
                log.warn("Incoming request is unknown");
            }
        } finally {
            ReferenceCountUtil.release(msg); // Ensure that the buffer is released
        }
    }

    /* Do the handshaking for WebSocket request */
    protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketURL(req), null, true);
        WebSocketServerHandshaker handshake = wsFactory.newHandshaker(req);
        if (handshake == null)
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        else
            handshake.handshake(ctx.channel(), req);
    }

    protected String getWebSocketURL(HttpRequest req) {
        return "ws://" + req.headers().get("Host") + req.uri();
    }
}
