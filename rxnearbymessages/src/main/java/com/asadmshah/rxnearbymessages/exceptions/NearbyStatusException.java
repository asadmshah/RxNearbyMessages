package com.asadmshah.rxnearbymessages.exceptions;

import com.google.android.gms.common.api.Status;

public class NearbyStatusException extends RuntimeException {

    private final Status status;

    public NearbyStatusException(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
