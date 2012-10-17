/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2010 All Rights Reserved.
 */
package org.dita.dost.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.dita.dost.util.OutputUtils;
public class TestOutputUtils {
    public static OutputUtils outpututils;
    @Test
    public  void testsetoutercontrol()
    {
        final OutputUtils outputUtils = new OutputUtils();
        assertEquals(OutputUtils.OutterControl.WARN, outputUtils.getOutterControl());
        outputUtils.setOutterControl("FAIL");
        assertEquals(OutputUtils.OutterControl.FAIL, outputUtils.getOutterControl());
        outputUtils.setOutterControl("WARN");
        assertEquals(OutputUtils.OutterControl.WARN ,outputUtils.getOutterControl());
        outputUtils.setOutterControl("QUIET");
        assertEquals(OutputUtils.OutterControl.QUIET, outputUtils.getOutterControl());
        try {
            outputUtils.setOutterControl(null);
            fail();
        } catch (final NullPointerException e) {}
    }


    @Test
    public void testsetonlytopicinmap()
    {
        final OutputUtils outputUtils = new OutputUtils();
        outputUtils.setOnlyTopicInMap(null);
        assertEquals(false,outputUtils.getOnlyTopicInMap());

        outputUtils.setOnlyTopicInMap("false");
        assertEquals(false,outputUtils.getOnlyTopicInMap());
        outputUtils.setOnlyTopicInMap("true");
        assertEquals(true,outputUtils.getOnlyTopicInMap());

    }

    @Test
    public void testsetgeneratecopyouter()
    {
        final OutputUtils outputUtils = new OutputUtils();
        assertEquals(OutputUtils.Generate.NOT_GENERATEOUTTER, outputUtils.getGeneratecopyouter());
        outputUtils.setGeneratecopyouter("1");
        assertEquals(OutputUtils.Generate.NOT_GENERATEOUTTER, outputUtils.getGeneratecopyouter());
        outputUtils.setGeneratecopyouter("2");
        assertEquals(OutputUtils.Generate.GENERATEOUTTER, outputUtils.getGeneratecopyouter());
        outputUtils.setGeneratecopyouter("3");
        assertEquals(OutputUtils.Generate.OLDSOLUTION, outputUtils.getGeneratecopyouter());
        try {
            outputUtils.setGeneratecopyouter(null);
            fail();
        } catch (final NumberFormatException e) {}
    }
    
    @Test
    public void testGetOutputURISingleDir() {
    	final OutputUtils o = new OutputUtils();
    	o.setFileOrganizationStrategy("single-dir");
    	final File baseDir = new File("/temp/flatten");
    	assertEquals("sub-file.dita", o.getOutputURI(baseDir, "space-in-name.dita", "sub-file.dita"));
    	assertEquals("sub-folder_sub-file.dita", o.getOutputURI(baseDir, "space-in-name.dita", "sub-folder/sub-file.dita"));
    	assertEquals("space-in-name.dita", o.getOutputURI(baseDir, "sub-folder/sub-file.dita", "../space-in-name.dita"));
    	assertEquals("sub-folder_space-in-name.dita", o.getOutputURI(baseDir, "sub-folder/sub-file.dita", "space-in-name.dita"));
    }
    
    @Test
    public void testGetOutputURIAsAuthored() {
    	final OutputUtils o = new OutputUtils();
    	o.setFileOrganizationStrategy("as-authored");
    	final File baseDir = new File("/temp/flatten");
    	assertEquals("sub-file.dita", o.getOutputURI(baseDir, "space-in-name.dita", "sub-file.dita"));
    	assertEquals("sub-folder/sub-file.dita", o.getOutputURI(baseDir, "space-in-name.dita", "sub-folder/sub-file.dita"));
    	assertEquals("../space-in-name.dita", o.getOutputURI(baseDir, "sub-folder/sub-file.dita", "../space-in-name.dita"));
    	assertEquals("space-in-name.dita", o.getOutputURI(baseDir, "sub-folder/sub-file.dita", "space-in-name.dita"));
    }

}
