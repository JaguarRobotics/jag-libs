package com.github.jaguarrobotics.jaglibs.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.jaguarrobotics.jaglibs.io.IOParameters;

public class IOFactory extends AbstractFactory {
    private static final Logger log = LogManager.getLogger();
    
    @Override
    protected void create(ByteBuffer buffer) {
        try {
            String className = decodeString(buffer);
            String configXml = decodeString(buffer);
            String topicName = decodeString(buffer);
            tryCreate(className, new IOParameters(client, configXml, topicName), IOParameters.class);
        } catch (Exception ex) {
            log.catching(ex);
        }
    }

    public IOFactory(NetClient client) throws IOException {
        super(client, "/factory/io");
    }
}
