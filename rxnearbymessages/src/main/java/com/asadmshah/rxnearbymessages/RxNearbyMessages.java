package com.asadmshah.rxnearbymessages;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.Strategy;

import rx.Observable;
import rx.functions.Func0;

/**
 * Factory of observables that lets you publish and subscribe to the NearbyMessages API.
 */
public final class RxNearbyMessages {

    private RxNearbyMessages() {}

    /**
     * Creates an observable that emits any discovered nearby messages using {@link Strategy#DEFAULT}
     * and {@link MessageFilter#INCLUDE_ALL_MY_TYPES}.
     *
     * @see #getSubscriberObservable(Context, Strategy, MessageFilter)
     *
     * @param context to be used in the {@link com.google.android.gms.common.api.GoogleApiClient}
     * @return an observable that emits any discovered messages
     */
    public static Observable<NearbySubscriberEvent> getSubscriberObservable(@NonNull Context context) {
        return getSubscriberObservable(context, Strategy.DEFAULT, MessageFilter.INCLUDE_ALL_MY_TYPES);
    }

    /**
     * Creates an observable that emits any discovered nearby messages using the given strategy and
     * {@link MessageFilter#INCLUDE_ALL_MY_TYPES}.
     *
     * @see #getSubscriberObservable(Context, Strategy, MessageFilter)
     *
     * @param context to be used in the {@link com.google.android.gms.common.api.GoogleApiClient}
     * @param strategy to follow when subscribing
     * @return an observable that emits any discovered messages
     */
    public static Observable<NearbySubscriberEvent> getSubscriberObservable(@NonNull Context context, @NonNull Strategy strategy) {
        return getSubscriberObservable(context, strategy, MessageFilter.INCLUDE_ALL_MY_TYPES);
    }

    /**
     * Creates an observable that emits any discovered nearby messages using {@link Strategy#DEFAULT}
     * and the given message filter.
     *
     * @see #getSubscriberObservable(Context, Strategy, MessageFilter)
     *
     * @param context to be used in the {@link com.google.android.gms.common.api.GoogleApiClient}
     * @param messageFilter to specify which messages you want
     * @return an observable that emits any discovered messages
     */
    public static Observable<NearbySubscriberEvent> getSubscriberObservable(@NonNull Context context, @NonNull MessageFilter messageFilter) {
        return getSubscriberObservable(context, Strategy.DEFAULT, messageFilter);
    }

    /**
     * Creates an observable that emits any discovered nearby messages using the given strategy and
     * message filter.
     * <p/>
     * As soon as the device starts successfully subscribing and listening for messages, a
     * {@link NearbySubscriberEvent#SUBSCRIBED} event is emitted with no message. When a message is
     * detected a {@link NearbySubscriberEvent#FOUND} event is emitted with the corresponding
     * {@link Message}. When a message can no longer be detected a {@link NearbySubscriberEvent#LOST}
     * event is emitted with the {@link Message} that was lost.
     * <p/>
     * This observable will potentially call {@link com.asadmshah.rxnearbymessages.exceptions.GoogleApiClientSuspendedException}
     * or {@link com.asadmshah.rxnearbymessages.exceptions.GoogleApiClientFailedException} if any
     * errors occur with the {@link com.google.android.gms.common.api.GoogleApiClient} connection.
     * If any errors occur within the Nearby Messages API they will be reported through
     * {@link com.asadmshah.rxnearbymessages.exceptions.NearbyStatusException}.
     *
     * @param context to be used in the {@link com.google.android.gms.common.api.GoogleApiClient}
     * @param strategy to follow when subscribing
     * @param filter to specify what messages you want
     * @return an observable that emits any discovered messages
     */
    public static Observable<NearbySubscriberEvent> getSubscriberObservable(@NonNull final Context context, @NonNull final Strategy strategy, @NonNull final MessageFilter filter) {
        return Observable.defer(new Func0<Observable<NearbySubscriberEvent>>() {
            @Override
            public Observable<NearbySubscriberEvent> call() {
                return Observable.create(new NearbySubscriberOnSubscribe(context, strategy, filter));
            }
        });
    }

    /**
     * Creates an observable that emits any events when publishing the message using
     * {@link Strategy#DEFAULT}.
     *
     * @see #getPublisherObservable(Context, Message, Strategy)
     *
     * @param context to be used in the {@link com.google.android.gms.common.api.GoogleApiClient}
     * @param message to publish
     * @return an observable that emits any events when publishing the message
     */
    public static Observable<Message> getPublisherObservable(@NonNull final Context context, @NonNull final Message message) {
        return getPublisherObservable(context, message, Strategy.DEFAULT);
    }

    /**
     * Creates an observable that emits any events when publishing the message using the given strategy.
     * <p/>
     * As soon as the device successfully starts publishing, the {@link Message} being published is
     * pushed to the observer.
     * <p/>
     * Once the TTL defined in the strategy is hit, an onComplete call is emitted to the observer.
     * <p/>
     * This observable will potentially call {@link com.asadmshah.rxnearbymessages.exceptions.GoogleApiClientSuspendedException}
     * or {@link com.asadmshah.rxnearbymessages.exceptions.GoogleApiClientFailedException} if any
     * errors occur with the {@link com.google.android.gms.common.api.GoogleApiClient} connection.
     * If any errors occur within the Nearby Messages API they will be reported through
     * {@link com.asadmshah.rxnearbymessages.exceptions.NearbyStatusException}.
     *
     * @param context to be used in the {@link com.google.android.gms.common.api.GoogleApiClient}
     * @param message to publish
     * @param strategy to follow when publishing
     * @return an observable that emits any events when publishing the message
     */
    public static Observable<Message> getPublisherObservable(@NonNull final Context context, @NonNull final Message message, @NonNull final Strategy strategy) {
        return Observable.defer(new Func0<Observable<Message>>() {
            @Override
            public Observable<Message> call() {
                return Observable.create(new NearbyPublisherOnSubscribe(context, message, strategy));
            }
        });
    }

}
