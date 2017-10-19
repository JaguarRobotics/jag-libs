package com.github.jaguarrobotics.jaglibs.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.github.jaguarrobotics.jaglibs.util.XmlNodeIterable;

public class IOConfiguration {
    public final Map<String, DataSourceConfiguration> sources;

    private static void load(Element element, Transformer transformer, Map<String, DataSourceConfiguration> sources,
                    String prefix) throws IOException, TransformerException {
        for (Node node : new XmlNodeIterable(element.getChildNodes())) {
            if (node instanceof Element) {
                String name = node.getNodeName();
                if (name.length() > 0) {
                    char first = name.charAt(0);
                    if (name.contains(".") || (first >= 'A' && first <= 'Z')) {
                        DataSourceConfiguration source = new DataSourceConfiguration((Element) node, transformer, prefix);
                        sources.put(source.fullName, source);
                    } else {
                        load((Element) node, transformer, sources, prefix.concat(name).concat("/"));
                    }
                }
            }
        }
    }

    public IOConfiguration(Element element, Transformer transformer) throws IOException, TransformerException {
        Map<String, DataSourceConfiguration> sources = new HashMap<String, DataSourceConfiguration>();
        load(element, transformer, sources, "/io/");
        this.sources = Collections.unmodifiableMap(sources);
    }
}
