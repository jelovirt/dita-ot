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

    public static Node wrap(Node src) {
        switch (src.getNodeType()) {
            case Node.ELEMENT_NODE: return new ImmutableElement((Element) src);
            case Node.ATTRIBUTE_NODE: return new ImmutableAttr((Attr) src);
            case Node.TEXT_NODE: return new ImmutableText((Text) src);
            case Node.CDATA_SECTION_NODE: return new ImmutableCDATASection((CDATASection) src);
            case Node.ENTITY_REFERENCE_NODE: return new ImmutableEntityReference(src);
            case Node.ENTITY_NODE: return new ImmutableEntity((Entity) src);
            case Node.PROCESSING_INSTRUCTION_NODE: return new ImmutableProcessingInstruction((ProcessingInstruction) src);
            case Node.COMMENT_NODE: return new ImmutableComment((Comment) src);
            case Node.DOCUMENT_NODE: return new ImmutableDocument((Document) src);
//                case Node.DOCUMENT_TYPE_NODE: return new ImmutableDocumentType((DocumentType) src);
//                case Node.DOCUMENT_FRAGMENT_NODE: return new ImmutableDocumentFragment((DocumentFragment) src);
//                case Node.NOTATION_NODE: return new ImmutableNotation((Notation) src);
            default:
                throw new IllegalArgumentException();
        }
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
        return wrap(doc.getParentNode());
    }

    @Override
    public NodeList getChildNodes() {
        return new ImmutableNodeList(doc.getChildNodes());
    }

    @Override
    public Node getFirstChild() {
        return wrap(doc.getFirstChild());
    }

    @Override
    public Node getLastChild() {
        return wrap(doc.getLastChild());
    }

    @Override
    public Node getPreviousSibling() {
        return wrap(doc.getPreviousSibling());
    }

    @Override
    public Node getNextSibling() {
        return wrap(doc.getNextSibling());
    }

    @Override
    public NamedNodeMap getAttributes() {
        return new ImmutableNamedNodeMap(doc.getAttributes());
    }

    @Override
    public Document getOwnerDocument() {
        return new ImmutableDocument(doc.getOwnerDocument());
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
