package com.github.jaguarrobotics.jaglibs.logging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import com.github.jaguarrobotics.jaglibs.net.NetClient;

public class NetworkLogSource {
    private static final Logger        log    = LogManager.getLogger();
    private static final Configuration config = ((LoggerContext) LogManager.getContext()).getConfiguration();

    static void log(LogEvent event) {
        String name = event.getLoggerName();
        String fqcn = event.getLoggerFqcn();
        config.getLoggerConfig(name == null ? fqcn == null ? "undefined" : fqcn : name).log(new NetworkProducedLogEvent(event));
    }

    public void handleLog(ByteBuffer data) {
        LogEvent ev = null;
        try (ByteArrayInputStream buffer = new ByteArrayInputStream(data.array());
                        ObjectInputStream stream = new ObjectInputStream(buffer)) {
            ev = (LogEvent) stream.readObject();
        } catch (Exception ex) {
            log.catching(ex);
        }
        log(ev);
    }

    public NetworkLogSource(NetClient client) throws IOException {
        client.subscribe("/log").onRaw(this::handleLog);
    }
}
