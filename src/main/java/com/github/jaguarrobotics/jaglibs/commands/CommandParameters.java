package com.github.jaguarrobotics.jaglibs.commands;

import com.github.jaguarrobotics.jaglibs.net.NetClient;

public class CommandParameters {
    public final NetClient client;
    public final String    configXml;

    public CommandParameters(NetClient client, String configXml) {
        this.client = client;
        this.configXml = configXml;
    }
}
