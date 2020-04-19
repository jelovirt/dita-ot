/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

public class ImmutableNodeList implements NodeList {

    private final List<ImmutableNode> nodes;

    public ImmutableNodeList(List<ImmutableNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Node item(int index) {
        return nodes.get(index);
    }

    @Override
    public int getLength() {
        return nodes.size();
    }
}
