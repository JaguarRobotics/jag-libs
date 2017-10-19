package com.github.jaguarrobotics.jaglibs.config;

import java.io.IOException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;

public class CommandConfiguration extends AbstractClassConfiguration {
    public CommandConfiguration(Element element, Transformer transformer) throws IOException, TransformerException {
        super(element, transformer, "com.github.jaguarrobotics.jaglibs.commands.library.");
    }
}
