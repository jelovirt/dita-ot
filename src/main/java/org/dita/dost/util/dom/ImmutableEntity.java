/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.Entity;

public class ImmutableEntity extends ImmutableNode implements Entity {
    private final Entity entity;

    public ImmutableEntity(Entity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public String getPublicId() {
        return entity.getPublicId();
    }

    @Override
    public String getSystemId() {
        return entity.getSystemId();
    }

    @Override
    public String getNotationName() {
        return getNotationName();
    }

    @Override
    public String getInputEncoding() {
        return entity.getInputEncoding();
    }

    @Override
    public String getXmlEncoding() {
        return entity.getXmlEncoding();
    }

    @Override
    public String getXmlVersion() {
        return entity.getXmlVersion();
    }
}
