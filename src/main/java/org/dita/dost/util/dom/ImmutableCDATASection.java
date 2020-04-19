/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.CDATASection;

public class ImmutableCDATASection extends ImmutableText implements CDATASection {
    public ImmutableCDATASection(CDATASection src) {
        super(src);
    }
}
