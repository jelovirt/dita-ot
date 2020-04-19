/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2020 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.ProcessingInstruction;

public class ImmutableProcessingInstruction extends ImmutableNode implements ProcessingInstruction {

    private final ProcessingInstruction pi;

    public ImmutableProcessingInstruction(ProcessingInstruction pi) {
        super(pi);
        this.pi = pi;
    }

    @Override
    public String getTarget() {
        return pi.getTarget();
    }

    @Override
    public String getData() {
        return pi.getData();
    }

    @Override
    public void setData(String data) throws DOMException {
        throw new UnsupportedOperationException();
    }
}
