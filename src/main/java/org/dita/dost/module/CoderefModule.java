/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2010 All Rights Reserved.
 */
package org.dita.dost.module;

import static org.dita.dost.util.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.AbstractPipelineOutput;
import org.dita.dost.util.Job;
import org.dita.dost.util.ThreadUtils;
import org.dita.dost.writer.CoderefResolver;

/**
 * Coderef Module class.
 *
 */
final class CoderefModule implements AbstractPipelineModule {

    private DITAOTLogger logger;

    /**
     * Constructor.
     */
    public CoderefModule() {
        super();
    }

    public void setLogger(final DITAOTLogger logger) {
        this.logger = logger;
    }

    /**
     * Entry point of Coderef Module.
     * @param input Input parameters and resources.
     * @return null
     * @throws DITAOTException exception
     */
    public AbstractPipelineOutput execute(final AbstractPipelineInput input)
            throws DITAOTException {
        if (logger == null) {
            throw new IllegalStateException("Logger not set");
        }
        final File tempDir = new File(input.getAttribute(ANT_INVOKER_PARAM_TEMPDIR));
        if (!tempDir.isAbsolute()) {
            throw new IllegalArgumentException("Temporary directory " + tempDir + " must be absolute");
        }

        Job job = null;
        try{
            job = new Job(tempDir);
        }catch(final IOException e){
            throw new DITAOTException(e);
        }

        final Set<String> codereflist=job.getSet(CODEREF_LIST);
        
        final List<Runnable> rs = new ArrayList<Runnable>(codereflist.size());
        for (final String file: codereflist) {
            rs.add(new Runnable() {
                public void run() {
                    logger.logInfo("Processing " + new File(tempDir, file).getAbsolutePath());
                    final CoderefResolver writer = new CoderefResolver();
                    writer.setLogger(logger);
                    try {
                        writer.write(new File(tempDir, file).getAbsolutePath());
                    } catch (DITAOTException e) {
                        logger.logException(e);
                    }
                }});
        }
        ThreadUtils.run(rs);
        
        return null;
    }

}
