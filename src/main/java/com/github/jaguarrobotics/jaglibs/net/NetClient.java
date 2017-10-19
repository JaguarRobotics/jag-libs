package com.github.jaguarrobotics.jaglibs.net;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class NetClient implements AutoCloseable, MqttCallback {
    MqttClient                                   client;
    final Map<String, WeakReference<DataSource>> dataSources;
    final Set<DataSource>                        strongReferences;
    protected Lifecycle                          lifecycle;
    private MessageDispatchThread                loop;

    @Override
    public void close() throws Exception {
        loop.interrupt();
        client.disconnect();
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        loop.queueMessage(topic, message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public DataSource subscribe(String topic) throws IOException {
        WeakReference<DataSource> ref;
        DataSource src;
        if (dataSources.containsKey(topic)) {
            ref = dataSources.get(topic);
            src = ref.get();
            if (src == null) {
                src = new DataSource(this, topic);
                ref = new WeakReference<DataSource>(src);
                dataSources.put(topic, ref);
            }
        } else {
            src = new DataSource(this, topic);
            ref = new WeakReference<DataSource>(src);
            dataSources.put(topic, ref);
        }
        return src;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    protected void createLifecycle() throws IOException {
        lifecycle = new RemoteLifecycle(this);
    }

    protected void connect(String host) throws IOException {
        try {
            client = new MqttClient(String.format("tcp://%s:1883", host), MqttClient.generateClientId(),
                            new MemoryPersistence());
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setCleanSession(true);
            client.setCallback(this);
            client.connect(opts);
            loop = new MessageDispatchThread(this);
            loop.start();
            createLifecycle();
        } catch (MqttException ex) {
            throw new IOException(ex);
        }
    }

    protected NetClient() {
        dataSources = Collections.synchronizedMap(new HashMap<String, WeakReference<DataSource>>());
        strongReferences = Collections.synchronizedSet(new HashSet<DataSource>());
    }

    public NetClient(String host) throws IOException {
        this();
        connect(host);
    }
}
