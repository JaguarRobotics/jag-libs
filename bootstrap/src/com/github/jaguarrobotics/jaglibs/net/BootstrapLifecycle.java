package com.github.jaguarrobotics.jaglibs.net;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.jaguarrobotics.jaglibs.math.Null;

public class BootstrapLifecycle extends Lifecycle {
    private static final Logger log = LogManager.getLogger();
    private final NetClient     client;

    @Override
    public void shutdown() {
        try {
            client.subscribe("/lifecycle/shutdown").emit(new Null());
        } catch (IOException ex) {
            log.catching(ex);
        }
        super.shutdown();
    }

    @Override
    public void disabledStart() {
        try {
            client.subscribe("/lifecycle/start/disabled").emit(new Null());
        } catch (IOException ex) {
            log.catching(ex);
        }
        super.disabledStart();
    }

    @Override
    public void disabledEnd() {
        try {
            client.subscribe("/lifecycle/end/disabled").emit(new Null());
        } catch (IOException ex) {
            log.catching(ex);
        }
        super.disabledEnd();
    }

    @Override
    public void autonomousStart() {
        try {
            client.subscribe("/lifecycle/start/autonomous").emit(new Null());
        } catch (IOException ex) {
            log.catching(ex);
        }
        super.autonomousStart();
    }

    @Override
    public void autonomousEnd() {
        try {
            client.subscribe("/lifecycle/end/autonomous").emit(new Null());
        } catch (IOException ex) {
            log.catching(ex);
        }
        super.autonomousEnd();
    }

    @Override
    public void teleopStart() {
        try {
            client.subscribe("/lifecycle/start/teleop").emit(new Null());
        } catch (IOException ex) {
            log.catching(ex);
        }
        super.teleopStart();
    }

    @Override
    public void teleopEnd() {
        try {
            client.subscribe("/lifecycle/end/teleop").emit(new Null());
        } catch (IOException ex) {
            log.catching(ex);
        }
        super.teleopEnd();
    }

    @Override
    public void testStart() {
        try {
            client.subscribe("/lifecycle/start/test").emit(new Null());
        } catch (IOException ex) {
            log.catching(ex);
        }
        super.testStart();
    }

    @Override
    public void testEnd() {
        try {
            client.subscribe("/lifecycle/end/test").emit(new Null());
        } catch (IOException ex) {
            log.catching(ex);
        }
        super.testEnd();
    }

    BootstrapLifecycle(NetClient client) {
        this.client = client;
    }
}
