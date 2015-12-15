package com.asadmshah.rxnearbymessages;

import android.content.Context;
import android.support.annotation.NonNull;

import com.asadmshah.rxnearbymessages.exceptions.NearbyStatusException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

class NearbySubscriber extends BaseConnection<NearbySubscriber.Callbacks> {

    private final SubscribeOptions subscribeOptions;

    private final MessageListener messageListener = new MessageListener() {
        @Override
        public void onFound(Message message) {
            if (listener != null) {
                listener.onFound(message);
            }
        }

        @Override
        public void onLost(Message message) {
            if (listener != null) {
                listener.onLost(message);
            }
        }
    };

    private final SubscribeCallback subscribeCallback = new SubscribeCallback() {
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
                    listener.onSubscribed();
                } else {
                    listener.onError(new NearbyStatusException(status));
                }
            }
        }
    };

    NearbySubscriber(@NonNull Context context, @NonNull Strategy strategy, @NonNull MessageFilter filter) {
        super(context);
        this.subscribeOptions = new SubscribeOptions.Builder()
                .setStrategy(strategy)
                .setFilter(filter)
                .setCallback(subscribeCallback)
                .build();
    }

    @Override
    void onConnected() {
        Nearby.Messages.subscribe(connection, messageListener, subscribeOptions)
                .setResultCallback(resultCallback);
    }

    @Override
    void onDisconnect() {
        Nearby.Messages.unsubscribe(connection, messageListener);
    }

    public interface Callbacks extends BaseConnection.Callbacks {
        void onSubscribed();
        void onFound(Message message);
        void onLost(Message message);
        void onExpired();
    }

}
