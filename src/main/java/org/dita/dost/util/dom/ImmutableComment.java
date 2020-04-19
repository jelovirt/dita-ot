/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.Comment;
import org.w3c.dom.Text;

public class ImmutableComment extends ImmutableCharacterData implements Comment {

    public ImmutableComment(Text text) {
        super(text);
    }

}
