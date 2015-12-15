package com.asadmshah.rxnearbymessages.exceptions;

import com.google.android.gms.common.ConnectionResult;

public class GoogleApiClientFailedException extends RuntimeException {

    private final ConnectionResult connectionResult;

    public GoogleApiClientFailedException(ConnectionResult connectionResult) {
        this.connectionResult = connectionResult;
    }

    public ConnectionResult getConnectionResult() {
        return connectionResult;
    }
}
