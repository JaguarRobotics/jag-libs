package com.github.jaguarrobotics.jaglibs.net;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.github.jaguarrobotics.jaglibs.math.RealCoordinate;
import com.github.jaguarrobotics.jaglibs.util.Pointer;

public class DataSource {
    private final NetClient client;
    private final String topic;
    private final Set<Consumer<RealCoordinate>> callbacks;
    
    public void on(Consumer<RealCoordinate> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        callbacks.add(callback);
        client.strongReferences.add(this);
    }
    
    public void removeListener(Consumer<RealCoordinate> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        callbacks.remove(callback);
        if (callbacks.isEmpty()) {
            client.strongReferences.remove(this);
        }
    }
    
    public void once(Consumer<RealCoordinate> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        Pointer<Consumer<RealCoordinate>> proxy = new Pointer<Consumer<RealCoordinate>>();
        proxy.value = c -> {
            removeListener(proxy.value);
            callback.accept(c);
        };
        on(proxy.value);
    }
    
    public void emit(RealCoordinate value) throws MqttException {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        client.client.getTopic(topic).publish(value.serialize().array(), 2, false);
    }
    
    @SuppressWarnings("unchecked")
    void messageArrived(MqttMessage message) throws Exception {
        RealCoordinate c = RealCoordinate.deserialize(ByteBuffer.wrap(message.getPayload()));
        for (Consumer<RealCoordinate> callback : callbacks.toArray(new Consumer[0])) {
            callback.accept(c);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        client.client.unsubscribe(topic);
    }

    DataSource(NetClient client, String topic) throws MqttException {
        this.client = client;
        this.topic = topic;
        callbacks = new HashSet<Consumer<RealCoordinate>>();
        client.client.subscribe(topic, 2);
    }
}
