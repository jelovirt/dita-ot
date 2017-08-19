/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2017 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost;

import org.dita.dost.util.Configuration;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * DITA-OT processer factory. Not thread-safe, but can be reused.
 */
public final class ProcessorFactory {

    private final File ditaDir;
    private final Map<String, String> args = new HashMap<>();
    private final Map<String, Class<Processor>> processors;

    private ProcessorFactory(final File ditaDir) {
        this.ditaDir = ditaDir;

        final ServiceLoader<Processor> serviceLoader = ServiceLoader.load(Processor.class);
        processors = new HashMap<>();
        for (Processor processor : (Iterable<Processor>)() -> serviceLoader.iterator()) {
            processors.put(processor.getTranstype(), (Class<Processor>) processor.getClass());
        }
    }

    /**
     * Obtain a new instance of a ProcessorFactory.
     *
     * @param ditaDir absolute directory to DITA-OT installation
     * @return new ProcessorFactory instance
     */
    public static ProcessorFactory newInstance(final File ditaDir) {
        if (!ditaDir.isAbsolute()) {
            throw new IllegalArgumentException("DITA-OT directory must be absolute");
        }
        return new ProcessorFactory(ditaDir);
    }

    /**
     * Set base directory for temporary directories.
     *
     * @param tmp absolute directory for temporary directories
     */
    public void setBaseTempDir(final File tmp) {
        if (!tmp.isAbsolute()) {
            throw new IllegalArgumentException("Temporary directory must be absolute");
        }
        args.put("base.temp.dir", tmp.getAbsolutePath());
    }

    /**
     * Create new Processor to run DITA-OT
     *
     * @param transtype transtype for the processor
     * @return new Processor instance
     */
    public Processor newProcessor(final String transtype) {
        if (ditaDir == null) {
            throw new IllegalStateException();
        }

        final Processor processor;
        if (processors.containsKey(transtype)) {
            final Class<Processor> cls = processors.get(transtype);
            try {
                processor = cls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (!Configuration.transtypes.contains(transtype)) {
                throw new IllegalArgumentException("Transtype " + transtype + " not supported");
            }
            processor = new ProcessorImpl(transtype);
        }

        return processor
                .setDitaDir(ditaDir)
                .setProperties(Collections.unmodifiableMap(args));
    }

}
