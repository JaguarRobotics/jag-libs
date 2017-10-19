package com.github.jaguarrobotics.jaglibs.net;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.util.Debug;
import com.github.jaguarrobotics.jaglibs.util.EventLoop;

public class WrappedMqttClient implements MqttCallback {
    @FunctionalInterface
    private interface Callback {
        void run() throws MqttException;
    }

    private static final Logger log = LogManager.getLogger();
    private final MqttClient    client;
    private MqttCallback        callback;
    private Semaphore           connectionLock;
    private Set<Thread>         waitingThreads;

    private boolean tryConnectHead() throws MqttException {
        if (connectionLock.tryAcquire()) {
            return true;
        } else {
            Thread current = Thread.currentThread();
            waitingThreads.add(current);
            try {
                while (true) {
                    Thread.sleep(Long.MAX_VALUE);
                }
            } catch (InterruptedException ex) {
                waitingThreads.remove(current);
            }
            return false;
        }
    }

    private void tryConnectTail() throws MqttException {
        connectionLock.release();
        while (!waitingThreads.isEmpty()) {
            for (Thread thread : waitingThreads.toArray(new Thread[0])) {
                thread.interrupt();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.catching(cause);
        if (callback != null) {
            callback.connectionLost(cause);
        }
        try {
            connect();
        } catch (MqttException ex) {
            log.catching(ex);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (callback != null) {
            callback.messageArrived(topic, message);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        if (callback != null) {
            callback.deliveryComplete(token);
        }
    }

    private void connectionRequired(Callback runnable) throws MqttException {
        EventLoop.run(()-> {
            try {
                runnable.run();
            } catch (MqttException ex) {
                try {
                    connect();
                    runnable.run();
                } catch (MqttException e) {
                    log.catching(ex);
                    log.catching(e);
                }
            }
        });
    }

    public int hashCode() {
        return client.hashCode();
    }

    public boolean equals(Object obj) {
        return client.equals(obj);
    }

    public String toString() {
        return client.toString();
    }

    public void connect() throws MqttSecurityException, MqttException {
        if (tryConnectHead()) {
            try {
                client.connect();
            } finally {
                tryConnectTail();
            }
        }
    }

    public void connect(MqttConnectOptions options) throws MqttSecurityException, MqttException {
        if (tryConnectHead()) {
            try {
                client.connect(options);
            } finally {
                tryConnectTail();
            }
        }
    }

    public IMqttToken connectWithResult(MqttConnectOptions options) throws MqttSecurityException, MqttException {
        IMqttToken token = null;
        if (tryConnectHead()) {
            try {
                token = client.connectWithResult(options);
            } finally {
                tryConnectTail();
            }
        }
        return token;
    }

    public void disconnect() throws MqttException {
        client.disconnect();
    }

    public void disconnect(long quiesceTimeout) throws MqttException {
        client.disconnect(quiesceTimeout);
    }

    public void disconnectForcibly() throws MqttException {
        client.disconnectForcibly();
    }

    public void disconnectForcibly(long disconnectTimeout) throws MqttException {
        client.disconnectForcibly(disconnectTimeout);
    }

    public void disconnectForcibly(long quiesceTimeout, long disconnectTimeout) throws MqttException {
        client.disconnectForcibly(quiesceTimeout, disconnectTimeout);
    }

    public void subscribe(String topicFilter) throws MqttException {
        connectionRequired(()->client.subscribe(topicFilter));
    }

    public void subscribe(String[] topicFilters) throws MqttException {
        connectionRequired(()->client.subscribe(topicFilters));
    }

    public void subscribe(String topicFilter, int qos) throws MqttException {
        connectionRequired(()->client.subscribe(topicFilter, qos));
    }

    public void subscribe(String[] topicFilters, int[] qos) throws MqttException {
        connectionRequired(()->client.subscribe(topicFilters, qos));
    }

    public void unsubscribe(String topicFilter) throws MqttException {
        connectionRequired(()->client.unsubscribe(topicFilter));
    }

    public void unsubscribe(String[] topicFilters) throws MqttException {
        connectionRequired(()->client.unsubscribe(topicFilters));
    }

    public void publish(String topic, byte[] payload, int qos, boolean retained)
                    throws MqttException, MqttPersistenceException {
        connectionRequired(()->client.publish(topic, payload, qos, retained));
    }

    public void publish(String topic, MqttMessage message) throws MqttException, MqttPersistenceException {
        connectionRequired(()->client.publish(topic, message));
    }

    public void setTimeToWait(long timeToWaitInMillis) throws IllegalArgumentException {
        client.setTimeToWait(timeToWaitInMillis);
    }

    public long getTimeToWait() {
        return client.getTimeToWait();
    }

    public void close() throws MqttException {
        client.close();
    }

    public String getClientId() {
        return client.getClientId();
    }

    public IMqttDeliveryToken[] getPendingDeliveryTokens() {
        return client.getPendingDeliveryTokens();
    }

    public String getServerURI() {
        return client.getServerURI();
    }

    public MqttTopic getTopic(String topic) {
        return client.getTopic(topic);
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public void setCallback(MqttCallback callback) {
        this.callback = callback;
    }

    public Debug getDebug() {
        return client.getDebug();
    }

    WrappedMqttClient(MqttClient client) {
        this.client = client;
        connectionLock = new Semaphore(1);
        waitingThreads = Collections.synchronizedSet(new HashSet<Thread>());
        client.setCallback(this);
    }
}
