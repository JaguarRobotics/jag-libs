package com.github.jaguarrobotics.jaglibs.config;

import java.io.IOException;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;

public abstract class AbstractClassConfiguration {
    public final String className;
    public final String configurationXml;

    protected AbstractClassConfiguration(Element element, Transformer transformer, String packageName) throws IOException, TransformerException {
        String tagName = element.getTagName();
        if (tagName.contains(".")) {
            className = tagName;
        } else {
            className = packageName.concat(tagName);
        }
        try (StringWriter writer = new StringWriter()) {
            transformer.transform(new DOMSource(element), new StreamResult(writer));
            configurationXml = writer.toString();
        }
    }
}
