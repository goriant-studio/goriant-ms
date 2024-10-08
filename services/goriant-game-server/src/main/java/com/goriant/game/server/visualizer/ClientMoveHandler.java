package com.goriant.game.server.visualizer;

import com.goriant.game.server.client.WebSocketClient;
import com.goriant.game.models.PlayerActions;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
class ClientMoveHandler implements Runnable {

    private final WebSocketClient client;
    private final Queue<PlayerActions.PlayerMessage> queue;
    private volatile boolean running = true;

    public ClientMoveHandler(WebSocketClient client, Queue<PlayerActions.PlayerMessage> queue) {
        this.client = client;
        this.queue = queue;
    }

    public void move(PlayerActions.PlayerMessage move) {
        this.queue.add(move);
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        while (running) {
            while (!queue.isEmpty() && client != null) {
                PlayerActions.PlayerMessage move = queue.poll();
                if (move != null)
                    client.send(move.toByteArray());
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(2));
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
        }
    }
}
