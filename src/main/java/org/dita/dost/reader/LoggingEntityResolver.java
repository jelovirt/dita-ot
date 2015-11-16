package org.dita.dost.reader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.Random;

public class LoggingEntityResolver implements EntityResolverFilter {

    private EntityResolver parent;

    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        try {
            final InputSource res = parent.resolveEntity(publicId, systemId);;
            final URI uri = new URI(systemId);
            if (res == null && "random".equals(uri.getScheme())) {
                System.err.println("Resolving '" + publicId + "' / " + systemId);
                final InputSource input = new InputSource();
                input.setCharacterStream(new StringReader("<!DOCTYPE topic PUBLIC '-//OASIS//DTD DITA Topic//EN' 'topic.dtd'>\n" +
                        "<topic id='random'>\n" +
                        "<title>Random</title>\n" +
                        "<body>\n" +
                        "<p>" + new Random().nextInt()  + "</p>\n" +
                        "</body>\n" +
                        "</topic>"));
                input.setPublicId(publicId);
                input.setSystemId(systemId);
                return input;
            } else {
                return res;
            }
        } catch (final URISyntaxException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void setEntityResolver(final EntityResolver entityResolver) {
        parent = entityResolver;
    }
}
