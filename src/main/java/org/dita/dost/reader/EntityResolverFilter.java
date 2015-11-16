package org.dita.dost.reader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

public interface EntityResolverFilter extends EntityResolver {

    void setEntityResolver(final EntityResolver entityResolver);
}
