package com.asadmshah.rxnearbymessages;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.Strategy;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

class NearbyPublisherOnSubscribe implements Observable.OnSubscribe<Message> {

    private final Context context;
    private final Message message;
    private final Strategy strategy;

    NearbyPublisherOnSubscribe(@NonNull Context context, @NonNull Message message, @NonNull Strategy strategy) {
        this.context = context;
        this.message = message;
        this.strategy = strategy;
    }

    @Override
    public void call(final Subscriber<? super Message> subscriber) {
        final NearbyPublisher nearbyPublisher = new NearbyPublisher(context, message, strategy);
        nearbyPublisher.setListener(new NearbyPublisher.Callbacks() {
            @Override
            public void onPublishing(Message message) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(message);
                }
            }

            @Override
            public void onExpired() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable error) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(error);
                }
            }
        });
        nearbyPublisher.connect();

        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                nearbyPublisher.setListener(null);
                nearbyPublisher.disconnect();
            }
        }));
    }

}
