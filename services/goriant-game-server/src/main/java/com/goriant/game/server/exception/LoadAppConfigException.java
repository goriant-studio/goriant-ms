package com.goriant.game.server.exception;

public class LoadAppConfigException extends RuntimeException {

    public LoadAppConfigException(String msg, Exception e) {
        super(msg, e);
    }
}
