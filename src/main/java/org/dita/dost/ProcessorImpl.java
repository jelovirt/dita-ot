/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2017 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.LoggerListener;
import org.dita.dost.util.Configuration.Mode;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * DITA-OT processer. Not thread-safe, but can be reused.
 */
public final class ProcessorImpl implements Processor {

    private final String transtype;

    private File ditaDir;
    private Map<String, String> args = new HashMap<>();
    private Logger logger;
    private boolean cleanOnFailure = true;
    private boolean createDebugLog = true;

    public ProcessorImpl() {
        this.transtype = null;
    }

    public ProcessorImpl(String transtype) {
        this.transtype = transtype;
    }

    @Override
    public String getTranstype() {
        return transtype;
    }

    @Override
    public Processor setDitaDir(final File ditaDir) {
        this.ditaDir = ditaDir;
        return this;
    }

    @Override
    public Processor setInput(final File input) {
        if (!input.isAbsolute()) {
            throw new IllegalArgumentException("Input file path must be absolute: " + input);
        }
        if (!input.isFile()) {
            throw new IllegalArgumentException("Input file is not a file: " + input);
        }
        setInput(input.toURI());
        return this;
    }

    @Override
    public Processor setInput(final URI input) {
        if (!input.isAbsolute()) {
            throw new IllegalArgumentException("Input file URI must be absolute: " + input);
        }
        args.put("args.input", input.toString());
        return this;
    }

    @Override
    public Processor setOutputDir(final File output) {
        if (!output.isAbsolute()) {
            throw new IllegalArgumentException("Output directory path must be absolute: " + output);
        }
        if (output.exists() && !output.isDirectory()) {
            throw new IllegalArgumentException("Output directory exists and is not a directory: " + output);
        }
        args.put("output.dir", output.getAbsolutePath());
        return this;
    }

    @Override
    public Processor setOutputDir(final URI output) {
        if (!output.isAbsolute()) {
            throw new IllegalArgumentException("Output directory URI must be absolute: " + output);
        }
        if (!output.getScheme().equals("file")) {
            throw new IllegalArgumentException("Only file scheme allowed as output directory URI: " + output);
        }
        args.put("output.dir", output.toString());
        return this;
    }

    @Override
    public Processor setProperty(final String name, final String value) {
        args.put(name, value);
        return this;
    }

    @Override
    public Processor setProperties(final Map<String, String> value) {
        args.putAll(value);
        return this;
    }

    @Override
    public Processor setLogger(final Logger logger) {
        this.logger = logger;
        return this;
    }

    @Override
    public Processor cleanOnFailure(final boolean cleanOnFailure) {
        this.cleanOnFailure = cleanOnFailure;
        return this;
    }

    @Override
    public Processor createDebugLog(final boolean createDebugLog) {
        this.createDebugLog = createDebugLog;
        return this;
    }

    @Override
    public Processor setMode(final Mode mode) {
        args.put("processing-mode", mode.toString().toLowerCase());
        return this;
    }

    @Override
    public void run() throws DITAOTException {
        if (!args.containsKey("args.input")) {
            throw new IllegalStateException("Input file not set");
        }
        if (!args.containsKey("output.dir")) {
            throw new IllegalStateException("Output directory not set");
        }
        final File tempDir = getTempDir();
        args.put("dita.temp.dir", tempDir.getAbsolutePath());
        this.args.put("dita.dir", ditaDir.getAbsolutePath());
        this.args.put("transtype", transtype);

        boolean cleanTemp = true;

        final ch.qos.logback.classic.Logger debugLogger = createDebugLog ? openDebugLogger(tempDir) : null;

        try {
            final File buildFile = new File(ditaDir, "build.xml");
            final Project project = new Project();
            project.setCoreLoader(this.getClass().getClassLoader());

            if (logger != null) {
                project.addBuildListener(new LoggerListener(logger));
            }
            if (debugLogger != null) {
                project.addBuildListener(new LoggerListener(debugLogger));
            }

            project.fireBuildStarted();
            project.init();
            project.setBaseDir(ditaDir);
            project.setKeepGoingMode(false);
            for (final Map.Entry<String, String> arg : args.entrySet()) {
                project.setUserProperty(arg.getKey(), arg.getValue());
            }
            ProjectHelper.configureProject(project, buildFile);
            final Vector<String> targets = new Vector<>();
//            targets.addElement(project.getDefaultTarget());
            targets.addElement("dita2" + args.get("transtype"));
            project.executeTargets(targets);
        } catch (final BuildException e) {
            cleanTemp = cleanOnFailure;
            throw new DITAOTException(e);
        } finally {
            if (debugLogger != null) {
                closeDebugLogger(debugLogger);
            }
            if (cleanTemp) {
                try {
                    FileUtils.forceDelete(tempDir);
                } catch (final IOException ex) {
                    logger.error("Failed to delete temporary directory " + tempDir);
                }
            }
        }
    }

    private ch.qos.logback.classic.Logger openDebugLogger(File tempDir) {
        final LoggerContext loggerContext = new LoggerContext();

        final FileAppender fileAppender = new FileAppender();
        fileAppender.setFile(new File(tempDir.getAbsolutePath() + ".log").getAbsolutePath());
        fileAppender.setContext(loggerContext);
        fileAppender.setAppend(false);
        fileAppender.setImmediateFlush(true);

        final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%-4relative [%-5level] %msg%n");
        encoder.start();

        fileAppender.setEncoder(encoder);
        fileAppender.start();

        final ch.qos.logback.classic.Logger debugLogger = loggerContext.getLogger(getClass().getCanonicalName() + "_"  + System.currentTimeMillis());
        debugLogger.addAppender(fileAppender);
        debugLogger.setLevel(Level.DEBUG);
        return debugLogger;
    }

    private void closeDebugLogger(ch.qos.logback.classic.Logger debugLogger) {
        debugLogger.detachAndStopAllAppenders();
    }

    private File getTempDir() {
        final File baseTempDir = new File(args.get("base.temp.dir"));
        File tempDir;
        for (int i = 0; i < 10; i++) {
            tempDir = new File(baseTempDir, Long.toString(System.currentTimeMillis()));
            if (!tempDir.exists()) {
                try {
                    FileUtils.forceMkdir(tempDir);
                    return tempDir;
                } catch (IOException e) {
                    // Ignore
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        throw new RuntimeException("Unable to create temporary directory");
    }

}
