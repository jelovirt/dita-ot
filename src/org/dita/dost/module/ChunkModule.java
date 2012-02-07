/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2007 All Rights Reserved.
 */
package org.dita.dost.module;

import static org.dita.dost.util.Constants.*;
import static org.dita.dost.util.Job.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.AbstractPipelineOutput;
import org.dita.dost.reader.ChunkMapReader;
import org.dita.dost.util.FileUtils;
import org.dita.dost.util.Job;
import org.dita.dost.util.StringUtils;
import org.dita.dost.writer.TopicRefWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * The chunking module class.
 *
 */
final class ChunkModule implements AbstractPipelineModule {

    private DITAOTLogger logger;

    /**
     *  using to save relative path when do rename action for newly chunked file
     */
    final Map<String,String> relativePath2fix=new HashMap<String,String>();

    /**
     * Constructor.
     */
    public ChunkModule() {
        super();
    }

    public void setLogger(final DITAOTLogger logger) {
        this.logger = logger;
    }

    /**
     * Entry point of chunk module. Starting from map files, it parses and
     * processes chunk attribute, writes out the "chunked" results and finally
     * update references pointing to "chunked" topics in other dita topics.
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
        String tempDir = input.getAttribute(ANT_INVOKER_PARAM_TEMPDIR);
        final String ditaext = input.getAttribute(ANT_INVOKER_PARAM_DITAEXT);
        final String transtype = input.getAttribute(ANT_INVOKER_EXT_PARAM_TRANSTYPE);

        if (!new File(tempDir).isAbsolute()) {
            final String baseDir = input.getAttribute(ANT_INVOKER_PARAM_BASEDIR);
            tempDir = new File(baseDir, tempDir).getAbsolutePath();
        }
        //change to xml property
        final ChunkMapReader mapReader = new ChunkMapReader();
        mapReader.setLogger(logger);
        mapReader.setup(ditaext, transtype);

        Job job = null;
        try{
            job = new Job(new File(tempDir));
        }catch(final IOException ioe){
            throw new DITAOTException(ioe);
        }
        try{
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            String mapFile = new File(tempDir, job.getProperty(INPUT_DITAMAP)).getAbsolutePath();
            final Document doc = builder.parse(new File(mapFile));
            final Element root = doc.getDocumentElement();
            if(root.getAttribute(ATTRIBUTE_NAME_CLASS).contains(" eclipsemap/plugin ") && transtype.equals(INDEX_TYPE_ECLIPSEHELP)){
                for (final String ditaMap: job.getSet(FULL_DITAMAP_LIST)) {
                    mapFile = new File(tempDir, ditaMap).getAbsolutePath();
                    mapReader.read(mapFile);
                }
            }
            else{
                mapReader.read(mapFile);
            }
        }catch (final Exception e){
            logger.logException(e);
        }

        final Content content = mapReader.getContent();
        if(content.getValue()!=null){
            // update dita.list to include new generated files
            updateList((LinkedHashMap<String,String>)content.getValue(), mapReader.getConflicTable(),input);
            // update references in dita files
            updateRefOfDita(content, mapReader.getConflicTable(),input);
        }




        return null;
    }
    //update the href in ditamap and topic files
    private void updateRefOfDita(final Content changeTable, final Hashtable<String, String> conflictTable, final AbstractPipelineInput input){
        String tempDir = input.getAttribute(ANT_INVOKER_PARAM_TEMPDIR);
        if (!new File(tempDir).isAbsolute()) {
            final String baseDir = input.getAttribute(ANT_INVOKER_PARAM_BASEDIR);
            tempDir = new File(baseDir, tempDir).getAbsolutePath();
        }
        Job job = null;
        try{
            job = new Job(new File(tempDir));
        }catch(final IOException io){
            logger.logError(io.getMessage());
        }
        final TopicRefWriter topicRefWriter=new TopicRefWriter();
        topicRefWriter.setLogger(logger);
        topicRefWriter.setContent(changeTable);
        topicRefWriter.setup(conflictTable);
        try{
            for (final String f: job.getSet(FULL_DITAMAP_TOPIC_LIST)) {
                topicRefWriter.write(tempDir, f, this.relativePath2fix);
            }
        }catch(final DITAOTException ex){
            logger.logException(ex);
        }

    }


    private void updateList(final LinkedHashMap<String, String> changeTable, final Hashtable<String, String> conflictTable, final AbstractPipelineInput input){
        String tempDir = input.getAttribute(ANT_INVOKER_PARAM_TEMPDIR);
        if (!new File(tempDir).isAbsolute()) {
            final String baseDir = input.getAttribute(ANT_INVOKER_PARAM_BASEDIR);
            tempDir = new File(baseDir, tempDir).getAbsolutePath();

        }
        final File xmlDitalist=new File(tempDir, "dummy.xml");
        Job job = null;
        try{
            job = new Job(new File(tempDir));
        }catch(final IOException ex){
            logger.logException(ex);
        }

        final Set<String> hrefTopics = job.getSet(HREF_TOPIC_LIST);
        final Set<String> chunkTopics = job.getSet(CHUNK_TOPIC_LIST);
        for (final String s : chunkTopics) {
            if (!StringUtils.isEmptyString(s) && !s.contains(SHARP)) {
                // This entry does not have an anchor, we assume that this topic will
                // be fully chunked. Thus it should not produce any output.
                final Iterator<String> hrefit = hrefTopics.iterator();
                while(hrefit.hasNext()) {
                    final String ent = hrefit.next();
                    if (FileUtils.resolveFile(tempDir, ent).equalsIgnoreCase(
                            FileUtils.resolveFile(tempDir, s)))  {
                        // The entry in hrefTopics points to the same target
                        // as entry in chunkTopics, it should be removed.
                        hrefit.remove();
                    }
                }
            } else if (!StringUtils.isEmptyString(s) && hrefTopics.contains(s)) {
                hrefTopics.remove(s);
            }
        }

        final Set<String> topicList = new LinkedHashSet<String>(INT_128);
        final Set<String> oldTopicList = job.getSet(FULL_DITA_TOPIC_LIST);
        for (String t : hrefTopics) {
            if (t.lastIndexOf(SHARP) != -1) {
                t = t.substring(0, t.lastIndexOf(SHARP));
            }
            if (t.lastIndexOf(FILE_EXTENSION_DITAMAP) == -1) {
                final String ditaext = input.getAttribute(ANT_INVOKER_PARAM_DITAEXT);
                t = changeExtName(t, ditaext, ditaext);
            }
            t = FileUtils.getRelativePathFromMap(xmlDitalist.getAbsolutePath(), FileUtils.resolveFile(tempDir, t));
            topicList.add(t);
            if (oldTopicList.contains(t)) {
                oldTopicList.remove(t);
            }
        }

        final Set<String> chunkedTopicSet=new LinkedHashSet<String>(INT_128);
        final Set<String> chunkedDitamapSet=new LinkedHashSet<String>(INT_128);
        final Set<String> ditamapList = job.getSet(FULL_DITAMAP_LIST);
        for (final Map.Entry<String, String> entry: changeTable.entrySet()) {
            final String oldFile=entry.getKey();
            if(entry.getValue().equals(oldFile)){
                //newly chunked file
                String newChunkedFile=entry.getValue();
                newChunkedFile=FileUtils.getRelativePathFromMap(xmlDitalist.getAbsolutePath(), newChunkedFile);
                final String extName=getExtName(newChunkedFile);
                if(extName!=null && !extName.equalsIgnoreCase("DITAMAP")){
                    chunkedTopicSet.add(newChunkedFile);
                    if (!topicList.contains(newChunkedFile)) {
                        topicList.add(newChunkedFile);
                        if (oldTopicList.contains(newChunkedFile)) {
                            //newly chunked file shouldn't be deleted
                            oldTopicList.remove(newChunkedFile);
                        }
                    }
                }else{
                    if (!ditamapList.contains(newChunkedFile)) {
                        ditamapList.add(newChunkedFile);
                        if (oldTopicList.contains(newChunkedFile)) {
                            oldTopicList.remove(newChunkedFile);
                        }
                    }
                    chunkedDitamapSet.add(newChunkedFile);
                }

            }
        }
        //removed extra topic files
        for (final String s : oldTopicList) {
            if (!StringUtils.isEmptyString(s)) {
                final File f = new File(tempDir, s);
                if(f.exists()) {
                    f.delete();
                }
            }
        }

        //TODO we have refined topic list and removed extra topic files, next we need to clean up
        // conflictTable and try to resolve file name conflicts.
        for (final Map.Entry<String,String> entry: changeTable.entrySet()) {
            final String oldFile = entry.getKey();
            if (entry.getValue().equals(oldFile)) {
                // original topic file
                final String targetPath = conflictTable.get(entry.getKey());
                if (targetPath != null) {
                    final File target = new File(targetPath);
                    if (!FileUtils.fileExists(target.getAbsolutePath())) {
                        // newly chunked file
                        final File from = new File(entry.getValue());
                        String relativePath = FileUtils.getRelativePathFromMap(xmlDitalist.getAbsolutePath(), from.getAbsolutePath());
                        final String relativeTargetPath = FileUtils.getRelativePathFromMap(xmlDitalist.getAbsolutePath(), target.getAbsolutePath());
                        if (relativeTargetPath.lastIndexOf(SLASH)!=-1){
                            relativePath2fix.put(relativeTargetPath, relativeTargetPath.substring(0, relativeTargetPath.lastIndexOf(SLASH)+1));
                        }
                        //ensure the rename
                        target.delete();
                        //ensure the newly chunked file to the old one
                        from.renameTo(target);
                        if (topicList.contains(relativePath)) {
                            topicList.remove(relativePath);
                        }
                        if (chunkedTopicSet.contains(relativePath)){
                            chunkedTopicSet.remove(relativePath);
                        }
                        relativePath = FileUtils.getRelativePathFromMap(xmlDitalist.getAbsolutePath(), target.getAbsolutePath());
                        topicList.add(relativePath);
                        chunkedTopicSet.add(relativePath);
                    } else {
                        conflictTable.remove(entry.getKey());
                    }
                }
            }
        }

        //TODO Remove newly generated files from resource-only list, these new files should not
        //     excluded from the final outputs.
        final Set<String> resourceOnlySet = job.getSet(RESOURCE_ONLY_LIST);
        resourceOnlySet.removeAll(chunkedTopicSet);
        resourceOnlySet.removeAll(chunkedDitamapSet);

        job.setSet(RESOURCE_ONLY_LIST, resourceOnlySet);
        job.setSet(FULL_DITA_TOPIC_LIST, topicList);
        job.setSet(FULL_DITAMAP_LIST, ditamapList);
        topicList.addAll(ditamapList);
        job.setSet(FULL_DITAMAP_TOPIC_LIST, topicList);

        try {
            job.writeList(FULL_DITA_TOPIC_LIST);
            job.writeList(FULL_DITAMAP_LIST);
            job.writeList(FULL_DITAMAP_TOPIC_LIST);
        } catch (final FileNotFoundException e) {
            logger.logException(e);
        } catch (final IOException e) {
            logger.logException(e);
        }

        job.setProperty("chunkedditamapfile", CHUNKED_DITAMAP_LIST_FILE);
        job.setProperty("chunkedtopicfile", CHUNKED_TOPIC_LIST_FILE);
        job.setProperty("resourceonlyfile", RESOURCE_ONLY_LIST_FILE);
        try {
            job.writeList(CHUNKED_DITAMAP_LIST);
            job.writeList(CHUNKED_TOPIC_LIST);
            job.writeList(RESOURCE_ONLY_LIST);
        } catch (final IOException e) {
            logger.logError("Failed to write list file: " + e.getMessage(), e);
        }

        job.setSet(CHUNKED_DITAMAP_LIST, chunkedDitamapSet);
        job.setSet(CHUNKED_TOPIC_LIST, chunkedTopicSet);

        try{
            job.write();
        }catch(final IOException ex){
            logger.logException(ex);
        }
    }

    /**
     * Get file extension
     * 
     * @param file filename, may contain a URL fragment
     * @return file extensions
     */
    private String getExtName(final String file){
        final int index = file.indexOf(SHARP);

        if (file.startsWith(SHARP)) {
            return null;
        } else if (index != -1) {
            final String fileName = file.substring(0, index);
            final int fileExtIndex = fileName.lastIndexOf(DOT);
            return (fileExtIndex != -1) ? fileName.substring(fileExtIndex + 1,
                    fileName.length()) : null;
        } else {
            final int fileExtIndex = file.lastIndexOf(DOT);
            return (fileExtIndex != -1) ? file.substring(fileExtIndex + 1,
                    file.length()) : null;
        }
    }

    /**
     * Change file extension.
     * 
     * @param filename original file name, may be <code>null</code>
     * @param from source extension, may be <code>null</code>
     * @param to destination extension, may be <code>null</code>
     * @return filename with changed file extension, <code>null</code> if empty input
     */
    private String changeExtName(final String filename, String from, String to) {
        if (StringUtils.isEmptyString(filename)) {
            return null;
        }
        if (filename.indexOf(to) != -1) {
            return filename;
        }
        if (from == null) {
            from = "";
        }
        if (to == null) {
            to = "";
        }
        if (filename.lastIndexOf(from) != -1) {
            return filename.substring(0, filename.lastIndexOf(from)) + to;
        } else {
            return filename + to;
        }
    }
}
