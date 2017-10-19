package com.github.jaguarrobotics.jaglibs.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractFactory extends LifecycleAdapter {
    private static final Logger log = LogManager.getLogger();
    protected final NetClient   client;
    Function<String, String> classNameTransformer;

    protected abstract void create(ByteBuffer buffer);

    protected <T> void tryCreate(String className, T arg, Class<T> typeOfArg) {
        try {
            Class<?> cls = Class.forName(classNameTransformer.apply(className));
            Constructor<?> ctor = cls.getDeclaredConstructor(typeOfArg);
            ctor.setAccessible(true);
            ctor.newInstance(arg);
        } catch (Exception ex) {
            log.catching(ex);
        }
    }

    protected String decodeString(ByteBuffer buffer) throws IOException {
        try {
            int len = buffer.getInt();
            byte[] data = new byte[len];
            buffer.get(data);
            return new String(data, "UTF8");
        } catch (UnsupportedEncodingException ex) {
            throw new IOException(ex);
        }
    }

    AbstractFactory(NetClient client, String topic) throws IOException {
        this.client = client;
        classNameTransformer = Function.identity();
        client.subscribe(topic).onRaw(this::create);
    }
}
