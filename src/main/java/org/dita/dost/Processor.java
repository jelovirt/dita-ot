/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2017 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.util.Configuration;
import org.slf4j.Logger;

import java.io.File;
import java.net.URI;
import java.util.Map;

/**
 * DITA-OT processer.
 */
public interface Processor {

    /**
     * Set input document.
     *
     * @param input input document file
     * @return this Process object
     */
    Processor setInput(File input);

    /**
     * Set input document.
     *
     * @param input absolute input document URI
     * @return this Process object
     */
    Processor setInput(URI input);

    /**
     * Set output directory.
     *
     * @param output absolute output directory
     * @return this Process object
     */
    Processor setOutputDir(File output);

    /**
     * Set output directory.
     *
     * @param output absolute output directory URI
     * @return this Process object
     */
    Processor setOutputDir(URI output);

    /**
     * Set property. Existing property mapping will be overridden.
     *
     * @param name property name
     * @param value property value
     * @return this Process object
     */
    Processor setProperty(String name, String value);

    /**
     * Set properties. Existing property mapping will be overridden.
     *
     * @param value property mappings
     * @return this Process object
     */
    Processor setProperties(Map<String, String> value);

    /**
     * Set process logger
     *
     * @param logger process logger
     * @return this Process object
     */
    Processor setLogger(Logger logger);

    /**
     * Clean temporary directory when process fails. By default temporary directory is always cleaned.
     *
     * @param cleanOnFailure clean on failure
     * @return this Process object
     */
    Processor cleanOnFailure(boolean cleanOnFailure);

    /**
     * Write a debug log to temporary directory. The name of the debug log is temporary file with {@code .log} extension.
     * By default debug log is generated
     *
     * @param createDebugLog create debug log
     * @return this Process object
     */
    Processor createDebugLog(boolean createDebugLog);

    /**
     * Set error recovery mode.
     *
     * @param mode processing mode
     * @return this Process object
     */
    Processor setMode(Configuration.Mode mode);

    /**
     * Run process
     *
     * @throws DITAOTException if processing failed
     */
    void run() throws DITAOTException;
}
