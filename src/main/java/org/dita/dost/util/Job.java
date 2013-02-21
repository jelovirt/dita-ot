/*
 * This file is part of the DITA Open Toolkit project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.dita.dost.util;

import static org.dita.dost.util.Constants.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Definition of current job.
 * 
 * <p>Instances are thread-safe.</p>
 * 
 * @since 1.5.4
 */
public final class Job {

    private static final String JOB_FILE = ".job.xml";
    
    private static final String ELEMENT_JOB = "job";
    private static final String ATTRIBUTE_KEY = "key";
    private static final String ELEMENT_ENTRY = "entry";
    private static final String ELEMENT_MAP = "map";
    private static final String ELEMENT_SET = "set";
    private static final String ELEMENT_STRING = "string";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ELEMENT_PROPERTY = "property";

    private static final String ELEMENT_FILES = "files";
    private static final String ELEMENT_FILE = "file";
    private static final String ATTRIBUTE_PATH = "path";
    private static final String ATTRIBUTE_FORMAT = "format";
    private static final String ATTRIBUTE_CHUNKED = "chunked";
    private static final String ATTRIBUTE_HAS_CONREF = "has-conref";
    private static final String ATTRIBUTE_HAS_KEYREF = "has-keyref";
    private static final String ATTRIBUTE_HAS_CODEREF = "has-coderef";
    private static final String ATTRIBUTE_RESOURCE_ONLY = "resource-only";
    // TODO: Maybe should be link-target
    private static final String ATTRIBUTE_TARGET = "target";
    private static final String ATTRIBUTE_CONREF_TARGET = "conref-target";
    private static final String ATTRIBUTE_NON_CONREF_TARGET = "non-conref-target";
    // TODO: Maybe should be conaction-target
    private static final String ATTRIBUTE_CONREF_PUSH = "conrefpush";
    private static final String ATTRIBUTE_HAS_LINK = "has-link";
    
    /** File name for chuncked dita map list file */
    public static final String CHUNKED_DITAMAP_LIST_FILE = "chunkedditamap.list";
    /** File name for chunked topic list file */
    public static final String CHUNKED_TOPIC_LIST_FILE = "chunkedtopic.list";
    /** File name for skip chunk list file */
    public static final String CHUNK_TOPIC_LIST_FILE = "skipchunk.list";
    /** File name for coderef list file */
    public static final String CODEREF_LIST_FILE = "coderef.list";
    /** File name for conref list file */
    public static final String CONREF_LIST_FILE = "conref.list";
    /** File name for conref push list file */
    public static final String CONREF_PUSH_LIST_FILE = "conrefpush.list";
    /** File name for conref targets list file */
    public static final String CONREF_TARGET_LIST_FILE = "conreftargets.list";
    /** File name for copy-to source list file */
    public static final String COPYTO_SOURCE_LIST_FILE = "copytosource.list";
    /** File name for copy-to target2sourcemap list file */
    public static final String COPYTO_TARGET_TO_SOURCE_MAP_LIST_FILE = "copytotarget2sourcemap.list";
    /** File name for flag image list file */
    public static final String FLAG_IMAGE_LIST_FILE = "flagimage.list";
    /** File name for map list file */
    public static final String FULL_DITAMAP_LIST_FILE = "fullditamap.list";
    /** File name for map and topic list file */
    public static final String FULL_DITAMAP_TOPIC_LIST_FILE = "fullditamapandtopic.list";
    /** File name for topic list file */
    public static final String FULL_DITA_TOPIC_LIST_FILE = "fullditatopic.list";
    /** File name for href topic list file */
    public static final String HREF_DITA_TOPIC_LIST_FILE = "hrefditatopic.list";
    /** File name for href targets list file */
    public static final String HREF_TARGET_LIST_FILE = "hreftargets.list";
    /** File name for candidate topics list file */
    public static final String HREF_TOPIC_LIST_FILE = "canditopics.list";
    /** File name for html list file */
    public static final String HTML_LIST_FILE = "html.list";
    /** File name for image list file */
    public static final String IMAGE_LIST_FILE = "image.list";
    /** File name for input file list file */
    public static final String INPUT_DITAMAP_LIST_FILE = "user.input.file.list";
    /** File name for key definition file */
    public static final String KEYDEF_LIST_FILE = "keydef.xml";
    /** File name for key definition file */
    public static final String SUBJECT_SCHEME_KEYDEF_LIST_FILE = "schemekeydef.xml";
    /** File name for keyref list file */
    public static final String KEYREF_LIST_FILE = "keyref.list";
    /** File name for key list file */
    public static final String KEY_LIST_FILE = "key.list";
    /** File name for out dita files list file */
    public static final String OUT_DITA_FILES_LIST_FILE = "outditafiles.list";
    /** File name for relflag image list file */
    public static final String REL_FLAGIMAGE_LIST_FILE = "relflagimage.list";
    /** File name for resource-only list file */
    public static final String RESOURCE_ONLY_LIST_FILE = "resourceonly.list";
    /** File name for subject scheme list file */
    public static final String SUBJEC_SCHEME_LIST_FILE = "subjectscheme.list";
    /** File name for subtargets list file */
    public static final String SUBSIDIARY_TARGET_LIST_FILE = "subtargets.list";
    /** File name for temporary input file list file */
    public static final String USER_INPUT_FILE_LIST_FILE = "usr.input.file.list";
    
