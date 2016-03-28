/*
 * This file is part of the DITA Open Toolkit project.
 * See the accompanying license.txt file for applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2004, 2005 All Rights Reserved.
 */
package org.dita.dost.module;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.MessageUtils;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.AbstractPipelineOutput;
import org.dita.dost.reader.MergeMapParser;
import org.dita.dost.util.CatalogUtils;
import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import static javax.xml.XMLConstants.NULL_NS_URI;
import static org.dita.dost.util.Constants.*;
import static org.dita.dost.util.EmptyAttributes.EMPTY_ATTRIBUTES;

/**
 * The module handles topic merge in issues as PDF.
 */
final class TopicMergeModule extends AbstractPipelineModuleImpl {

    /**
     * Default Constructor.
     */
    public TopicMergeModule() {
        super();
    }

    /**
     * Entry point of TopicMergeModule.
     *
     * @param input Input parameters and resources.
     * @return null
     * @throws DITAOTException exception
     */
    @Override
    public AbstractPipelineOutput execute(final AbstractPipelineInput input)
            throws DITAOTException {
        if (logger == null) {
            throw new IllegalStateException("Logger not set");
        }
        final File ditaInput = new File(input.getAttribute(ANT_INVOKER_PARAM_INPUTMAP));
        final File style = input.getAttribute(ANT_INVOKER_EXT_PARAM_STYLE) != null ? new File(input.getAttribute(ANT_INVOKER_EXT_PARAM_STYLE)) : null;
        final File out = new File(input.getAttribute(ANT_INVOKER_EXT_PARAM_OUTPUT));
        final MergeMapParser mapParser = new MergeMapParser();
        mapParser.setLogger(logger);
        mapParser.setJob(job);
        mapParser.setOutput(out.getAbsoluteFile());

        if (!ditaInput.exists()) {
            logger.error(MessageUtils.getInstance().getMessage("DOTJ025E").toString());
            return null;
        }

        ByteArrayOutputStream midBuffer = null;
        try {
            midBuffer = new ByteArrayOutputStream();
            final TransformerFactory tf = TransformerFactory.newInstance();
            if (!tf.getFeature(SAXTransformerFactory.FEATURE)) {
                throw new RuntimeException("SAX transformation factory not supported");
            }
            final SAXTransformerFactory stf = (SAXTransformerFactory) tf;
            final TransformerHandler s = stf.newTransformerHandler();
            s.setResult(new StreamResult(midBuffer));

            s.startDocument();
            s.startPrefixMapping(ATTRIBUTE_PREFIX_DITAARCHVERSION, "http://dita.oasis-open.org/architecture/2005/");
            s.startElement(NULL_NS_URI, "dita-merge", "dita-merge", EMPTY_ATTRIBUTES);

            mapParser.setContentHandler(s);
            mapParser.read(ditaInput, job.tempDir);

            s.endElement(NULL_NS_URI, "dita-merge", "dita-merge");
            s.endPrefixMapping(ATTRIBUTE_PREFIX_DITAARCHVERSION);
            s.endDocument();
        } catch (final TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (final SAXException e) {
            throw new DITAOTException("Failed to merge topics: " + e.getMessage(), e);
        } finally {
            if (midBuffer != null) {
                try {
                    midBuffer.close();
                } catch (final IOException e) {
                    logger.error("Failed to close output buffer: " + e.getMessage(), e);
                }
            }
        }

        logger.info("Writing " + out.toURI().toString());
        OutputStream output = null;
        try {
            final File outputDir = out.getParentFile();
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                logger.error("Failed to create directory " + outputDir.getAbsolutePath());
            }
            output = new BufferedOutputStream(new FileOutputStream(out));
            if (style != null) {
                final TransformerFactory factory = TransformerFactory.newInstance();
                factory.setURIResolver(CatalogUtils.getCatalogResolver());
                final Transformer transformer = factory.newTransformer(new StreamSource(style.toURI().toString()));
                transformer.transform(new StreamSource(new ByteArrayInputStream(midBuffer.toByteArray())),
                        new StreamResult(output));
            } else {
                output.write(midBuffer.toByteArray());
                output.flush();
            }
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new DITAOTException("Failed to process merged topics: " + e.getMessage(), e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (final Exception e) {
                logger.error("Failed to close output buffer: " + e.getMessage(), e);
            }
        }

        return null;
    }

}
