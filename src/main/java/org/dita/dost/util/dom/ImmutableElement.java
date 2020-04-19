/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.*;

public class ImmutableElement extends ImmutableNode implements Element {

    final Element elem;

    public ImmutableElement(Element elem) {
        super(elem);
        this.elem = elem;
    }

    @Override
    public String getTagName() {
        return elem.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        return elem.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr getAttributeNode(String name) {
        return new ImmutableAttr(elem.getAttributeNode(name));
    }

    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeList getElementsByTagName(String name) {
        return new ImmutableNodeList(elem.getElementsByTagName(name));
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        return elem.getAttributeNS(namespaceURI, localName);
    }

    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        return elem.getAttributeNodeNS(namespaceURI, localName);
    }

    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        return new ImmutableNodeList(elem.getElementsByTagNameNS(namespaceURI, localName));
    }

    @Override
    public boolean hasAttribute(String name) {
        return false;
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        return false;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return elem.getSchemaTypeInfo();
    }

    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }
}
