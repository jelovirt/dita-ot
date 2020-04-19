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
            node = ImmutableNode.wrap(srcList.item(index));
            nodes.set(index, node);
        }
        return node;
    }

    @Override
    public int getLength() {
        return nodes.size();
    }
}
