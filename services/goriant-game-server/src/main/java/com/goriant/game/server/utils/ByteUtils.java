package com.goriant.game.server.utils;

import com.google.protobuf.GeneratedMessageLite;
import com.goriant.game.models.PlayerActions;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public final class ByteUtils {
    private ByteUtils() {}

    public static BinaryWebSocketFrame protoMsgToBytes(GeneratedMessageLite<PlayerActions.PlayerMessage, PlayerActions.PlayerMessage.Builder> msg) {
        return new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg.toByteArray()));
    }
}
