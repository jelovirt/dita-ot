/*
 * This file is part of the DITA Open Toolkit project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.dita.dost.reader;

import org.dita.dost.TestUtils;
import org.dita.dost.util.Job;
import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import static javax.xml.XMLConstants.NULL_NS_URI;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.dita.dost.util.EmptyAttributes.EMPTY_ATTRIBUTES;

public class MergeMapParserTest {

    final File resourceDir = TestUtils.getResourceDir(MergeMapParserTest.class);
    private final File srcDir = new File(resourceDir, "src");
    private final File expDir = new File(resourceDir, "exp");

    @Test
    public void testReadStringString() throws SAXException, IOException, TransformerConfigurationException {
        final MergeMapParser parser = new MergeMapParser();
        parser.setLogger(new TestUtils.TestLogger());
        parser.setJob(new Job(srcDir));
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final ContentHandler s = getSerializer(output);
        s.startDocument();
        s.startElement(NULL_NS_URI, "wrapper", "wrapper", EMPTY_ATTRIBUTES);
        parser.setContentHandler(s);
        parser.read(new File(srcDir, "test.ditamap").getAbsoluteFile(), srcDir.getAbsoluteFile());
        s.endElement(NULL_NS_URI, "wrapper", "wrapper");
        s.endDocument();
        assertXMLEqual(new InputSource(new File(expDir, "merged.xml").toURI().toString()),
                new InputSource(new ByteArrayInputStream(output.toByteArray())));
    }

    @Test
    public void testReadSpace() throws SAXException, IOException, TransformerConfigurationException {
        final MergeMapParser parser = new MergeMapParser();
        parser.setLogger(new TestUtils.TestLogger());
        parser.setJob(new Job(srcDir));
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final ContentHandler s = getSerializer(output);
        s.startDocument();
        s.startElement(NULL_NS_URI, "wrapper", "wrapper", EMPTY_ATTRIBUTES);
        parser.setContentHandler(s);
        parser.read(new File(srcDir, "space in map name.ditamap").getAbsoluteFile(), srcDir.getAbsoluteFile());
        s.endElement(NULL_NS_URI, "wrapper", "wrapper");
        s.endDocument();
        assertXMLEqual(new InputSource(new File(expDir, "merged.xml").toURI().toString()),
                new InputSource(new ByteArrayInputStream(output.toByteArray())));
    }

    private ContentHandler getSerializer(final OutputStream out) throws TransformerConfigurationException {
        final TransformerFactory tf = TransformerFactory.newInstance();
        if (!tf.getFeature(SAXTransformerFactory.FEATURE)) {
            throw new RuntimeException("SAX transformation factory not supported");
        }
        final SAXTransformerFactory stf = (SAXTransformerFactory) tf;
        final TransformerHandler s = stf.newTransformerHandler();
        s.setResult(new StreamResult(out));
        return s;
    }

}
