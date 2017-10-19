package com.github.jaguarrobotics.jaglibs.net;

import java.io.IOException;
import java.util.Properties;
import io.moquette.server.Server;

public class NetServer extends NetClient {
    private Server server;

    public void start() throws Exception {
        server = new Server();
        server.startServer(new Properties());
        Runtime.getRuntime().addShutdownHook(new Thread(server::stopServer));
        connect("localhost");
    }

    @Override
    public BootstrapLifecycle getLifecycle() {
        return (BootstrapLifecycle) super.getLifecycle();
    }

    @Override
    protected void createLifecycle() throws IOException {
        lifecycle = new BootstrapLifecycle(this);
    }

    public NetServer() throws IOException {
    }
}