    private final Map<String, Object> prop;
    private final File tempDir;
    private final Map<String, FileInfo> files = new HashMap<String, FileInfo>();

    /**
     * Create new job configuration instance. Initialise by reading temporary configuration files.
     *  
     * @param tempDir temporary directory
     * @throws IOException if reading configuration files failed
     * @throws IllegalStateException if configuration files are missing
     */
    public Job(final File tempDir) throws IOException {
        this.tempDir = tempDir;
        prop = new HashMap<String, Object>();
        read();
    }

    /**
     * Read temporary configuration files. If configuration files are not found,
     * assume an empty job object is being created.
     * 
     * @throws IOException if reading configuration files failed
     * @throws SAXException if XML parsing failed
     * @throws IllegalStateException if configuration files are missing
     */
    private void read() throws IOException {
        final File jobFile = new File(tempDir, JOB_FILE);
        if (jobFile.exists()) {
        	InputStream in = null;
            try {
                XMLReader parser = StringUtils.getXMLReader();
                parser.setContentHandler(new JobHandler(prop, files));
                parser.parse(jobFile.toURI().toString());
            } catch (final SAXException e) {
                throw new IOException("Failed to read job file: " + e.getMessage());
            } finally {
            	if (in != null) {
            		in.close();
            	}
            } 
            return;
        }

        final Properties p = new Properties();
        final File ditalist = new File(tempDir, FILE_NAME_DITA_LIST);
        final File xmlDitalist=new File(tempDir, FILE_NAME_DITA_LIST_XML);
        InputStream in = null;
        try{
            if(xmlDitalist.exists()) {
                in = new FileInputStream(xmlDitalist);
                p.loadFromXML(in);
            } else if(ditalist.exists()) {
                in = new FileInputStream(ditalist);
                p.load(in);
            }
        } catch(final IOException e) {
            throw new IOException("Failed to read file: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException e) {
                    throw new IOException("Failed to close file: " + e.getMessage());
                }
            }
        }
        
