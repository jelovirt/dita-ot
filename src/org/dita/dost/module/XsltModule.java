package org.dita.dost.module;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.util.FileUtils;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.AbstractPipelineOutput;

/**
 * XSLT processing module.
 */
public final class XsltModule implements AbstractPipelineModule {

    private final Templates templates;
    private Map<String, String> params = new HashMap<String, String>();
    private DITAOTLogger logger;
    private File destDir;
    private File baseDir;
    private List<File> includes;
    private String filenameparameter;
    
    public XsltModule(final File style) {
        final TransformerFactory tf = TransformerFactory.newInstance();
        try {
            templates = tf.newTemplates(new StreamSource(style));
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Failed to compile stylesheet '" + style.getAbsolutePath() + "': " + e.getMessage(), e);
        }
    }
    
    public AbstractPipelineOutput execute(AbstractPipelineInput input) throws DITAOTException {
        for (final File include: includes) {
            Transformer t = null;
            try {
                t = templates.newTransformer();
            } catch (final TransformerConfigurationException e) {
                throw new DITAOTException("Failed to compile stylesheet: " + e.getMessage(), e);
            }
            final File in = new File(baseDir, include.getPath());
            final File out = new File(destDir, include.getPath());
            final boolean same = in.getAbsolutePath().equals(out.getAbsolutePath());
            final File tmp = same ? new File(out.getAbsolutePath() + ".tmp" + Long.toString(System.currentTimeMillis())) : out; 
            for (Map.Entry<String, String> e: params.entrySet()) {
                logger.logDebug("Set parameter " + e.getKey() + " to '" + e.getValue() + "'");
                t.setParameter(e.getKey(), e.getValue());
            }
            if (filenameparameter != null) {
                logger.logDebug("Set parameter " + filenameparameter + " to '" + include.getName() + "'");
                t.setParameter(filenameparameter, include.getName());
            }
            logger.logInfo("Processing " + in.getAbsolutePath() + " to " + tmp.getAbsolutePath());
            try {
                t.transform(new StreamSource(in), new StreamResult(tmp));
                if (same) {
                    logger.logDebug("Moving " + tmp.getAbsolutePath() + " to " + out.getAbsolutePath());
                }
            } catch (final TransformerException e) {
            //} catch (final Exception e) {
                logger.logError("Failed to transform document: " + e.getMessage(), e);
                logger.logDebug("Remove " + tmp.getAbsolutePath());
                FileUtils.delete(tmp);
            }            
        }
        return null;
    }

    public void setLogger(DITAOTLogger logger) {
        this.logger = logger;
    }

    public void setParam(final String key, final String value) {
        params.put(key, value);
    }

    public void setIncludes(final List<File> includes) {
        this.includes = includes;
    }

    public void setDestinationDir(final File destDir) {
        this.destDir = destDir;
    }

    public void setSorceDir(final File baseDir) {
        this.baseDir = baseDir;
    }

    public void setFilenameParam(final String filenameparameter) {
        this.filenameparameter = filenameparameter;
    }
    
}
