package com.github.jaguarrobotics.jaglibs.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import com.github.jaguarrobotics.jaglibs.net.DataSource;
import com.github.jaguarrobotics.jaglibs.net.NetClient;
import com.github.jaguarrobotics.jaglibs.util.EventLoop;

@Plugin(name = "NetworkEgressAppender", category = "Core", elementType = "appender", printObject = true)
public class NetworkEgressAppender extends AbstractAppender {
    private static DataSource logSource;

    public static void setClient(NetClient client) throws IOException {
        logSource = client.subscribe("/log");
    }

    @Override
    public void append(LogEvent event) {
        if (!(event instanceof NetworkProducedLogEvent)) {
            if (logSource == null) {
                EventLoop.run(()->NetworkLogSource.log(event));
            } else {
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                                ObjectOutputStream stream = new ObjectOutputStream(buffer)) {
                    stream.writeObject(event);
                    logSource.emit(ByteBuffer.wrap(buffer.toByteArray()), 0, false);
                } catch (IOException ex) {
                    EventLoop.run(()->NetworkLogSource.log(event));
                    Throwable t = ex.getCause();
                    if (t == null || !(t instanceof MqttException)) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @PluginFactory
    public static NetworkEgressAppender createAppender(@PluginAttribute("name") String name) {
        return new NetworkEgressAppender(name);
    }

    private NetworkEgressAppender(String name) {
        super(name, null, null, false);
    }
}
