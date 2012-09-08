/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2004, 2005 All Rights Reserved.
 */
package org.dita.dost.module;

import static org.dita.dost.util.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Element;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.log.MessageUtils;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.AbstractPipelineOutput;
import org.dita.dost.reader.MapMetaReader;
import org.dita.dost.util.FileUtils;
import org.dita.dost.util.Job;
import org.dita.dost.util.ThreadUtils;
import org.dita.dost.writer.DitaMapMetaWriter;
import org.dita.dost.writer.DitaMetaWriter;

/**
 * MoveMetaModule implement the move index step in preprocess. It reads the index
 * information from ditamap file and move these information to different
 * corresponding dita topic file.
 * 
 * @author Zhang, Yuan Peng
 */
final class MoveMetaModule implements AbstractPipelineModule {

    private DITAOTLogger logger;

    /**
     * Default constructor of MoveMetaModule class.
     */
    public MoveMetaModule() {
        super();
    }

    public void setLogger(final DITAOTLogger logger) {
        this.logger = logger;
    }

    /**
     * Entry point of MoveMetaModule.
     * 
     * @param input Input parameters and resources.
     * @return null
     * @throws DITAOTException exception
     */
    public AbstractPipelineOutput execute(final AbstractPipelineInput input) throws DITAOTException {
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
        } catch (final IOException e) {
            throw new DITAOTException(e);
        }

        final MapMetaReader metaReader = new MapMetaReader();
        metaReader.setLogger(logger);
        final Set<String> fullditamaplist = job.getSet(FULL_DITAMAP_LIST);
        for (String mapFile: fullditamaplist) {
            mapFile = new File(tempDir, mapFile).getAbsolutePath();
            logger.logInfo("Reading " + mapFile);
            //FIXME: this reader gets the parent path of input file
            metaReader.read(mapFile);
            final File oldMap = new File(mapFile);
            final File newMap = new File(mapFile+".temp");
            if (newMap.exists()) {
                if (!oldMap.delete()) {
                    final Properties p = new Properties();
                    p.put("%1", oldMap.getPath());
                    p.put("%2", newMap.getAbsolutePath()+".chunk");
                    logger.logError(MessageUtils.getMessage("DOTJ009E", p).toString());
                }
                if (!newMap.renameTo(oldMap)) {
                    final Properties p = new Properties();
                    p.put("%1", oldMap.getPath());
                    p.put("%2", newMap.getAbsolutePath()+".chunk");
                    logger.logError(MessageUtils.getMessage("DOTJ009E", p).toString());
                }
            }
        }

        final Map<String, Hashtable<String, Element>> mapSet = metaReader.getMapping();
        
        //process map first
        final DitaMapMetaWriter mapInserter = new DitaMapMetaWriter();
        mapInserter.setLogger(logger);
        for (final Entry<String, Hashtable<String, Element>> entry: mapSet.entrySet()) {
            String targetFileName = entry.getKey();
            targetFileName = targetFileName.indexOf(SHARP) != -1
                             ? targetFileName.substring(0, targetFileName.indexOf(SHARP))
                             : targetFileName;
            if (targetFileName.endsWith(FILE_EXTENSION_DITAMAP )) {
                if (FileUtils.fileExists(entry.getKey())) {
                    mrs.add(new Runnable() {
                        public void run() {
                            logger.logInfo("Processing " + entry.getKey());
                            final DitaMapMetaWriter mapInserter = new DitaMapMetaWriter();
                            mapInserter.setLogger(logger);
                            final ContentImpl content = new ContentImpl();
                            content.setValue(entry.getValue());
                            mapInserter.setContent(content);
                            mapInserter.write(entry.getKey());
                        }});
                } else {
                    logger.logError("File " + entry.getKey() + " does not exist");
                }

            }
        }
        ThreadUtils.run(mrs);

        //process topic
        final List<Runnable> rs = new ArrayList<Runnable>(mapSet.size());
        for (final Map.Entry<String, Hashtable<String, Element>> entry: mapSet.entrySet()) {
            String targetFileName = entry.getKey();
            targetFileName = targetFileName.indexOf(SHARP) != -1
                             ? targetFileName.substring(0, targetFileName.indexOf(SHARP))
                             : targetFileName;
            if (targetFileName.endsWith(FILE_EXTENSION_DITA) || targetFileName.endsWith(FILE_EXTENSION_XML)) {
                if (FileUtils.fileExists(entry.getKey())) {
                    rs.add(new Runnable() {
                        public void run() {
                            logger.logInfo("Processing " + entry.getKey());
                            final DitaMetaWriter topicInserter = new DitaMetaWriter();
                            topicInserter.setLogger(logger);
                            final ContentImpl content = new ContentImpl();
                            content.setValue(entry.getValue());
                            topicInserter.setContent(content);
                            topicInserter.write(entry.getKey());
                        }});
                } else {
                    logger.logError("File " + entry.getKey() + " does not exist");
                }
            }
        }
        ThreadUtils.run(rs);
        
        return null;
    }
}
