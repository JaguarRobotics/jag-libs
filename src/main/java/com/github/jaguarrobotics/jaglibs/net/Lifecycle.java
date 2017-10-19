package com.github.jaguarrobotics.jaglibs.net;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Lifecycle {
    private static final Logger log = LogManager.getLogger();
    private final Set<ILifecycleListener> listeners;

    public void on(ILifecycleListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        listeners.add(listener);
    }

    public void removeListener(ILifecycleListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        listeners.remove(listener);
    }

    void shutdown() {
        log.debug("Lifecycle event: shutdown");
        for (ILifecycleListener listener : listeners.toArray(new ILifecycleListener[0])) {
            listener.shutdown();
        }
    }

    void disabledStart() {
        log.debug("Lifecycle event: disabledStart");
        for (ILifecycleListener listener : listeners.toArray(new ILifecycleListener[0])) {
            listener.disabledStart();
        }
    }

    void disabledEnd() {
        log.debug("Lifecycle event: disabledEnd");
        for (ILifecycleListener listener : listeners.toArray(new ILifecycleListener[0])) {
            listener.disabledEnd();
        }
    }

    void autonomousStart() {
        log.debug("Lifecycle event: autonomousStart");
        for (ILifecycleListener listener : listeners.toArray(new ILifecycleListener[0])) {
            listener.autonomousStart();
        }
    }

    void autonomousEnd() {
        log.debug("Lifecycle event: autonomousEnd");
        for (ILifecycleListener listener : listeners.toArray(new ILifecycleListener[0])) {
            listener.autonomousEnd();
        }
    }

    void teleopStart() {
        log.debug("Lifecycle event: teleopStart");
        for (ILifecycleListener listener : listeners.toArray(new ILifecycleListener[0])) {
            listener.teleopStart();
        }
    }

    void teleopEnd() {
        log.debug("Lifecycle event: teleopEnd");
        for (ILifecycleListener listener : listeners.toArray(new ILifecycleListener[0])) {
            listener.teleopEnd();
        }
    }

    void testStart() {
        log.debug("Lifecycle event: testStart");
        for (ILifecycleListener listener : listeners.toArray(new ILifecycleListener[0])) {
            listener.testStart();
        }
    }

    void testEnd() {
        log.debug("Lifecycle event: testEnd");
        for (ILifecycleListener listener : listeners.toArray(new ILifecycleListener[0])) {
            listener.testEnd();
        }
    }

    Lifecycle() {
        listeners = Collections.synchronizedSet(new HashSet<ILifecycleListener>());
    }
}
