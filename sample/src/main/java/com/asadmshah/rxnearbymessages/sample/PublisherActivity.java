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

import com.asadmshah.rxnearbymessages.RxNearbyMessages;
import com.asadmshah.rxnearbymessages.exceptions.NearbyStatusException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.Strategy;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PublisherActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Strategy STRATEGY = new Strategy.Builder().setTtlSeconds(15).build();
    private static final int RC_RESOLUTION = 2;

    private Subscription subscription;

    private EditText viewMessage;
    private EditText viewType;
    private TextView viewStatus;
    private TextView viewCurrentMessage;
    private TextView viewCurrentType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        viewMessage = (EditText) findViewById(R.id.message);
        viewType = (EditText) findViewById(R.id.type);
        viewStatus = (TextView) findViewById(R.id.status);
        viewCurrentMessage = (TextView) findViewById(R.id.current_message);
        viewCurrentType = (TextView) findViewById(R.id.current_type);

        findViewById(R.id.start_publishing).setOnClickListener(this);
        setStatusNotPublishing();
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
            findViewById(R.id.start_publishing).performClick();
        }
    }

    @Override
    public void onClick(View v) {
        viewMessage.clearFocus();
        viewType.clearFocus();

        String text = viewMessage.getText().toString();
        if (text.length() > Message.MAX_CONTENT_SIZE_BYTES) {
            showToast("Message length cannot exceed " + Message.MAX_CONTENT_SIZE_BYTES + " bytes");
            return;
        }

        String type = viewType.getText().toString();
        if (type.length() > Message.MAX_TYPE_LENGTH) {
            showToast("Type length cannot exceed " + Message.MAX_TYPE_LENGTH + " bytes");
            return;
        }

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        setStatusAttemptingToPublish();
        subscription = RxNearbyMessages.getPublisherObservable(this, new Message(text.getBytes(), type), STRATEGY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Message>() {
                    @Override
                    public void onCompleted() {
                        setStatusNotPublishing();
                        showToast("Publishing TTL hit.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        setStatusNotPublishing();
                        if (e instanceof NearbyStatusException) {
                            handleNearbyStatusError(((NearbyStatusException) e).getStatus());
                        } else {
                            showToast("An unknown error has occurred");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Message message) {
                        setStatusPublishing(new String(message.getContent()), message.getType());
                    }
                });
    }

    private void setStatusNotPublishing() {
        viewStatus.setText("Not Publishing");
        viewCurrentMessage.setText("");
        viewCurrentType.setText("");
    }

    private void setStatusAttemptingToPublish() {
        viewStatus.setText("Attempting to Publish");
        viewCurrentMessage.setText("");
        viewCurrentType.setText("");
    }

    private void setStatusPublishing(String message, String type) {
        viewStatus.setText("Publishing");
        viewCurrentMessage.setText(message);
        viewCurrentType.setText(type);
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
