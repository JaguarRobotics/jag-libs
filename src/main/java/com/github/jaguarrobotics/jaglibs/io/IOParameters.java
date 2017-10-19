package com.github.jaguarrobotics.jaglibs.io;

import com.github.jaguarrobotics.jaglibs.net.NetClient;

public class IOParameters {
    public final NetClient client;
    public final String    configXml;
    public final String    topic;
    
    public IOParameters(NetClient client, String configXml, String topic) {
        this.client = client;
        this.configXml = configXml;
        this.topic = topic;
    }
}
