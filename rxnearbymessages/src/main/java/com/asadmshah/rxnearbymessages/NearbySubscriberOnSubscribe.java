package com.asadmshah.rxnearbymessages;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.Strategy;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

class NearbySubscriberOnSubscribe implements Observable.OnSubscribe<NearbySubscriberEvent> {

    private final Context context;
    private final Strategy strategy;
    private final MessageFilter filter;

    NearbySubscriberOnSubscribe(@NonNull Context context, @NonNull Strategy strategy, @NonNull MessageFilter filter) {
        this.context = context;
        this.strategy = strategy;
        this.filter = filter;
    }

    @Override
    public void call(final Subscriber<? super NearbySubscriberEvent> subscriber) {
        final NearbySubscriber nearbySubscriber = new NearbySubscriber(context, strategy, filter);
        nearbySubscriber.setListener(new NearbySubscriber.Callbacks() {
            @Override
            public void onSubscribed() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new NearbySubscriberEvent(NearbySubscriberEvent.SUBSCRIBED, new Message(new byte[]{})));
                }
            }

            @Override
            public void onFound(Message message) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new NearbySubscriberEvent(NearbySubscriberEvent.FOUND, message));
                }
            }

            @Override
            public void onLost(Message message) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new NearbySubscriberEvent(NearbySubscriberEvent.LOST, message));
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
        nearbySubscriber.connect();

        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                nearbySubscriber.setListener(null);
                nearbySubscriber.disconnect();
            }
        }));
    }

}
