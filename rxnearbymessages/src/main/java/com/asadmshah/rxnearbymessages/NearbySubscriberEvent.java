package com.asadmshah.rxnearbymessages;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.google.android.gms.nearby.messages.Message;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Event used in conjunction with {@link NearbySubscriberOnSubscribe}
 */
public final class NearbySubscriberEvent {

    @IntDef({SUBSCRIBED, LOST, FOUND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SubscriberEvent {}
    public static final int SUBSCRIBED = 1;
    public static final int LOST = 2;
    public static final int FOUND = 3;

    @SubscriberEvent public final int kind;
    public final Message message;

    public NearbySubscriberEvent(@SubscriberEvent int kind, @NonNull Message message) {
        this.kind = kind;
        this.message = message;
    }
}
