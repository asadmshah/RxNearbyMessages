package com.asadmshah.rxnearbymessages;

import android.content.Context;
import android.support.annotation.NonNull;

import com.asadmshah.rxnearbymessages.exceptions.NearbyStatusException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;

class NearbyPublisher extends BaseConnection<NearbyPublisher.Callbacks> {

    private final Message message;
    private final PublishOptions publishOptions;

    private final PublishCallback publishCallback = new PublishCallback() {
        @Override
        public void onExpired() {
            if (listener != null) {
                listener.onExpired();
            }
        }
    };

    private final ResultCallback<Status> resultCallback = new ResultCallback<Status>() {
        @Override
        public void onResult(Status status) {
            if (listener != null) {
                if (status.isSuccess()) {
                    listener.onPublishing(message);
                } else {
                    listener.onError(new NearbyStatusException(status));
                }
            }
        }
    };

    NearbyPublisher(@NonNull Context context, @NonNull Message message, @NonNull Strategy strategy) {
        super(context);
        this.message = message;
        this.publishOptions = new PublishOptions.Builder()
                .setStrategy(strategy)
                .setCallback(publishCallback)
                .build();
    }

    @Override
    void onConnected() {
        Nearby.Messages.publish(connection, message, publishOptions)
                .setResultCallback(resultCallback);
    }

    @Override
    void onDisconnect() {
        Nearby.Messages.unpublish(connection, message);
    }

    interface Callbacks extends BaseConnection.Callbacks {
        void onPublishing(Message message);
        void onExpired();
    }

}
