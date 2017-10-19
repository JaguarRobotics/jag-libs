package com.github.jaguarrobotics.jaglibs.commands;

import java.io.IOException;
import com.github.jaguarrobotics.jaglibs.net.DataSource;
import com.github.jaguarrobotics.jaglibs.net.LifecycleAdapter;
import com.github.jaguarrobotics.jaglibs.net.NetClient;

public abstract class AbstractCommand extends LifecycleAdapter {
    private final NetClient client;
    
    protected DataSource get(String source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }
        if (!source.startsWith("/")) {
            throw new IllegalArgumentException("Source must start with a '/'");
        }
        return client.subscribe("/io".concat(source));
    }
    
    protected AbstractCommand(CommandParameters params) {
        try {
            client = params.client;
            client.getLifecycle().on(this);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
