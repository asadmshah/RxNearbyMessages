# RxNearbyMessages

Wraps the Nearby Messages API using [RxJava](https://github.com/ReactiveX/RxJava) observables.

> The Nearby Messages API is a publish-subscribe API that lets you pass small binary payloads
> between internet-connected Android and iOS devices. The devices don't have to be on the same
> network, but they do have to be connected to the Internet.
>
> Nearby uses a combination of Bluetooth, Bluetooth Low Energy, Wi-Fi and an ultrasonic modem to
> communicate a unique-in-time pairing code between devices. The server facilitates message exchange
> between devices that detect the same pairing code.

[Learn More](https://developers.google.com/nearby/messages/overview)

## Download

via Maven:
```xml
<dependency>
    <groupId>com.asadmshah.rxnearbymessages</groupId>
    <artifactId>rxnearbymessages</artifactId>
    <version>0.1.0</version>
</dependency>
```

via Gradle:
```groovy
compile 'com.asadmshah.rxnearbymessages:rxnearbymessages:0.1.0'
```

## Usage

Before you get started with the Nearby Messages API, you're going to need to specify an API key in
your manifest:
```xml
<meta-data android:name="com.google.android.nearby.messages.API_KEY"
           android:value="YOUR_KEY_GOES_HERE" />
```

#### Publish

Calling the `RxNearbyMessages.getPublisherObservable` methods let you publish messages to other
devices.

```java
RxNearbyMessages.getPublisherObservable(context, new Message("Hello World".getBytes()))
    .subscribe(new Subscriber<Message> {
        @Override
        public void onNext(Message message) {
            // Once your device successfully starts publishing a message, that message will be
            // reported here. This will only occur once.
        }

        @Override
        public void onCompleted() {
            // This will only get called when the provided strategy TTL expires.
        }

        @Override
        public void onError(Throwable e) {
            // Will be one of GoogleApiClientFailedException, GoogleApiClientSuspendedException, or
            // NearbyStatusException.
        }
    });
```

`getPublisherObservable(Context context, Message message, Strategy strategy)` is provided to pass
in a custom strategy to your publisher.

#### Subscribing

Calling the `RxNearbyMessages.getSubscriberObservable` methods let you subscribe and listen to
messages being published by other devices:

```java
RxNearbyMessages.getSubscriberObservable(context)
    .subscribe(new Subscriber<NearbySubscriberEvent> {
        @Override
        public void onNext(NearbySubscriberEvent event) {
            // Once the the API successfully starts subscribing a NearbySubscriberEvent.SUBSCRIBED
            // event will be emitted. Any events following it will be NearbySubscriberEvent.FOUND
            // or NearbySubscriber.LOST.
        }

        @Override
        public void onCompleted() {
            // This will only get called when the provided strategy TTL expires.
        }

        @Override
        public void onError(Throwable e) {
            // Will be one of GoogleApiClientFailedException, GoogleApiClientSuspendedException, or
            // NearbyStatusException.
        }
    });
```

Additional methods are provided to pass in a custom `Strategy` and `MessageFilters` when creating a
subscriber.

#### Stopping Publisher/Subscriber

Unsubscribing from the `Observable` is the only way to go through the unpublish/unsubscribe flow,
so remember to do that.

## Sample Application

The sample application requires a `nearby.key` to be defined in your `local.properties` file.

## License

    Copyright 2015 Asad Shah

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

