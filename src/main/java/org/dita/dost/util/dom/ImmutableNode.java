/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.*;

public class ImmutableNode implements Node {

    final Node doc;

    public ImmutableNode(Node doc) {
        this.doc = doc;
    }

    @Override
    public String getNodeName() {
        return doc.getNodeName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public short getNodeType() {
        return doc.getNodeType();
    }

    @Override
    public Node getParentNode() {
        return doc.getParentNode();
    }

    @Override
    public NodeList getChildNodes() {
        return new ImmutableNodeList(doc.getChildNodes());
    }

    @Override
    public Node getFirstChild() {
        return doc.getFirstChild();
    }

    @Override
    public Node getLastChild() {
        return doc.getLastChild();
    }

    @Override
    public Node getPreviousSibling() {
        return doc.getPreviousSibling();
    }

    @Override
    public Node getNextSibling() {
        return doc.getNextSibling();
    }

    @Override
    public NamedNodeMap getAttributes() {
        return doc.getAttributes();
    }

    @Override
    public Document getOwnerDocument() {
        return doc.getOwnerDocument();
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasChildNodes() {
        return doc.hasChildNodes();
    }

    @Override
    public Node cloneNode(boolean deep) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void normalize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return doc.isSupported(feature, version);
    }

    @Override
    public String getNamespaceURI() {
        return doc.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return doc.getPrefix();
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalName() {
        return doc.getLocalName();
    }

    @Override
    public boolean hasAttributes() {
        return doc.hasAttributes();
    }

    @Override
    public String getBaseURI() {
        return doc.getBaseURI();
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        return doc.compareDocumentPosition(other);
    }

    @Override
    public String getTextContent() throws DOMException {
        return null;
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSameNode(Node other) {
        return doc.isSameNode(other);
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return doc.lookupPrefix(namespaceURI);
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return doc.isDefaultNamespace(namespaceURI);
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return doc.lookupNamespaceURI(prefix);
    }

    @Override
    public boolean isEqualNode(Node arg) {
        return doc.isEqualNode(arg);
    }

    @Override
    public Object getFeature(String feature, String version) {
        return null;
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getUserData(String key) {
        return null;
    }
}
