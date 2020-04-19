/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.*;

public class ImmutableDocument extends ImmutableNode implements Document {

    final Document doc;

    public ImmutableDocument(Document doc) {
        super(doc);
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

}
