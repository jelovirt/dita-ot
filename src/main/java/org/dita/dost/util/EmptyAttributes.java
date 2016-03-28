package org.dita.dost.util;

import org.xml.sax.Attributes;

/**
 * Empty and immutable Attributes implementation.
 */
public class EmptyAttributes implements Attributes {

    public static final Attributes EMPTY_ATTRIBUTES = new EmptyAttributes();

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public String getURI(final int index) {
        return null;
    }

    @Override
    public String getLocalName(final int index) {
        return null;
    }

    @Override
    public String getQName(final int index) {
        return null;
    }

    @Override
    public String getType(final int index) {
        return null;
    }

    @Override
    public String getValue(final int index) {
        return null;
    }

    @Override
    public int getIndex(final String uri, final String localName) {
        return -1;
    }

    @Override
    public int getIndex(final String qName) {
        return -1;
    }

    @Override
    public String getType(final String uri, final String localName) {
        return null;
    }

    @Override
    public String getType(final String qName) {
        return null;
    }

    @Override
    public String getValue(final String uri, final String localName) {
        return null;
    }

    @Override
    public String getValue(final String qName) {
        return null;
    }
}
