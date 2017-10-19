package com.github.jaguarrobotics.jaglibs.net;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;

class MessageDispatchThread extends Thread {
    private static class MessageUnit {
        String      topic;
        MqttMessage message;

        MessageUnit(String topic, MqttMessage message) {
            this.topic = topic;
            this.message = message;
        }
    }

    private static final Logger      log = LogManager.getLogger();
    private final Queue<MessageUnit> queue;
    private final NetClient          client;

    private void processMessage(String topic, MqttMessage message) throws Exception {
        if (client.dataSources.containsKey(topic)) {
            WeakReference<DataSource> ref = client.dataSources.get(topic);
            DataSource src = ref.get();
            if (src == null) {
                client.client.unsubscribe(topic);
                client.dataSources.remove(topic, ref);
            } else {
                src.messageArrived(message);
            }
        } else {
            client.client.unsubscribe(topic);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                MessageUnit unit = queue.poll();
                if (unit == null) {
                    Thread.sleep(10);
                } else {
                    try {
                        processMessage(unit.topic, unit.message);
                    } catch (InterruptedException ex) {
                        break;
                    } catch (Exception ex) {
                        log.catching(ex);
                    }
                }
                Thread.sleep(1);
            }
        } catch (InterruptedException ex) {
        }
    }

    public void queueMessage(String topic, MqttMessage message) {
        queue.add(new MessageUnit(topic, message));
    }

    MessageDispatchThread(NetClient client) {
        super("Message-Dispatch-Thread");
        queue = new LinkedBlockingQueue<MessageUnit>();
        this.client = client;
    }
}
