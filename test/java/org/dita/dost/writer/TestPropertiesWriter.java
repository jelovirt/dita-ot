/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for 
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2010 All Rights Reserved.
 */
package org.dita.dost.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.dita.dost.TestUtils;
import org.dita.dost.module.Content;
import org.dita.dost.module.ContentImpl;
import org.dita.dost.util.Constants;

import org.dita.dost.writer.PropertiesWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.dita.dost.exception.DITAOTException;


import static org.junit.Assert.assertEquals;

public class TestPropertiesWriter {
	
	private File tempDir;
	private final File resourceDir = new File("test-stub" + File.separator + "TestPropertiesWriter");
	
	private File outputFile;
	private File xmlDitalist;
	
	@Before
	public void setUp() throws IOException {
		tempDir = TestUtils.createTempDir(getClass());
		outputFile = new File(tempDir, Constants.FILE_NAME_DITA_LIST);
    	xmlDitalist = new File(tempDir, Constants.FILE_NAME_DITA_LIST_XML);
	}
	
    @Test
    public void testwrite() throws DITAOTException, FileNotFoundException, IOException {
    	final File inputfile = new File(resourceDir,Constants.FILE_NAME_EXPORT_XML);
    	final Properties prop = new Properties();
    	prop.loadFromXML(new FileInputStream (inputfile));
    	final Content content = new ContentImpl();
    	content.setValue(prop);
    	final PropertiesWriter propertieswriter = new PropertiesWriter();
		propertieswriter.setContent(content);		
		propertieswriter.write(outputFile.getAbsolutePath());
		propertieswriter.writeToXML(xmlDitalist.getAbsolutePath());
		
		final File ditalistfile=new File(tempDir, Constants.FILE_NAME_DITA_LIST);		
		final File compareditalistfile=new File(resourceDir, "compareofdita.list");
		final File ditalistpropertiesfile=new File(tempDir, Constants.FILE_NAME_DITA_LIST_XML);
		final File compareditalistpropertiesfile=new File(resourceDir,  "compareofdita.xml.properties");

        assertEquals(TestUtils.readFileToString(ditalistfile, true),
        			 TestUtils.readFileToString(compareditalistfile, true));

        assertEquals(TestUtils.readFileToString(ditalistpropertiesfile, true),
   			 		 TestUtils.readFileToString(compareditalistpropertiesfile, true));
    }
    
    @After
    public void tearDown() throws IOException {
    	TestUtils.forceDelete(tempDir);
    }
    
}
