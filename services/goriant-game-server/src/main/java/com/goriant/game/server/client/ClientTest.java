package com.goriant.game.server.client;

import com.goriant.game.models.PlayerActions;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.logging.Logger;

@Slf4j
public class ClientTest {

    private static final Logger logger = Logger.getLogger(ClientTest.class.getName());

    public static void main(String[] args) {

        PlayerActions.Join join = PlayerActions.Join.newBuilder()
                .setId(1)
                .build();
        PlayerActions.PlayerMessage message = PlayerActions.PlayerMessage.newBuilder()
                .setJoin(join)
                .build();

        sendMsg(message);
    }

    static void sendMsg(PlayerActions.PlayerMessage message) {
        try (EventLoopGroup group = new NioEventLoopGroup()) {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new ProtobufEncoder());
                        }
                    });

            ChannelFuture f = b.connect("ws.memo.bond", 443).sync();
            Channel channel = f.channel();

            channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info("Message sent successfully");
                } else {
                    logger.warning("Failed to send message: " + future.cause().getMessage());
                }
                // Close the channel and event loop group after sending the message
                channel.close().addListener(ChannelFutureListener.CLOSE);
                group.shutdownGracefully();
            });

            // Wait until the connection is closed
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to send message", e);
        }
    }

}
