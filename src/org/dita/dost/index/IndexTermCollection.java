/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for 
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2005 All Rights Reserved.
 */
package org.dita.dost.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.DITAOTJavaLogger;
import org.dita.dost.module.Content;
import org.dita.dost.module.ContentImpl;
import org.dita.dost.pipeline.PipelineHashIO;
import org.dita.dost.util.Constants;
import org.dita.dost.writer.AbstractExtendDitaWriter;
import org.dita.dost.writer.AbstractWriter;
import org.dita.dost.writer.CHMIndexWriter;
import org.dita.dost.writer.EclipseIndexWriter;
import org.dita.dost.writer.IDitaTranstypeIndexWriter;
import org.dita.dost.writer.JavaHelpIndexWriter;

/**
 * This class is a collection of index term.
 * 
 * @version 1.0 2005-05-18
 * 
 * @author Wu, Zhi Qiang
 */
public class IndexTermCollection {
	/** The collection of index terms. */
	private static IndexTermCollection collection = null;
	/** The list of all index term. */
	private List<IndexTerm> termList = new ArrayList<IndexTerm>(Constants.INT_16);

	/** The type of index term. */
	private String indexType = null;
	
	/** The type of index class. */
	private String indexClass = null;

	/** The output file name of index term without extension. */
	private String outputFileRoot = null;
	/** The logger. */
	private DITAOTJavaLogger javaLogger = null;
	
	//RFE 2987769 Eclipse index-see
	/* Parameters passed in from ANT module */
	private PipelineHashIO pipelineHashIO = null;

	/**
	 * Private constructor used to forbid instance.
	 */
	private IndexTermCollection() {
		javaLogger = new DITAOTJavaLogger();
	}
	
	/**
	 * The only interface to access IndexTermCollection instance.
	 * @return Singleton IndexTermCollection instance
	 * @author Marshall
	 */
	public static synchronized IndexTermCollection getInstantce(){
		if(collection == null){
			collection = new IndexTermCollection();
		}
		return collection;
	}
	
	/**
	 * The interface to clear the result in IndexTermCollection instance.
	 * @author Stephen
	 */
	public void clear(){
		termList.clear();
	}

	/**
	 * Get the index type.
	 * 
	 * @return index type
	 */
	public String getIndexType() {
		return this.indexType;
	}

	/**
	 * Set the index type.
	 * 
	 * @param type The indexType to set.
	 */
	public void setIndexType(String type) {
		this.indexType = type;
	}
	/**
	 * get index class.
	 * @return index class
	 */
	public String getIndexClass() {
		return indexClass;
	}
	/**
	 * set index class.
	 * @param indexClass index class
	 */
	public void setIndexClass(String indexClass) {
		this.indexClass = indexClass;
	}
	
	
	/**
	 * All a new term into the collection.
	 * 
	 * @param term index term
	 */
	public void addTerm(IndexTerm term) {
		int i = 0;
		int termNum = termList.size();

		for (; i < termNum; i++) {
			IndexTerm indexTerm = (IndexTerm) termList.get(i);
			if (indexTerm.equals(term)) {
				return;
			}
			
			// Add targets when same term name and same term key
			if (indexTerm.getTermFullName().equals(term.getTermFullName())
					&& indexTerm.getTermKey().equals(term.getTermKey())) {
				indexTerm.addTargets(term.getTargetList());
				indexTerm.addSubTerms(term.getSubTerms());
				break;
			}
		}

		if (i == termNum) {
			termList.add(term);
		}
	}

	/**
	 * Get all the term list from the collection.
	 * 
	 * @return term list
	 */
	public List<IndexTerm> getTermList() {
		return termList;
	}

	/**
	 * Sort term list extracted from dita files base on Locale.
	 */
	public void sort() {
		int termListSize = termList.size();
		if (IndexTerm.getTermLocale() == null ||
				IndexTerm.getTermLocale().getLanguage().trim().length()==0) {
			IndexTerm.setTermLocale(new Locale(Constants.LANGUAGE_EN,
					Constants.COUNTRY_US));
		}

		/*
		 * Sort all the terms recursively
		 */
		for (int i = 0; i < termListSize; i++) {
			IndexTerm term = termList.get(i);
			term.sortSubTerms();
		}

		Collections.sort(termList);
	}

	/**
	 * Output index terms into index file.
	 * 
	 * @throws DITAOTException exception
	 */
	public void outputTerms() throws DITAOTException {
		StringBuffer buff = new StringBuffer(this.outputFileRoot);
		AbstractWriter abstractWriter = null;
		IDitaTranstypeIndexWriter indexWriter = null;
		Content content = new ContentImpl();
		
		if (indexClass != null && indexClass.length() > 0) {
			//Instantiate the class value 
			Class<?> anIndexClass = null;
			try {
				anIndexClass = Class.forName( indexClass );
				abstractWriter = (AbstractWriter) anIndexClass.newInstance();
				indexWriter = (IDitaTranstypeIndexWriter)anIndexClass.newInstance();
				
				//RFE 2987769 Eclipse index-see
				try{
					
					((AbstractExtendDitaWriter) abstractWriter).setPipelineHashIO(this.getPipelineHashIO());
		
				}catch (ClassCastException e){
					javaLogger.logInfo(e.getMessage());
					javaLogger.logInfo(e.toString());
					e.printStackTrace();
					
				}
				
				
				buff = new StringBuffer(indexWriter.getIndexFileName(this.outputFileRoot));
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			

		} 
		//Fallback to the old way of doing things.
		else {

			if (Constants.INDEX_TYPE_HTMLHELP.equalsIgnoreCase(indexType)) {
				abstractWriter = new CHMIndexWriter();
				buff.append(".hhk");
			} else if (Constants.INDEX_TYPE_JAVAHELP
					.equalsIgnoreCase(indexType)) {
				abstractWriter = new JavaHelpIndexWriter();
				buff.append("_index.xml");
			} else if (Constants.INDEX_TYPE_ECLIPSEHELP
					.equalsIgnoreCase(indexType)) {
				abstractWriter = new EclipseIndexWriter();
				// We need to get rid of the ditamap or topic name in the URL
				// so we can create index.xml file for Eclipse plug-ins.
				// int filepath = buff.lastIndexOf("\\");
				File indexDir = new File(buff.toString()).getParentFile();
				// buff.delete(filepath, buff.length());
				((EclipseIndexWriter) abstractWriter).setFilePath(indexDir
						.getAbsolutePath());
				// buff.insert(filepath, "\\index.xml");
				buff = new StringBuffer(new File(indexDir, "index.xml")
						.getAbsolutePath());
			}
		}
		
		//if (!getTermList().isEmpty()){
		//Even if there is no term in the list create an empty index file
		//otherwise the compiler will report error.
			content.setCollection(this.getTermList());
			abstractWriter.setContent(content);
			abstractWriter.write(buff.toString());
		//}
	}

	/**
	 * Set the output file.
	 * @param fileRoot The outputFile to set.
	 */
	public void setOutputFileRoot(String fileRoot) {
		this.outputFileRoot = fileRoot;
	}

	//RFE 2987769 Eclipse index-see
	/**
	*  Get input parameters from ANT pipeline module.
    *  @return PipelineHashIO The hashmap containing some module parameters.
    */
	public PipelineHashIO getPipelineHashIO() {
		return pipelineHashIO;
	}

	/**
	 * Set the hashmap cotaining parameters from ANT pipeline module.
	 * @param hashIO The hashmap to set.
	 */
	public void setPipelineHashIO(PipelineHashIO hashIO) {
		this.pipelineHashIO = hashIO;
	}
	
	

}
