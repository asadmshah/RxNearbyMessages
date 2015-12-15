package com.asadmshah.rxnearbymessages.exceptions;

public class GoogleApiClientSuspendedException extends RuntimeException {

    private final int cause;

    public GoogleApiClientSuspendedException(int cause) {
        this.cause = cause;
    }

    public int getReason() {
        return cause;
    }

}