        readProperties(p);
    }

	private void readProperties(final Properties p) {
		for (final Map.Entry<Object, Object> e: p.entrySet()) {
            if (((String) e.getValue()).length() > 0) {
            	final String key = e.getKey().toString();
            	if (key.equals(COPYTO_TARGET_TO_SOURCE_MAP_LIST)) {
            		setMap(e.getKey().toString(), StringUtils.restoreMap(e.getValue().toString()));
            	} else if (key.endsWith("list")) {
            		setSet(e.getKey().toString(), StringUtils.restoreSet(e.getValue().toString()));
            	} else {
            		setProperty(e.getKey().toString(), e.getValue().toString());
            	}
            }
        }
    }
    
    private final static class JobHandler extends DefaultHandler {

        private final Map<String, Object> prop;
        private final Map<String, FileInfo> files;
        private StringBuilder buf;
        private String name;
        private String key;
        private Set<String> set;
        private Map<String, String> map;
        
        JobHandler(final Map<String, Object> prop, Map<String, FileInfo> files) {
            this.prop = prop;
            this.files = files;
        }
        
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (buf != null) {
                buf.append(ch, start, length);
            }
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            if (buf != null) {
                buf.append(ch, start, length);
            }
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            final String n = localName != null ? localName : qName;
            if (n.equals(ELEMENT_PROPERTY)) {
                name = atts.getValue(ATTRIBUTE_NAME);
            } else if (n.equals(ELEMENT_STRING)) {
                buf = new StringBuilder();
            } else if (n.equals(ELEMENT_SET)) {
                set = new HashSet<String>();
            } else if (n.equals(ELEMENT_MAP)) {
                map = new HashMap<String, String>();
            } else if (n.equals(ELEMENT_ENTRY)) {
                key = atts.getValue(ATTRIBUTE_KEY);
            } else if (n.equals(ELEMENT_FILE)) {
                final String path = atts.getValue(ATTRIBUTE_PATH);
                final FileInfo i = new FileInfo(path);
                i.format = atts.getValue(ATTRIBUTE_FORMAT);
                i.isChunked = Boolean.parseBoolean(atts.getValue(ATTRIBUTE_CHUNKED));
                i.hasLink = Boolean.parseBoolean(atts.getValue(ATTRIBUTE_HAS_LINK));
                i.hasConref = Boolean.parseBoolean(atts.getValue(ATTRIBUTE_HAS_CONREF));
                i.hasKeyref = Boolean.parseBoolean(atts.getValue(ATTRIBUTE_HAS_KEYREF));
                i.hasCoderef = Boolean.parseBoolean(atts.getValue(ATTRIBUTE_HAS_CODEREF));
                i.isResourceOnly = Boolean.parseBoolean(atts.getValue(ATTRIBUTE_RESOURCE_ONLY));
                i.isTarget = Boolean.parseBoolean(atts.getValue(ATTRIBUTE_TARGET));
                i.isConrefTarget = Boolean.parseBoolean(atts.getValue(ATTRIBUTE_CONREF_TARGET));
                i.isNonConrefTarget = Boolean.parseBoolean(atts.getValue(ATTRIBUTE_NON_CONREF_TARGET));
                i.isConrefPush = Boolean.parseBoolean(atts.getValue(ATTRIBUTE_CONREF_PUSH));
                files.put(path, i);
            }
        }
        
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            final String n = localName != null ? localName : qName;
            if (n.equals(ELEMENT_PROPERTY)) {
                name = null;
            } else if (n.equals(ELEMENT_STRING)) {
                if (set != null) {
                    set.add(buf.toString());
                } else if (map != null) {
                    map.put(key, buf.toString());
                } else {
                    prop.put(name, buf.toString());
                }
                buf = null;
            } else if (n.equals(ELEMENT_SET)) {
                prop.put(name, set);
                set = null;
            } else if (n.equals(ELEMENT_MAP)) {
                prop.put(name, map);
                map = null;
            } else if (n.equals(ELEMENT_ENTRY)) {
                key = null;
            }
        }
        
    }
    
    /**
     * Store job into temporary configuration files.
     * 
     * @throws IOException if writing configuration files failed
     */
    public void write() throws IOException {
    	OutputStream outStream = null;
        XMLStreamWriter out = null;
        try {
        	outStream = new FileOutputStream(new File(tempDir, JOB_FILE));
            out = XMLOutputFactory.newInstance().createXMLStreamWriter(outStream, "UTF-8");
            out.writeStartDocument();
            out.writeStartElement(ELEMENT_JOB);
            for (final Map.Entry<String, Object> e: prop.entrySet()) {
                out.writeStartElement(ELEMENT_PROPERTY);
                out.writeAttribute(ATTRIBUTE_NAME, e.getKey());
                if (e.getValue() instanceof String) {
                    out.writeStartElement(ELEMENT_STRING);
                    out.writeCharacters(e.getValue().toString());
                    out.writeEndElement(); //string
                } else if (e.getValue() instanceof Set) {
                    out.writeStartElement(ELEMENT_SET);
                    final Set<?> s = (Set<?>) e.getValue();
                    for (final Object o: s) {
                        out.writeStartElement(ELEMENT_STRING);
                        out.writeCharacters(o.toString());
                        out.writeEndElement(); //string
                    }
                    out.writeEndElement(); //set
                } else if (e.getValue() instanceof Map) {
                    out.writeStartElement(ELEMENT_MAP);
                    final Map<?, ?> s = (Map<?, ?>) e.getValue();
                    for (final Map.Entry<?, ?> o: s.entrySet()) {
                        out.writeStartElement(ELEMENT_ENTRY);
                        out.writeAttribute(ATTRIBUTE_KEY, o.getKey().toString());
                        out.writeStartElement(ELEMENT_STRING);
                        out.writeCharacters(o.getValue().toString());
                        out.writeEndElement(); //string
                        out.writeEndElement(); //entry
                    }
                    out.writeEndElement(); //string
                } else {
                    out.writeStartElement(e.getValue().getClass().getName());
                    out.writeCharacters(e.getValue().toString());
                    out.writeEndElement(); //string
                }
                out.writeEndElement(); //property
            }
            out.writeStartElement(ELEMENT_FILES);
            for (final FileInfo i: files.values()) {
                out.writeStartElement(ELEMENT_FILE);
                out.writeAttribute(ATTRIBUTE_PATH, i.file);
                if (i.format != null) {
                	out.writeAttribute(ATTRIBUTE_FORMAT, i.format);
                }
                if (i.isChunked) {
                    out.writeAttribute(ATTRIBUTE_CHUNKED, Boolean.toString(i.isChunked));
                }
                if (i.hasLink) {
                    out.writeAttribute(ATTRIBUTE_HAS_LINK, Boolean.toString(i.hasLink));    
                }
                if (i.hasConref) {
                    out.writeAttribute(ATTRIBUTE_HAS_CONREF, Boolean.toString(i.hasConref));    
                }
                if (i.hasKeyref) {
                    out.writeAttribute(ATTRIBUTE_HAS_KEYREF, Boolean.toString(i.hasKeyref));    
                }
                if (i.hasCoderef) {
                    out.writeAttribute(ATTRIBUTE_HAS_CODEREF, Boolean.toString(i.hasCoderef));    
                }
                if (i.isResourceOnly) {
                    out.writeAttribute(ATTRIBUTE_RESOURCE_ONLY, Boolean.toString(i.isResourceOnly));    
                }
                if (i.isTarget) {
                    out.writeAttribute(ATTRIBUTE_TARGET, Boolean.toString(i.isTarget));    
                }
                if (i.isConrefTarget) {
                    out.writeAttribute(ATTRIBUTE_CONREF_TARGET, Boolean.toString(i.isConrefTarget));    
                }
                if (i.isNonConrefTarget) {
                    out.writeAttribute(ATTRIBUTE_NON_CONREF_TARGET, Boolean.toString(i.isNonConrefTarget));    
                }
                if (i.isConrefPush) {
                    out.writeAttribute(ATTRIBUTE_CONREF_PUSH, Boolean.toString(i.isConrefPush));    
                }
                out.writeEndElement(); //file
            }
            out.writeEndElement(); //files
            out.writeEndElement(); //job
            out.writeEndDocument();
        } catch (final IOException e) {
            throw new IOException("Failed to write file: " + e.getMessage());
        } catch (final XMLStreamException e) {
            throw new IOException("Failed to serialize job file: " + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final XMLStreamException e) {
                    throw new IOException("Failed to close file: " + e.getMessage());
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (final IOException e) {
                    throw new IOException("Failed to close file: " + e.getMessage());
                }
            }
        }

        final Properties p = new Properties();
        for (final Map.Entry<String, Object> e: prop.entrySet()) {
            if (e.getValue() instanceof Set) {
                p.put(e.getKey(), StringUtils.assembleString((Collection) e.getValue(), COMMA));
            } else if (e.getValue() instanceof Map) {
                p.put(e.getKey(), StringUtils.assembleString((Map) e.getValue(), COMMA));
            } else {
                p.put(e.getKey(), e.getValue());
            }
        }
        
        FileOutputStream propertiesOutputStream = null;
        try {
            propertiesOutputStream = new FileOutputStream(new File(tempDir, FILE_NAME_DITA_LIST));
            p.store(propertiesOutputStream, null);
            propertiesOutputStream.flush();
        } catch (final IOException e) {
            throw new IOException("Failed to write file: " + e.getMessage());
        } finally {
            if (propertiesOutputStream != null) {
                try {
                    propertiesOutputStream.close();
                } catch (final IOException e) {
                    throw new IOException("Failed to close file: " + e.getMessage());
                }
            }
        }
        FileOutputStream xmlOutputStream = null;
        try {
            xmlOutputStream = new FileOutputStream(new File(tempDir, FILE_NAME_DITA_LIST_XML));
            p.storeToXML(xmlOutputStream, null);
            xmlOutputStream.flush();
        } catch (final IOException e) {
            throw new IOException("Failed to write file: " + e.getMessage());
        } finally {
            if (xmlOutputStream != null) {
                try {
                    xmlOutputStream.close();
                } catch (final IOException e) {
                    throw new IOException("Failed to close file: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Searches for the property with the specified key in this property list.
     * 
     * @param key property key
     * @return the value in this property list with the specified key value, {@code null} if not found
     */
    public String getProperty(final String key) {
        final Object value = prop.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof Set) { // migration support
            return StringUtils.assembleString((Collection) value, COMMA);
        } else if (value instanceof Map) { // migration support
            return StringUtils.assembleString((Map) value, COMMA);
        } else {
            return (String) value;
        }
    }
    
    /**
     * Searches for the property with the specified key in this property list.
     * 
     * @param key property key
     * @return the value in this property list with the specified key value, empty map if not found
     */
    public Map<String, String> getMap(final String key) {
        final Object value = prop.get(key);
        if (value == null) {
            return Collections.emptyMap();
        } else if (value instanceof String) { // migration support
            return StringUtils.restoreMap((String) value);
        } else {
            return (Map<String, String>) value;
        }
    }
    
    /**
     * Searches for the property with the specified key in this property list.
     * 
     * @param key property key
     * @return the value in this property list with the specified key value, empty set if not found
     */
    public Set<String> getSet(final String key) {
        if (key.equals(FULL_DITAMAP_TOPIC_LIST)) {
            return getFilesByFormat("dita", "ditamap");
        } else if (key.equals(FULL_DITA_TOPIC_LIST)) {
            return getFilesByFormat("dita");
        } else if (key.equals(FULL_DITAMAP_LIST)) {
            return getFilesByFormat("ditamap");
        } else if (key.equals(HTML_LIST)) {
            return getFilesByFormat("html");
        } else if (key.equals(IMAGE_LIST)) {
            return getFilesByFormat("image");
        } else if (key.equals(CHUNKED_TOPIC_LIST)) {
            Set<String> ret = new HashSet<String>();
            for (final FileInfo f: files.values()) {
                if (f.isChunked) {
                    ret.add(f.file);
                }
            }
            return ret;
        } else if (key.equals(CONREF_LIST)) {
            Set<String> ret = new HashSet<String>();
            for (final FileInfo f: files.values()) {
                if (f.hasConref) {
                    ret.add(f.file);
                }
            }
            return ret;
        } else if (key.equals(HREF_DITA_TOPIC_LIST)) {
            Set<String> ret = new HashSet<String>();
            for (final FileInfo f: files.values()) {
                if (f.hasLink) {
                    ret.add(f.file);
                }
            }
            return ret;
        } else if (key.equals(KEYREF_LIST)) {
//            throw new RuntimeException(KEYREF_LIST + " should be accessed via files accessor");
            Set<String> ret = new HashSet<String>();
            for (final FileInfo f: files.values()) {
                if (f.hasKeyref) {
                    ret.add(f.file);
                }
            }
            return ret;
        } else if (key.equals(CODEREF_LIST)) {
            Set<String> ret = new HashSet<String>();
            for (final FileInfo f: files.values()) {
                if (f.hasCoderef) {
                    ret.add(f.file);
                }
            }
            return ret;
        } else if (key.equals(RESOURCE_ONLY_LIST)) {
            Set<String> ret = new HashSet<String>();
            for (final FileInfo f: files.values()) {
                if (f.isResourceOnly) {
                    ret.add(f.file);
                }
            }
            return ret;
        } else if (key.equals(HREF_TARGET_LIST)) {
            Set<String> ret = new HashSet<String>();
            for (final FileInfo f: files.values()) {
                if (f.isTarget) {
                    ret.add(f.file);
                }
            }
            return ret;
        } else if (key.equals(HREF_TOPIC_LIST)) {
            Set<String> ret = new HashSet<String>();
            for (final FileInfo f: files.values()) {
                if (f.isNonConrefTarget) {
                    ret.add(f.file);
                }
            }
            return ret;
        } else if (key.equals(CONREF_TARGET_LIST)) {
            Set<String> ret = new HashSet<String>();
            for (final FileInfo f: files.values()) {
                if (f.isConrefTarget) {
                    ret.add(f.file);
                }
            }
            return ret;    
        } else if (key.equals(CONREF_PUSH_LIST)) {
            Set<String> ret = new HashSet<String>();
            for (final FileInfo f: files.values()) {
                if (f.isConrefPush) {
                    ret.add(f.file);
                }
            }
            return ret;
        } else {
            final Object value = prop.get(key);
            if (value == null) {
                return Collections.emptySet();
            } else if (value instanceof String) { // migration support
                return StringUtils.restoreSet((String) value);
            } else {
                return (Set<String>) value;
            }
        }
        //return (Set<String>) prop.get(key);
    }
    
    private Set<String> getFilesByFormat(final String...formats) {
        Set<String> ret = new HashSet<String>();
        for (final FileInfo f: files.values()) {
            format: for (final String format: formats) {
                if (format.equals(f.format)) {
                    ret.add(f.file);
                    break format;
                }
            }
        }
        return ret;
    }
    
    /**
     * Set property value.
     * 
     * @param key property key
     * @param value property value
     * @return the previous value of the specified key in this property list, or {@code null} if it did not have one
     */
    public Object setProperty(final String key, final String value) {
        return prop.put(key, value);
    }
    
    /**
     * Set property value.
     * 
     * @param key property key
     * @param value property value
     * @return the previous value of the specified key in this property list, or {@code null} if it did not have one
     */
    public Set<String> setSet(final String key, final Set<String> value) {
        Object previous = null;
        if (key.equals(FULL_DITAMAP_TOPIC_LIST)) {
            // skip, compound set
        } else if (key.equals(FULL_DITA_TOPIC_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).format = "dita";
            }
        } else if (key.equals(FULL_DITAMAP_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).format = "ditamap";
            }
        } else if (key.equals(HTML_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).format = "html";
            }
        } else if (key.equals(IMAGE_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).format = "image";
            }
        } else if (key.equals(CHUNKED_TOPIC_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).isChunked = true;
            }    
        } else if (key.equals(CONREF_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).hasConref = true;
            }
        } else if (key.equals(HREF_DITA_TOPIC_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).hasLink = true;
            }
        } else if (key.equals(KEYREF_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).hasKeyref = true;
            }
        } else if (key.equals(CODEREF_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).hasCoderef = true;
            }
        } else if (key.equals(RESOURCE_ONLY_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).isResourceOnly = true;
            }
        } else if (key.equals(HREF_TARGET_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).isTarget = true;
            }
        } else if (key.equals(CONREF_TARGET_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).isConrefTarget = true;
            }
        } else if (key.equals(HREF_TOPIC_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).isNonConrefTarget = true;
            }

        } else if (key.equals(CONREF_PUSH_LIST)) {
            for (final String f: value) {
            	getOrAdd(f).isConrefPush = true;
            }
        } else {
            previous = prop.put(key, value);
            if (previous == null) {
                return null;
            } else if (previous instanceof String) { // migration support
                return StringUtils.restoreSet((String) previous);
            } else {
                return (Set<String>) previous;
            }
        }
        return (Set<String>) previous;
    }

	private FileInfo getOrAdd(final String f) {
		FileInfo i = files.get(f); 
		if (i == null) {
			i = new FileInfo(f);
		    files.put(f, i);
		}
		return i;
	}
    
    /**
     * Set property value.
     * 
     * @param key property key
     * @param value property value
     * @return the previous value of the specified key in this property list, or {@code null} if it did not have one
     */
    public Map<String, String> setMap(final String key, final Map<String, String> value) {        
        final Object previous = prop.put(key, value);
        if (previous == null) {
            return null;
        } else if (previous instanceof String) { // migration support
            return StringUtils.restoreMap((String) previous);
        } else {
            return (Map<String, String>) previous;
        }
    }
    
    /**
     * Return the copy-to map.
     * @return copy-to map
     */
    public Map<String, String> getCopytoMap() {
        //return StringUtils.restoreMap(prop.getProperty(COPYTO_TARGET_TO_SOURCE_MAP_LIST, ""));
        return getMap(COPYTO_TARGET_TO_SOURCE_MAP_LIST);
    }

    /**
     * @return the schemeSet
     */
    public Set<String> getSchemeSet() {
        //return StringUtils.restoreSet(prop.getProperty(SUBJEC_SCHEME_LIST, ""));
        return getSet(SUBJEC_SCHEME_LIST);
        
    }

    /**
     * Get input file
     * 
     * @return input file path relative to input directory
     */
    public String getInputMap() {
        return getProperty(INPUT_DITAMAP);
    }
    
    /**
     * Get reference list.
     * 
     * @return reference list
     */
    public Set<String> getReferenceList() {
        final Set<String> refList = new HashSet<String>();
        refList.addAll(getSet(FULL_DITAMAP_TOPIC_LIST));
        refList.addAll(getSet(CONREF_TARGET_LIST));
        refList.addAll(getSet(COPYTO_SOURCE_LIST));
        return refList;
    }

    /**
     * Get input directory.
     * 
     * @return absolute input directory path 
     */
    public String getInputDir() {
        return getProperty(INPUT_DIR);
    }

    /**
     * Get all file info objects
     * 
     * @return map of file info objects
     */
    public Map<String, FileInfo> getFileInfo() {
        return Collections.unmodifiableMap(files);
    }
    
    /**
     * Add a collection of file info objects
     * 
     * @param fs file info objects
     */
    public void addAll(final Collection<FileInfo> fs) {
    	for (final FileInfo f: fs) {
    		files.put(f.file, f);
    	}
    }
    
    // Utility methods
    
    /**
     * Write list file.
     * 
     * @param prop property name
     * @throws IOException if writing fails
     */
    public void writeList(final String prop) throws IOException {
        final String filename = prop.equals(INPUT_DITAMAP)
                                ? INPUT_DITAMAP_LIST_FILE
                                : prop.substring(0, prop.lastIndexOf("list"))+ ".list";
        writeList(prop, filename);
    }
    
    /**
     * Write list file.
     * 
     * @param prop property name
     * @param filename list file name
     * @throws IOException if writing fails
     */
    private void writeList(final String prop, final String filename) throws IOException {
        final File listFile = new File(tempDir, filename);
        BufferedWriter topicWriter = null;
        try {
            topicWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(listFile)));
            final Set<String> topics = getSet(prop);
            for (final Iterator<String> i = topics.iterator(); i.hasNext();) {
                topicWriter.write(i.next());
                if (i.hasNext()) {
                    topicWriter.write("\n");
                }
            }
            topicWriter.flush();
        } finally {
            if (topicWriter != null) {
                topicWriter.close();
            }
        }
    }
    
    /**
     * File info object.
     * 
     * TODO: make immutable
     */
    public static final class FileInfo {
        
    	public final String file;
    	
    	public String format;
        public boolean hasConref;
        public boolean isChunked;
        public boolean hasLink;
        public boolean isResourceOnly;
        public boolean isTarget;
        public boolean isConrefTarget;
        public boolean isNonConrefTarget;
        public boolean isConrefPush;
        public boolean hasKeyref;
        public boolean hasCoderef;
        
        FileInfo(final String file) {
            this.file = file;
        }

//        private FileInfo(final String file, final String format,
//                         final boolean hasConref,
//                         final boolean isChunked,
//                         final boolean hasLink,
//                         final boolean isResourceOnly,
//                         final boolean isTarget,
//                         final boolean isConrefTarget,
//                         final boolean isNonConrefTarget,
//                         final boolean isConrefPush,
//                         final boolean hasKeyref,
//                         final boolean hasCoderef) {
//            this.file = file;
//            this.format = format;
//			this.hasConref = hasConref;
//			this.isChunked = isChunked;
//			this.hasLink = hasLink;
//			this.isResourceOnly = isResourceOnly;
//			this.isTarget = isTarget;
//			this.isConrefTarget = isConrefTarget;
//			this.isNonConrefTarget = isNonConrefTarget;
//			this.isConrefPush = isConrefPush;
//			this.hasKeyref = hasKeyref;
//			this.hasCoderef = hasCoderef;
//        }
//        
//        public String file() { return file; }
//        public String format() { return format; }
//        public boolean hasConref() { return hasConref; }
//        public boolean isChunked() { return isChunked; }
//        public boolean hasLink() { return hasLink; }
//        public boolean isResourceOnly() { return isResourceOnly; }
//        public boolean isTarget() { return isTarget; }
//        public boolean isConrefTarget() { return isConrefTarget; }
//        public boolean isNonConrefTarget() { return isNonConrefTarget; }
//        public boolean isConrefPush() { return isConrefPush; }
//        public boolean hasKeyref() { return hasKeyref; }
//        public boolean hasCoderef() { return hasCoderef; }
//        
//        public FileInfo withIsResourceOnly(final boolean hasConref) { return new FileInfo(file, format, hasConref, isChunked, hasLink, isResourceOnly, isTarget, isConrefTarget, isNonConrefTarget, isConrefPush, hasKeyref, hasCoderef); }
    
    }
    
}
