package org.dita.dost.module;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.util.FileUtils;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.AbstractPipelineOutput;
import org.dita.dost.util.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * XSLT processing module.
 * 
 * <p>The module matches Ant's XSLT task with the following exceptions:</p>
 * <ul>
 *   <li>If source and destination directories are same, transformation results are saved to a temporary file
 *   and the original source file is replaced after a successful transformation.</li>
 *   <li>If no {@code extension} attribute is set, the target file extension is the same as the source file extension.</li> 
 *   <li>Mappers are not supported.</li>
 * </ul>
 *  
 */
public final class XsltModule implements AbstractPipelineModule {

    private Templates templates;
    private Map<String, String> params = new HashMap<String, String>();
    private DITAOTLogger logger;
    private File style;
    private File in;
    private File out;
    private File destDir;
    private File baseDir;
    private Collection<File> includes;
    private String filenameparameter;
    private String filedirparameter;
    private boolean reloadstylesheet;
    private XMLCatalog xmlcatalog;
    
    public AbstractPipelineOutput execute(AbstractPipelineInput input) throws DITAOTException {
        final TransformerFactory tf = TransformerFactory.newInstance();
        tf.setURIResolver(xmlcatalog);
        try {
            templates = tf.newTemplates(new StreamSource(style));
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Failed to compile stylesheet '" + style.getAbsolutePath() + "': " + e.getMessage(), e);
        }
        XMLReader parser;
		try {
			parser = StringUtils.getXMLReader();
		} catch (final SAXException e) {
			throw new RuntimeException("Failed to create XML reader: " + e.getMessage(), e);
		}
        parser.setEntityResolver(xmlcatalog);
        
    	Transformer t = null;
        for (final File include: includes) {
        	if (reloadstylesheet || t == null) {
	            try {
	                t = templates.newTransformer();
	            } catch (final TransformerConfigurationException e) {
	                throw new DITAOTException("Failed to create Transformer: " + e.getMessage(), e);
	            }
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
            if (filedirparameter != null) {
            	final String v = include.getParent() != null ? include.getParent() : ".";
                logger.logDebug("Set parameter " + filedirparameter + " to '" + v + "'");
                t.setParameter(filedirparameter, v);
            }
            if (same) {
	            logger.logInfo("Processing " + in.getAbsolutePath());
	            logger.logDebug("Processing " + in.getAbsolutePath() + " to " + tmp.getAbsolutePath());
            } else {
            	logger.logInfo("Processing " + in.getAbsolutePath() + " to " + tmp.getAbsolutePath());
            }
            final Source source = new SAXSource(parser, new InputSource(in.toURI().toString()));
            try {
                t.transform(source, new StreamResult(tmp));
                if (same) {
                    logger.logDebug("Moving " + tmp.getAbsolutePath() + " to " + out.getAbsolutePath());
                    if (!out.delete()) {
                        throw new IOException("Failed to to delete input file " + out.getAbsolutePath());
                    }
                    if (!tmp.renameTo(out)) {
                        throw new IOException("Failed to to replace input file " + out.getAbsolutePath());
                    }
                }
            } catch (final Exception e) {
                logger.logError("Failed to transform document: " + e.getMessage(), e);
                logger.logDebug("Remove " + tmp.getAbsolutePath());
                FileUtils.delete(tmp);
            } 
        }
        return null;
    }
    
    public void setStyle(final File style) {
    	this.style = style;
    }

    public void setLogger(DITAOTLogger logger) {
        this.logger = logger;
    }

    public void setParam(final String key, final String value) {
        params.put(key, value);
    }

    public void setIncludes(final Collection<File> includes) {
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
    
    public void setFiledirParam(final String filedirparameter) {
        this.filedirparameter = filedirparameter;
    }
    
    public void setReloadstylesheet(final boolean reloadstylesheet) {
    	this.reloadstylesheet = reloadstylesheet;
    }

	public void setSource(final File in) {
		this.in = in;
	}

	public void setResult(final File out) {
		this.out = out;
	}

	public void setXMLCatalog(final XMLCatalog xmlcatalog) {
		this.xmlcatalog = xmlcatalog;
	}
    
}
