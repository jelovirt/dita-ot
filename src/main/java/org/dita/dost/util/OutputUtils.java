/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2004, 2005 All Rights Reserved.
 */
package org.dita.dost.util;

import static org.dita.dost.util.Constants.*;

import java.io.File;
import java.net.URI;

/**
 * OutputUtils to control the output behavior.
 * @author wxzhang
 *
 */
public final class OutputUtils {

    public enum OutterControl {
        /** Fail behavior. */
        FAIL,
        /** Warn behavior. */
        WARN,
        /** Quiet behavior. */
        QUIET
    }

    public enum Generate {
        /** Not generate outer files. */
        NOT_GENERATEOUTTER(1),
        /** Generate outer files. */
        GENERATEOUTTER(2),
        /** Old solution. */
        OLDSOLUTION(3);

        public final int type;

        Generate(final int type) {
            this.type = type;
        }

        public static Generate get(final int type) {
            for (final Generate g: Generate.values()) {
                if (g.type == type) {
                    return g;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    private static Generate generatecopyouter = Generate.NOT_GENERATEOUTTER;//default:only generate&copy the non-overflowing files
    private boolean onlytopicinmap=false;//default:only the topic files will be resolved in the map
    private OutterControl outercontrol = OutterControl.WARN;
    /**Output Dir.*/
    private static File OutputDir=null;
    /** Input map file. */
    private File inputMapFile=null;
    private int uplevels = 0;
    /** Prefix path. Either an empty string or a path which ends in {@link java.io.File#separator File.separator}. */
    private String prefix = "";
    /** Absolute basedir for processing */
    private File baseInputDir;
    /** Flatten directory structure */
    private boolean flat = false;

    /**
     * Retrieve the outercontrol.
     * @return String outercontrol behavior
     *
     */
    public OutterControl getOutterControl(){
        return outercontrol;
    }

    /**
     * Set the outercontrol.
     * @param control control
     */
    public void setOutterControl(final String control){
        outercontrol = OutterControl.valueOf(control.toUpperCase());
    }

    /**
     * Retrieve the flag of onlytopicinmap.
     * @return boolean if only topic in map
     */
    public boolean getOnlyTopicInMap(){
        return onlytopicinmap;
    }

    /**
     * Set the onlytopicinmap.
     * @param flag onlytopicinmap flag
     */
    public void setOnlyTopicInMap(final String flag){
        if("true".equalsIgnoreCase(flag)){
            onlytopicinmap=true;
        }else{
            onlytopicinmap=false;
        }
    }

    /**
     * Retrieve the flag of generatecopyouter.
     * @return int generatecopyouter flag
     */
    public static Generate getGeneratecopyouter(){
        return generatecopyouter;
    }

    /**
     * Set the generatecopyouter.
     * @param flag generatecopyouter flag
     */
    public void setGeneratecopyouter(final String flag){
        generatecopyouter = Generate.get(Integer.parseInt(flag));
    }

    /**
     * Set file organization strategy.
     * 
     * @param fileOrganizationStrategy
     * @throws IllegalArgumentException if value not recognized
     */
    public void setFileOrganizationStrategy(final String fileOrganizationStrategy) {
    	if (fileOrganizationStrategy == null) {
    		flat = false;
    	} else if (fileOrganizationStrategy.equals("single-dir")) {
    		flat = true;
    	} else if (fileOrganizationStrategy.equals("as-authored")) {
    		flat = false;
    	} else {
    		throw new IllegalArgumentException("File organization strategy '" + fileOrganizationStrategy + "' not supported");
    	}
    } 
    
    /**
     * Get output dir.
     * @return absolute output dir
     */
    public static File getOutputDir(){
        return OutputDir;
    }
    /**
     * Set output dir.
     * @param outputDir absolute output dir
     */
    public void setOutputDir(final File outputDir){
        OutputDir=outputDir;
    }
    /**
     * Get input map path.
     * @return absolute input map path
     */
    public File getInput(){
        return inputMapFile;
    }
    /**
     * Set input map path.
     * @param inputMapDir absolute input map path
     */
    public void setInput(final File inputMapFile){
        this.inputMapFile = inputMapFile;
    }
    
    /**
     * get input base directory
     * 
     * @return absolute input base directory
     */
    public File getInputDir() {
    	return baseInputDir;
    }
    
    /**
     * Set input base directory
     * 
     * @param baseInputDir absolute input base directory
     */
    public void setInputDir(final File baseInputDir) {
    	this.baseInputDir = baseInputDir;
    }
    
    /**
     * Get uplevels
     * 
     * @return number of directories to walk up
     */
    public int getUplevels() {
    	return uplevels;
    }
    
    /**
     * Get path adjustment prefix.
     * 
     * @return path adjustment prefix
     */
    public String getPrefix() {
    	return prefix;
    }
    
    /**
     * Update uplevels if needed. If the parameter contains a {@link org.dita.dost.util.Constants#STICK STICK}, it and
     * anything following it is removed.
     * 
     * @param file file path
     */
    public void updateUplevels(final String file) {
        String f = file;
        if (f.contains(STICK)) {
            f = f.substring(0, f.indexOf(STICK));
        }
        final int lastIndex = FileUtils.separatorsToUnix(FileUtils.normalize(f)).lastIndexOf("../");
        if (lastIndex != -1) {
            final int newUplevels = lastIndex / 3 + 1;
            if (newUplevels > uplevels) {
            	uplevels = newUplevels;
            }
        }
    }
    
    /**
     * Update base directory based on uplevels.
     */
    public void updateBaseDirectory() {
        for (int i = getUplevels(); i > 0; i--) {
            final File file = baseInputDir;
            baseInputDir = baseInputDir.getParentFile();
            prefix = file.getName() + File.separator + prefix;
        }
    }
        
    /**
     * Get output path.
     * 
     * @param inputFile input file path, relative to {@link org.dita.dost.util.Job#getInputDir() input directory}
     * @return output file in temporary directory
     */
    public File getOutputFile(final String inputFile) {
    	if (flat) {
    		final String f = inputFile.replace(UNIX_SEPARATOR, "_");
    		return new File(f);
    	} else {
    		return new File(inputFile);
    	}
    }

    /**
     * Get output file URI.
     * 
     * @param baseDir base directory
     * @param thisFile current file URI, relative to base director
     * @param thatFile target file URI, relative to current file
     * @return output URI to target file
     */
    public String getOutputURI(final File baseDir, final String thisFile, final String thatFile) {
    	if (flat) {
    		final URI t = new File(baseDir, "x").toURI();
    		final URI ths = t.resolve(thisFile);
	    	final URI tht = ths.resolve(thatFile);
	    	final String r = FileUtils.getRelativePath(t.toASCIIString(), tht.toASCIIString());
	    	return r.replace(URI_SEPARATOR, "_");
    	} else {
    		return thatFile;
//    		final URI ths = new File(baseDir, "x").toURI().resolve(thisFile);
//	    	final URI tht = ths.resolve(thatFile);
//	    	return FileUtils.getRelativePath(ths.toASCIIString(), tht.toASCIIString());
    	}
    }
    
}
