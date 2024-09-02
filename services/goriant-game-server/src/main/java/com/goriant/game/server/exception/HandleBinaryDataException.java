package com.goriant.game.server.exception;

import com.google.protobuf.InvalidProtocolBufferException;

public class HandleBinaryDataException extends RuntimeException {

    public HandleBinaryDataException(InvalidProtocolBufferException e) {

    }
}
