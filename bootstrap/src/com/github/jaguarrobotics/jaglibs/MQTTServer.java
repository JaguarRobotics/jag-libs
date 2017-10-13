package com.github.jaguarrobotics.jaglibs;

import java.util.Properties;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import io.moquette.server.Server;

public class MQTTServer {
    private Server server;
    private MqttClient client;
    
    public void start() throws Exception {
        server = new Server();
        server.startServer(new Properties());
        Runtime.getRuntime().addShutdownHook(new Thread(server::stopServer));
        client = new MqttClient("tcp://localhost:1883", "server", new MemoryPersistence());
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setCleanSession(true);
        client.connect(opts);
        client.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                MqttMessage msg = new MqttMessage();
                msg.setPayload(message.getPayload());
                msg.setQos(2);
                msg.setRetained(false);
                client.getTopic("hey").publish(msg);
            }
            
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
            
            @Override
            public void connectionLost(Throwable cause) {
            }
        });
        client.subscribe("hello");
    }
}
