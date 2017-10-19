package com.github.jaguarrobotics.jaglibs.util;

import java.util.Iterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlNodeIterable implements Iterable<Node>, Iterator<Node> {
    private NodeList list;
    private int      i;

    @Override
    public Iterator<Node> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return i + 1 < list.getLength();
    }

    @Override
    public Node next() {
        return list.item(++i);
    }

    public XmlNodeIterable(NodeList list) {
        this.list = list;
        i = -1;
    }
}
