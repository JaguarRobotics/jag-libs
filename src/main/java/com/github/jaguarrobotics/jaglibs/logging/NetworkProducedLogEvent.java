package com.github.jaguarrobotics.jaglibs.logging;

import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext.ContextStack;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

class NetworkProducedLogEvent implements LogEvent {
    private static final long serialVersionUID = -3387312401046081472L;
    private final LogEvent    real;

    @Override
    public LogEvent toImmutable() {
        return real.toImmutable();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Map<String, String> getContextMap() {
        return real.getContextMap();
    }

    @Override
    public ReadOnlyStringMap getContextData() {
        return real.getContextData();
    }

    @Override
    public ContextStack getContextStack() {
        return real.getContextStack();
    }

    @Override
    public String getLoggerFqcn() {
        return real.getLoggerFqcn();
    }

    @Override
    public Level getLevel() {
        return real.getLevel();
    }

    @Override
    public String getLoggerName() {
        return real.getLoggerName();
    }

    @Override
    public Marker getMarker() {
        return real.getMarker();
    }

    @Override
    public Message getMessage() {
        return real.getMessage();
    }

    @Override
    public long getTimeMillis() {
        return real.getTimeMillis();
    }

    @Override
    public StackTraceElement getSource() {
        return real.getSource();
    }

    @Override
    public String getThreadName() {
        return real.getThreadName();
    }

    @Override
    public long getThreadId() {
        return real.getThreadId();
    }

    @Override
    public int getThreadPriority() {
        return real.getThreadPriority();
    }

    @Override
    public Throwable getThrown() {
        return real.getThrown();
    }

    @Override
    public ThrowableProxy getThrownProxy() {
        return real.getThrownProxy();
    }

    @Override
    public boolean isEndOfBatch() {
        return real.isEndOfBatch();
    }

    @Override
    public boolean isIncludeLocation() {
        return real.isIncludeLocation();
    }

    @Override
    public void setEndOfBatch(boolean endOfBatch) {
        real.setEndOfBatch(endOfBatch);
    }

    @Override
    public void setIncludeLocation(boolean locationRequired) {
        real.setIncludeLocation(locationRequired);
    }

    @Override
    public long getNanoTime() {
        return real.getNanoTime();
    }
    
    NetworkProducedLogEvent(LogEvent ev) {
        real = ev;
    }
}
