/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class ImmutableCharacterData extends ImmutableNode implements CharacterData {

    final CharacterData text;

    public ImmutableCharacterData(CharacterData text) {
        super(text);
        this.text = text;
    }

    @Override
    public String getData() throws DOMException {
        return text.getData();
    }

    @Override
    public void setData(String data) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLength() {
        return text.getLength();
    }

    @Override
    public String substringData(int offset, int count) throws DOMException {
        return text.substringData(offset, count);
    }

    @Override
    public void appendData(String arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertData(int offset, String arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteData(int offset, int count) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceData(int offset, int count, String arg) throws DOMException {
        throw new UnsupportedOperationException();
    }
}
