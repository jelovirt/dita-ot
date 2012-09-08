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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.AbstractPipelineOutput;
import org.dita.dost.reader.ConrefPushReader;
import org.dita.dost.util.Job;
import org.dita.dost.util.ThreadUtils;
import org.dita.dost.writer.ConrefPushParser;

/**
 * Conref push module.
 * 
 *
 */
final class ConrefPushModule implements AbstractPipelineModule {

    private DITAOTLogger logger;

    public void setLogger(final DITAOTLogger logger) {
        this.logger = logger;
    }

    /**
     * @param input input
     * @return output
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
            logger.logException(e);
        }

        final Set<String> conrefpushlist = job.getSet(CONREF_PUSH_LIST);
        final ConrefPushReader reader = new ConrefPushReader();
        reader.setLogger(logger);
        for(final String fileName:conrefpushlist){
            final File file = new File(tempDir,fileName);
            logger.logInfo("Reading  " + file.getAbsolutePath());
            //FIXME: this reader calculate parent directory
            reader.read(file.getAbsolutePath());
        }

		final Map<String, Hashtable<String, String>> pushSet = reader.getPushMap();
                
        final List<Runnable> rs = new ArrayList<Runnable>(pushSet.size());
        for (final Map.Entry<String, Hashtable<String,String>> entry: pushSet.entrySet()) {
          rs.add(new Runnable() {
              public void run() {
            	  logger.logInfo("Processing " + new File(tempDir, entry.getKey()).getAbsolutePath());
                  final ConrefPushParser parser = new ConrefPushParser();
                  parser.setLogger(logger);
                  final Content content = new ContentImpl();
                  content.setValue(entry.getValue());
                  parser.setContent(content);
                  //pass the tempdir to ConrefPushParser
                  parser.setTempDir(tempDir.getAbsolutePath());
                  //FIXME:This writer creates and renames files, have to
                  try {
                	  parser.write(entry.getKey());
                  } catch (DITAOTException e) {
                      logger.logException(e);
                  }
              }});
        }
        ThreadUtils.run(rs);

        return null;
    }

}
