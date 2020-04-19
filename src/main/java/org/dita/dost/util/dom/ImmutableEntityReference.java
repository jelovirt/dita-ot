/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;

public class ImmutableEntityReference extends ImmutableNode implements EntityReference {
    public ImmutableEntityReference(Node doc) {
        super(doc);
    }
}
