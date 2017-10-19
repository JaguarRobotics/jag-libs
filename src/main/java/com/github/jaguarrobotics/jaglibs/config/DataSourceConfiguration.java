package com.github.jaguarrobotics.jaglibs.config;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;
import com.github.jaguarrobotics.jaglibs.net.DataSource;
import com.github.jaguarrobotics.jaglibs.net.NetClient;

public class DataSourceConfiguration extends AbstractClassConfiguration {
    public final String name;
    public final String fullName;
    
    public void create(NetClient client) throws IOException {
        byte[] clazz = className.getBytes("UTF8");
        byte[] config = configurationXml.getBytes("UTF8");
        byte[] topic = fullName.getBytes("UTF8");
        ByteBuffer buffer = ByteBuffer.allocate(12 + clazz.length + config.length + topic.length);
        buffer.putInt(clazz.length);
        buffer.put(clazz);
        buffer.putInt(config.length);
        buffer.put(config);
        buffer.putInt(topic.length);
        buffer.put(topic);
        client.subscribe("/factory/io").emit(buffer);
    }
    
    public DataSource createAndSubscribe(NetClient client) throws IOException {
        create(client);
        return client.subscribe(fullName);
    }

    public DataSourceConfiguration(Element element, Transformer transformer, String prefix) throws IOException, TransformerException {
        super(element, transformer, "com.github.jaguarrobotics.jaglibs.io.library.");
        name = element.getAttribute("name");
        if (name == null || name.length() == 0) {
            throw new IOException("Missing attribute 'name'");
        }
        fullName = prefix.concat(name);
    }
}
