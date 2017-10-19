package com.github.jaguarrobotics.jaglibs.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.github.jaguarrobotics.jaglibs.util.XmlNodeIterable;

public class RobotConfiguration {
    public final Map<String, PeriodConfiguration> periods;
    public final IOConfiguration                  io;

    public RobotConfiguration(Document doc) throws IOException, TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        Map<String, PeriodConfiguration> periods = new HashMap<String, PeriodConfiguration>();
        IOConfiguration io = null;
        for (Node element : new XmlNodeIterable(doc.getDocumentElement().getChildNodes())) {
            if (element instanceof Element) {
                if (element.getNodeName().equals("configuration")) {
                    if (io == null) {
                        io = new IOConfiguration((Element) element, transformer);
                    } else {
                        throw new IOException("Duplicate <configuration> sections.");
                    }
                } else {
                    PeriodConfiguration period = new PeriodConfiguration((Element) element, transformer);
                    if (periods.containsKey(period.name)) {
                        throw new IOException("Duplicate period sections.");
                    } else {
                        periods.put(period.name, period);
                    }
                }
            }
        }
        this.periods = Collections.unmodifiableMap(periods);
        this.io = io;
    }
}
