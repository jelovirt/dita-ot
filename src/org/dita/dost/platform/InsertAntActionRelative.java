/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for 
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2008 All Rights Reserved.
 */
package org.dita.dost.platform;

import org.dita.dost.util.Constants;
import org.dita.dost.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.dita.dost.util.FileUtils;
import java.io.File;

/**
 * InsertAntActionRelative inserts the children of the root element of an XML document
 * into a plugin extension point, rewriting relative file references so that they
 * are still correct in their new location.
 *
 * Attributes affected: import/@file
 * 
 * @author Deborah Pickett
 *
 */
public class InsertAntActionRelative extends InsertAction implements
		IAction {

	@Override
	public void startElement(final String uri, final String localName, final String qName,
			final Attributes attributes) throws SAXException {
		final AttributesImpl attrBuf = new AttributesImpl();

		final int attLen = attributes.getLength();
		for (int i = 0; i < attLen; i++){
			String value;
			if ("import".equals(localName) && "file".equals(attributes.getQName(i))
					&& !FileUtils.isAbsolutePath(attributes.getValue(i))) {
				// Rewrite file path to be local to its final resting place.
			    final File targetFile = new File(
			    		new File(currentFile).getParentFile(),
			    		attributes.getValue(i));
			    value = FileUtils.getRelativePathFromMap(
			    		paramTable.get(FileGenerator.PARAM_TEMPLATE),
			    		targetFile.toString());
			}
			else {
				value = attributes.getValue(i);
			}
			attrBuf.addAttribute(attributes.getURI(i), attributes.getLocalName(i),
  		             attributes.getQName(i), attributes.getType(i), value);
		}

		super.startElement(uri, localName, qName, attrBuf);
	}
}
