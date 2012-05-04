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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.module.GenMapAndTopicListModule.KeyDef;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.AbstractPipelineOutput;
import org.dita.dost.reader.KeyrefReader;
import org.dita.dost.util.Job;
import org.dita.dost.util.ThreadUtils;
import org.dita.dost.writer.KeyrefPaser;
/**
 * Keyref Module.
 *
 */
final class KeyrefModule implements AbstractPipelineModule {

    private DITAOTLogger logger;

    public void setLogger(final DITAOTLogger logger) {
        this.logger = logger;
    }

    /**
     * Entry point of KeyrefModule.
     * 
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

        if (!tempDir.isAbsolute()){
            throw new IllegalArgumentException("Temporary directory " + tempDir + " must be absolute");
        }

        //Added by Alan Date:2009-08-04 --begin
        final String ext = input.getAttribute(ANT_INVOKER_PARAM_DITAEXT);
        final String extName = ext.startsWith(DOT) ? ext : (DOT + ext);
        //Added by Alan Date:2009-08-04 --end

        Job job = null;
        try{
            job = new Job(tempDir);
        }catch(final Exception e){
            logger.logException(e);
        }

        // maps of keyname and target
        final Map<String, String> keymap =new HashMap<String, String>();
        // store the key name defined in a map(keyed by ditamap file)
        final Hashtable<String, Set<String>> maps = new Hashtable<String, Set<String>>();

        // get the key definitions from the dita.list, and the ditamap where it is defined
        // are not handle yet.
        for(final String key: job.getSet(KEY_LIST)){
            final KeyDef keyDef = new KeyDef(key);
            keymap.put(keyDef.keys, keyDef.href);
            // map file which define the keys
            final String map = keyDef.source;
            // put the keyname into corresponding map which defines it.
            //a map file can define many keys
            if(maps.containsKey(map)){
                maps.get(map).add(keyDef.keys);
            }else{
                final Set<String> set = new HashSet<String>();
                set.add(keyDef.keys);
                maps.put(map, set);
            }
        }
        final KeyrefReader reader = new KeyrefReader();
        reader.setLogger(logger);
        reader.setTempDir(tempDir.getAbsolutePath());
        for(final String mapFile: maps.keySet()){
            logger.logInfo("Reading " + new File(tempDir, mapFile).getAbsolutePath());
            reader.setKeys(maps.get(mapFile));
            reader.read(mapFile);
        }
        final Content content = reader.getContent();
        //get files which have keyref attr
        final Set<String> parseList = job.getSet(KEYREF_LIST);
        //Conref Module will change file's content, it is possible that tags with @keyref are copied in
        //while keyreflist is hard update with xslt.
        //bug:3056939
        final Set<String> conrefList = job.getSet(CONREF_LIST);
        parseList.addAll(conrefList);
        
        final List<Runnable> rs = new ArrayList<Runnable>(parseList.size());
        for (final String file: parseList) {
            rs.add(new Runnable() {
                public void run() {
                    logger.logInfo("Processing " + new File(tempDir, file).getAbsolutePath());
                    final KeyrefPaser parser = new KeyrefPaser();
                    parser.setLogger(logger);
                    parser.setContent(content);
                    parser.setTempDir(tempDir.getAbsolutePath());
                    parser.setKeyMap(keymap);
                    parser.setExtName(extName);
                    try {
                        parser.write(file);
                    } catch (DITAOTException e) {
                        logger.logException(e);
                    }
                }});
        }
        ThreadUtils.run(rs);
        
        return null;
    }
    
}
