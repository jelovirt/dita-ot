/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ImmutableNamedNodeMap implements NamedNodeMap {
    private final NamedNodeMap map;

    public ImmutableNamedNodeMap(NamedNodeMap map) {
        this.map = map;
    }

    @Override
    public Node getNamedItem(String name) {
        return ImmutableNode.wrap(map.getNamedItem(name));
    }

    @Override
    public Node setNamedItem(Node arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node removeNamedItem(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node item(int index) {
        return ImmutableNode.wrap(map.item(index));
    }

    @Override
    public int getLength() {
        return map.getLength();
    }

    @Override
    public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return ImmutableNode.wrap(map.getNamedItemNS(namespaceURI, localName));
    }

    @Override
    public Node setNamedItemNS(Node arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }
}
