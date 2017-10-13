package com.github.jaguarrobotics.jaglibs.net;

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
    final MqttClient client;
    final Map<String, WeakReference<DataSource>> dataSources;
    final Set<DataSource> strongReferences;
    
    @Override
    public void close() throws Exception {
        client.disconnect();
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (dataSources.containsKey(topic)) {
            WeakReference<DataSource> ref = dataSources.get(topic);
            DataSource src = ref.get();
            if (src == null) {
                client.unsubscribe(topic);
                dataSources.remove(topic, ref);
            } else {
                src.messageArrived(message);
            }
        } else {
            client.unsubscribe(topic);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
    
    public DataSource subscribe(String topic) throws MqttException {
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
    
    public NetClient(String host) throws MqttException {
        client = new MqttClient(host, MqttClient.generateClientId(), new MemoryPersistence());
        dataSources = Collections.synchronizedMap(new HashMap<String, WeakReference<DataSource>>());
        strongReferences = Collections.synchronizedSet(new HashSet<DataSource>());
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setCleanSession(true);
        client.connect(opts);
        client.setCallback(this);
    }
}
