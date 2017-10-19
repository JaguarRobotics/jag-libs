package com.github.jaguarrobotics.jaglibs.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.github.jaguarrobotics.jaglibs.util.XmlNodeIterable;

public class PeriodConfiguration {
    public final String                     name;
    public final List<CommandConfiguration> commands;

    public PeriodConfiguration(Element element, Transformer transformer) throws IOException, TransformerException {
        name = element.getTagName();
        List<CommandConfiguration> commands = new ArrayList<CommandConfiguration>();
        for (Node node : new XmlNodeIterable(element.getChildNodes())) {
            if (node instanceof Element) {
                commands.add(new CommandConfiguration((Element) node, transformer));
            }
        }
        this.commands = Collections.unmodifiableList(commands);
    }
}
