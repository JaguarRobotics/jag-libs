package com.github.jaguarrobotics.jaglibs.io;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.jaguarrobotics.jaglibs.math.RealCoordinate;
import com.github.jaguarrobotics.jaglibs.net.DataSource;
import com.github.jaguarrobotics.jaglibs.net.LifecycleAdapter;

public abstract class AbstractIODevice extends LifecycleAdapter {
    private static final Logger log = LogManager.getLogger();
    private final DataSource    source;
    private boolean             isStarted;

    protected abstract void start();

    protected abstract void stop();

    protected void setValue(RealCoordinate val) {
        try {
            source.emit(val);
        } catch (IOException ex) {
            log.catching(ex);
        }
    }

    @Override
    public void disabledStart() {
        if (isStarted) {
            isStarted = false;
            stop();
        }
    }

    @Override
    public void disabledEnd() {
        if (!isStarted) {
            isStarted = true;
            start();
        }
    }

    @Override
    public void shutdown() {
        if (isStarted) {
            isStarted = false;
            stop();
        }
    }

    protected AbstractIODevice(IOParameters params) {
        try {
            source = params.client.subscribe(params.topic);
            params.client.getLifecycle().on(this);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
