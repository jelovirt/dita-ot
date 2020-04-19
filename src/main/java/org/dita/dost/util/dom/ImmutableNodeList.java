/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;

public class ImmutableNodeList implements NodeList {

    private final NodeList srcList;
    private final List<Node> nodes;

    public ImmutableNodeList(NodeList srcList) {
        this.srcList = srcList;
        this.nodes = new ArrayList<>(srcList.getLength());
    }

    @Override
    public synchronized Node item(int index) {
        Node node = nodes.get(index);
        if (node == null) {
            final Node src = srcList.item(index);
            switch (src.getNodeType()) {
                case Node.ELEMENT_NODE: node = new ImmutableElement((Element) src); break;
                case Node.ATTRIBUTE_NODE: node = new ImmutableAttr((Attr) src); break;
                case Node.TEXT_NODE: node = new ImmutableText((Text) src); break;
                case Node.CDATA_SECTION_NODE: node = new ImmutableCDATASection((CDATASection) src); break;
                case Node.ENTITY_REFERENCE_NODE: node = new ImmutableEntityReference(src); break;
                case Node.ENTITY_NODE: node = new ImmutableEntity((Entity) src); break;
                case Node.PROCESSING_INSTRUCTION_NODE: node = new ImmutableProcessingInstruction((ProcessingInstruction) src); break;
                case Node.COMMENT_NODE: node = new ImmutableComment((Comment) src); break;
                case Node.DOCUMENT_NODE: node = new ImmutableDocument((Document) src); break;
//                case Node.DOCUMENT_TYPE_NODE: node = new ImmutableDocumentType((DocumentType) src); break;
//                case Node.DOCUMENT_FRAGMENT_NODE: node = new ImmutableDocumentFragment((DocumentFragment) src); break;
//                case Node.NOTATION_NODE: node = new ImmutableNotation((Notation) src); break;
                default:
                    throw new IllegalArgumentException();
            }
            nodes.set(index, node);
        }
        return node;
    }

    @Override
    public int getLength() {
        return nodes.size();
    }
}
