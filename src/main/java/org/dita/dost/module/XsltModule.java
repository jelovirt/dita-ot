/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2013 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */
package org.dita.dost.module;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.dita.dost.exception.DITAOTException;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.AbstractPipelineOutput;
import org.dita.dost.util.CatalogUtils;
import org.dita.dost.util.Configuration;
import org.dita.dost.util.Job;
import org.dita.dost.util.XMLUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.dita.dost.util.FileUtils.replaceExtension;
import static org.dita.dost.util.XMLUtils.withLogger;

/**
 * XSLT processing module.
 *
 * <p>The module matches Ant's XSLT task with the following exceptions:</p>
 * <ul>
 *   <li>If source and destination directories are same, transformation results are saved to a temporary file
 *   and the original source file is replaced after a successful transformation.</li>
 *   <li>If no {@code extension} attribute is set, the target file extension is the same as the source file extension.</li>
 * </ul>
 *
 */
public final class XsltModule extends AbstractPipelineModuleImpl {

    private Templates templates;
    private final Map<String, String> params = new HashMap<>();
    private File style;
    private File in;
    private File out;
    private File destDir;
    private File baseDir;
    private Collection<File> includes;
    private String filenameparameter;
    private String filedirparameter;
    private boolean reloadstylesheet;
    private EntityResolver entityResolver;
    private URIResolver uriResolver;
    private FileNameMapper mapper;
    private String extension;

    private void init() {
        if (entityResolver == null || uriResolver == null) {
            final CatalogResolver catalogResolver = CatalogUtils.getCatalogResolver();
            entityResolver = catalogResolver;
            uriResolver = catalogResolver;
        }

        if (fileInfoFilter != null) {
            final Collection<Job.FileInfo> res = job.getFileInfo(fileInfoFilter);
            includes = new ArrayList<>(res.size());
            for (final Job.FileInfo f : res) {
                includes.add(f.file);
            }
            baseDir = job.tempDir;
        }
    }

    public AbstractPipelineOutput execute(AbstractPipelineInput input) throws DITAOTException {
        init();
        if (includes.isEmpty()) {
            return null;
        }

        logger.info("Transforming into " + destDir.getAbsolutePath());
        final TransformerFactory tf = TransformerFactory.newInstance();
        configureExtensions(tf);
        tf.setURIResolver(uriResolver);
        try {
            templates = tf.newTemplates(new StreamSource(style));
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Failed to compile stylesheet '" + style.getAbsolutePath() + "': " + e.getMessage(), e);
        }
        XMLReader parser;
        try {
            parser = XMLUtils.getXMLReader();
        } catch (final SAXException e) {
            throw new RuntimeException("Failed to create XML reader: " + e.getMessage(), e);
        }
        parser.setEntityResolver(entityResolver);

        Transformer t = null;
        for (final File include: includes) {
            if (reloadstylesheet || t == null) {
                logger.info("Loading stylesheet " + style.getAbsolutePath());
                try {
                    t = withLogger(templates.newTransformer(), logger);
                    if (Configuration.DEBUG) {
                        t.setURIResolver(new XMLUtils.DebugURIResolver(uriResolver));
                    }
                } catch (final TransformerConfigurationException e) {
                    throw new DITAOTException("Failed to create Transformer: " + e.getMessage(), e);
                }
            }
            final File in = new File(baseDir, include.getPath());
            File out = new File(destDir, include.getPath());
            if (mapper != null) {
                final String[] outs = mapper.mapFileName(include.getPath());
                if (outs == null) {
                    continue;
                }
                if (outs.length > 1) {
                    throw new RuntimeException("XSLT module only support one to one output mapping");
                }
                out = new File(destDir, outs[0]);
            } else if (extension != null) {
                out = new File(replaceExtension(out.getAbsolutePath(), extension));
            }
            final boolean same = in.getAbsolutePath().equals(out.getAbsolutePath());
            final File tmp = same ? new File(out.getAbsolutePath() + ".tmp" + Long.toString(System.currentTimeMillis())) : out;
            for (Map.Entry<String, String> e: params.entrySet()) {
                logger.debug("Set parameter " + e.getKey() + " to '" + e.getValue() + "'");
                t.setParameter(e.getKey(), e.getValue());
            }
            if (filenameparameter != null) {
                logger.debug("Set parameter " + filenameparameter + " to '" + include.getName() + "'");
                t.setParameter(filenameparameter, include.getName());
            }
            if (filedirparameter != null) {
                final String v = include.getParent() != null ? include.getParent() : ".";
                logger.debug("Set parameter " + filedirparameter + " to '" + v + "'");
                t.setParameter(filedirparameter, v);
            }
            if (same) {
                logger.info("Processing " + in.getAbsolutePath());
                logger.debug("Processing " + in.getAbsolutePath() + " to " + tmp.getAbsolutePath());
            } else {
                logger.info("Processing " + in.getAbsolutePath() + " to " + tmp.getAbsolutePath());
            }
            final Source source = new SAXSource(parser, new InputSource(in.toURI().toString()));
            try {
                if (!tmp.getParentFile().exists() && !tmp.getParentFile().mkdirs()) {
                    throw new IOException("Failed to create directory " + tmp.getParent());
                }
                t.transform(source, new StreamResult(tmp));
                if (same) {
                    logger.debug("Moving " + tmp.getAbsolutePath() + " to " + out.getAbsolutePath());
                    if (!out.delete()) {
                        throw new IOException("Failed to to delete input file " + out.getAbsolutePath());
                    }
                    if (!tmp.renameTo(out)) {
                        throw new IOException("Failed to to replace input file " + out.getAbsolutePath());
                    }
                }
            } catch (final Exception e) {
                logger.error("Failed to transform document: " + e.getMessage(), e);
                logger.debug("Remove " + tmp.getAbsolutePath());
                FileUtils.delete(tmp);
            }
        }
        return null;
    }

    public void setStyle(final File style) {
        this.style = style;
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
        this.entityResolver = xmlcatalog;
        this.uriResolver = xmlcatalog;
    }

    public void setMapper(final FileNameMapper mapper) {
        this.mapper = mapper;
    }

    public void setExtension(final String extension) {
        this.extension = extension.startsWith(".") ? extension : ("." + extension);
    }

    private void configureExtensions (TransformerFactory tf) {
        if (tf.getClass().isAssignableFrom(net.sf.saxon.TransformerFactoryImpl.class)) {
            configureSaxonExtensions((net.sf.saxon.TransformerFactoryImpl) tf);
        }
    }

    /**
     * Registers Saxon full integrated function definitions.
     * The intgrated function should be an instance of net.sf.saxon.lib.ExtensionFunctionDefinition abstract class.
     * @see <a href="https://www.saxonica.com/html/documentation/extensibility/integratedfunctions/ext-full-J.html">Saxon
     *      Java extension functions: full interface</a>
     */
    private void configureSaxonExtensions(net.sf.saxon.TransformerFactoryImpl tfi) {
        final net.sf.saxon.Configuration conf = tfi.getConfiguration();
        for (ExtensionFunctionDefinition def : ServiceLoader.load(ExtensionFunctionDefinition.class)) {
            try {
                conf.registerExtensionFunction(def.getClass().newInstance());
            } catch (InstantiationException e) {
                throw new RuntimeException("Failed to register " + def.getFunctionQName().getDisplayName()
                        + ". Cannot create instance of " + def.getClass().getName() + ": " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
