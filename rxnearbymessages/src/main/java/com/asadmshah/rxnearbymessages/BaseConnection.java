package com.asadmshah.rxnearbymessages;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.asadmshah.rxnearbymessages.exceptions.GoogleApiClientFailedException;
import com.asadmshah.rxnearbymessages.exceptions.GoogleApiClientSuspendedException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.nearby.Nearby;

abstract class BaseConnection<T extends BaseConnection.Callbacks> {

    protected final GoogleApiClient connection;
    protected T listener;

    private final ConnectionCallbacks connectionCallbacks = new ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            if (listener != null) {
                BaseConnection.this.onConnected();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            if (listener != null) {
                listener.onError(new GoogleApiClientSuspendedException(i));
            }
        }
    };

    private final OnConnectionFailedListener onConnectionFailedListener = new OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            if (listener != null) {
                listener.onError(new GoogleApiClientFailedException(connectionResult));
            }
        }
    };

    BaseConnection(@NonNull Context context) {
        connection = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(Nearby.MESSAGES_API)
                .build();
    }

    public void connect() {
        if (!connection.isConnected()) {
            connection.connect();
        } else if (!connection.isConnecting()) {
            connection.connect();
        }
    }

    public void disconnect() {
        if (connection.isConnected()) {
            onDisconnect();
            connection.disconnect();
        } else if (connection.isConnecting()) {
            connection.disconnect();
        }
    }

    public void setListener(@Nullable T listener) {
        this.listener = listener;
    }

    abstract void onConnected();

    abstract void onDisconnect();

    interface Callbacks {
        void onError(Throwable error);
    }

}
