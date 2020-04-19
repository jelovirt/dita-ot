/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.*;

public class ImmutableAttr extends ImmutableNode implements Attr {

    final Attr attr;

    public ImmutableAttr(Attr attr) {
        super(attr);
        this.attr = attr;
    }

    @Override
    public String getName() {
        return attr.getName();
    }

    @Override
    public boolean getSpecified() {
        return attr.getSpecified();
    }

    @Override
    public String getValue() {
        return attr.getValue();
    }

    @Override
    public void setValue(String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Element getOwnerElement() {
        return attr.getOwnerElement();
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return attr.getSchemaTypeInfo();
    }

    @Override
    public boolean isId() {
        return attr.isId();
    }
}
