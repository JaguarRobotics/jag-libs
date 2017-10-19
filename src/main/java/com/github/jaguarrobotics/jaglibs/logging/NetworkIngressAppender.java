package com.github.jaguarrobotics.jaglibs.logging;

import java.io.IOException;
import java.io.Serializable;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "NetworkIngressAppender", category = "Core", elementType = "appender", printObject = true)
public class NetworkIngressAppender extends AbstractAppender {
    @Override
    public void append(LogEvent event) {
        if (event instanceof NetworkProducedLogEvent) {
            try {
                System.out.write(getLayout().toByteArray(event));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @PluginFactory
    public static NetworkIngressAppender createAppender(@PluginAttribute("name") String name,
                    @PluginElement("Layout") Layout<? extends Serializable> layout) {
        return new NetworkIngressAppender(name, layout);
    }

    private NetworkIngressAppender(String name, Layout<? extends Serializable> layout) {
        super(name, null, layout, false);
    }
}
