/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class ImmutableText extends ImmutableCharacterData implements Text {

    final Text text;

    public ImmutableText(Text text) {
        super(text);
        this.text = text;
    }

    @Override
    public Text splitText(int offset) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isElementContentWhitespace() {
        return text.isElementContentWhitespace();
    }

    @Override
    public String getWholeText() {
        return text.getWholeText();
    }

    @Override
    public Text replaceWholeText(String content) throws DOMException {
        throw new UnsupportedOperationException();
    }
}
