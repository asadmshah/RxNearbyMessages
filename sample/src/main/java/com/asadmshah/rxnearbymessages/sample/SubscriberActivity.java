package com.asadmshah.rxnearbymessages.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asadmshah.rxnearbymessages.NearbySubscriberEvent;
import com.asadmshah.rxnearbymessages.RxNearbyMessages;
import com.asadmshah.rxnearbymessages.exceptions.NearbyStatusException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.Strategy;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SubscriberActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Strategy STRATEGY = new Strategy.Builder().setTtlSeconds(15).build();
    private static final int RC_RESOLUTION = 2;

    private Subscription subscription;

    private EditText viewType;
    private TextView viewStatus;
    private TextView viewCurrentMessage;
    private TextView viewCurrentEvent;
    private TextView viewCurrentType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        viewType = (EditText) findViewById(R.id.type);
        viewStatus = (TextView) findViewById(R.id.status);
        viewCurrentMessage = (TextView) findViewById(R.id.current_message);
        viewCurrentEvent = (TextView) findViewById(R.id.current_event);
        viewCurrentType = (TextView) findViewById(R.id.current_type);

        findViewById(R.id.start_subscribing).setOnClickListener(this);
        setStatusNotSubscribing();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_RESOLUTION && resultCode == Activity.RESULT_OK) {
            findViewById(R.id.start_subscribing).performClick();
        }
    }

    @Override
    public void onClick(View v) {
        viewType.clearFocus();

        String type = viewType.getText().toString();
        if (type.length() > Message.MAX_TYPE_LENGTH) {
            showToast("Type length cannot exceed " + Message.MAX_TYPE_LENGTH + " bytes");
            return;
        }
        MessageFilter filter = new MessageFilter.Builder()
                .includeNamespacedType("", type)
                .build();

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        setStatusSubscribe();

        subscription = RxNearbyMessages.getSubscriberObservable(this, STRATEGY, filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NearbySubscriberEvent>() {
                    @Override
                    public void onCompleted() {
                        setStatusNotSubscribing();
                        showToast("Subscriber TTL hit");
                    }

                    @Override
                    public void onError(Throwable e) {
                        setStatusNotSubscribing();
                        if (e instanceof NearbyStatusException) {
                            handleNearbyStatusError(((NearbyStatusException) e).getStatus());
                        } else {
                            showToast("Unknown error occured");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(NearbySubscriberEvent nearbySubscriberEvent) {
                        setStatusOnEvent(nearbySubscriberEvent);
                    }
                });
    }

    private void setStatusNotSubscribing() {
        viewStatus.setText("Not Subscribing");
        viewCurrentMessage.setText("");
        viewCurrentEvent.setText("");
        viewCurrentType.setText("");
    }

    private void setStatusSubscribe() {
        viewStatus.setText("Subscribing");
        viewCurrentMessage.setText("");
        viewCurrentEvent.setText("");
        viewCurrentType.setText("");
    }

    private void setStatusOnEvent(NearbySubscriberEvent event) {
        viewStatus.setText("Last Event");
        viewCurrentMessage.setText(new String(event.message.getContent()));
        switch (event.kind) {
            case NearbySubscriberEvent.SUBSCRIBED:
                viewCurrentEvent.setText("Subscribed");
                break;
            case NearbySubscriberEvent.FOUND:
                viewCurrentEvent.setText("Found");
                break;
            case NearbySubscriberEvent.LOST:
                viewCurrentEvent.setText("Lost");
                break;
        }
        viewCurrentType.setText(event.message.getType());
    }

    private void handleNearbyStatusError(Status status) {
        if (status.getStatusCode() == NearbyMessagesStatusCodes.APP_NOT_OPTED_IN) {
            try {
                status.startResolutionForResult(this, RC_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                showToast("Unable to start resolution for error");
                e.printStackTrace();
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
