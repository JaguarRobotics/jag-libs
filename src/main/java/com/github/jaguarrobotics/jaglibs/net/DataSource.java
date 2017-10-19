package com.github.jaguarrobotics.jaglibs.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.github.jaguarrobotics.jaglibs.math.RealCoordinate;
import com.github.jaguarrobotics.jaglibs.util.Pointer;

public class DataSource {
    private static final Logger                 log = LogManager.getLogger();
    private final NetClient                     client;
    private final String                        topic;
    private final Set<Consumer<RealCoordinate>> callbacks;
    private final Set<Consumer<ByteBuffer>>     rawCallbacks;

    public void on(Consumer<RealCoordinate> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        callbacks.add(callback);
        client.strongReferences.add(this);
    }

    public void onRaw(Consumer<ByteBuffer> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        rawCallbacks.add(callback);
        client.strongReferences.add(this);
    }

    public void removeListener(Consumer<RealCoordinate> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        callbacks.remove(callback);
        if (callbacks.isEmpty() && rawCallbacks.isEmpty()) {
            client.strongReferences.remove(this);
        }
    }

    public void removeRawListener(Consumer<ByteBuffer> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        rawCallbacks.remove(callback);
        if (callbacks.isEmpty() && rawCallbacks.isEmpty()) {
            client.strongReferences.remove(this);
        }
    }

    public void once(Consumer<RealCoordinate> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        Pointer<Consumer<RealCoordinate>> proxy = new Pointer<Consumer<RealCoordinate>>();
        proxy.value = c-> {
            removeListener(proxy.value);
            callback.accept(c);
        };
        on(proxy.value);
    }

    public void onceRaw(Consumer<ByteBuffer> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        Pointer<Consumer<ByteBuffer>> proxy = new Pointer<Consumer<ByteBuffer>>();
        proxy.value = c-> {
            removeRawListener(proxy.value);
            callback.accept(c);
        };
        onRaw(proxy.value);
    }

    public void emit(ByteBuffer value, int qos, boolean retain) throws IOException {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        if (qos < 0 || qos > 2) {
            throw new IllegalArgumentException("Invalid QoS value");
        }
        try {
            client.client.getTopic(topic).publish(value.array(), qos, retain);
        } catch (MqttException ex) {
            switch (ex.getReasonCode()) {
                case MqttException.REASON_CODE_CLIENT_NOT_CONNECTED:
                case MqttException.REASON_CODE_CONNECTION_LOST:
                    try {
                        client.client.connect();
                        client.client.getTopic(topic).publish(value.array(), qos, retain);
                    } catch (MqttException e) {
                        throw new IOException(e);
                    }
                    break;
                default:
                    throw new IOException(ex);
            }
        }
    }

    public void emit(ByteBuffer value) throws IOException {
        emit(value, 2, false);
    }

    public void emit(RealCoordinate value) throws IOException {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        emit(value.serialize());
    }

    @SuppressWarnings("unchecked")
    void messageArrived(MqttMessage message) throws Exception {
        if (!rawCallbacks.isEmpty()) {
            for (Consumer<ByteBuffer> callback : rawCallbacks.toArray(new Consumer[0])) {
                callback.accept(ByteBuffer.wrap(message.getPayload()));
            }
        }
        if (!callbacks.isEmpty()) {
            RealCoordinate c = RealCoordinate.deserialize(ByteBuffer.wrap(message.getPayload()));
            for (Consumer<RealCoordinate> callback : callbacks.toArray(new Consumer[0])) {
                callback.accept(c);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        log.debug("Unsubscribing from '{}'.", topic);
        client.client.unsubscribe(topic);
    }

    DataSource(NetClient client, String topic) throws IOException {
        log.debug("Subscribing to '{}'.", topic);
        this.client = client;
        this.topic = topic;
        callbacks = new HashSet<Consumer<RealCoordinate>>();
        rawCallbacks = new HashSet<Consumer<ByteBuffer>>();
        try {
            client.client.subscribe(topic, 2);
        } catch (MqttException ex) {
            throw new IOException(ex);
        }
    }
}
