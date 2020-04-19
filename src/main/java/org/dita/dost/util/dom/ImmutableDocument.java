/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.*;

public class ImmutableDocument implements Document {

    final Document doc;

    public ImmutableDocument(Document doc){
        this.doc = doc;
    }

    @Override
    public DocumentType getDoctype() {
        return doc.getDoctype();
    }

    @Override
    public DOMImplementation getImplementation() {
        return doc.getImplementation();
    }

    @Override
    public Element getDocumentElement() {
        return doc.getDocumentElement();
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Text createTextNode(String data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comment createComment(String data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CDATASection createCDATASection(String data) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr createAttribute(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityReference createEntityReference(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeList getElementsByTagName(String tagname) {
        return doc.getElementsByTagName(tagname);
    }

    @Override
    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return doc.getElementsByTagNameNS(namespaceURI, localName);
    }

    @Override
    public Element getElementById(String elementId) {
        return doc.getElementById(elementId);
    }

    @Override
    public String getInputEncoding() {
        return doc.getInputEncoding();
    }

    @Override
    public String getXmlEncoding() {
        return doc.getXmlEncoding();
    }

    @Override
    public boolean getXmlStandalone() {
        return doc.getXmlStandalone();
    }

    @Override
    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getXmlVersion() {
        return doc.getXmlVersion();
    }

    @Override
    public void setXmlVersion(String xmlVersion) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getStrictErrorChecking() {
        return doc.getStrictErrorChecking();
    }

    @Override
    public void setStrictErrorChecking(boolean strictErrorChecking) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDocumentURI() {
        return doc.getDocumentURI();
    }

    @Override
    public void setDocumentURI(String documentURI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node adoptNode(Node source) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DOMConfiguration getDomConfig() {
        return doc.getDomConfig();
    }

    @Override
    public void normalizeDocument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
        throw new UnsupportedOperationException();
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
        return doc.getChildNodes();
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
