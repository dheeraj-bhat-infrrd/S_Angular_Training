package com.realtech.socialsurvey.core.utils.sitemap;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.entities.SiteMapEntry;

/**
 * Class to write into sitemap.xml
 *
 */
public class SiteMapWriter {
	
	private static final Logger LOG = LoggerFactory.getLogger(SiteMapWriter.class);

	private String siteMapFileName;
	private SitemapContentFecher contentFetcher;
	
	// Set the elements' names for the file
	private static final String KEY_URLSET = "urlset";
	private static final String KEY_URL = "url";
	private static final String KEY_LOC = "loc";
	private static final String KEY_LASTMOD = "lastmod";
	private static final String KEY_CHANGEFREQ = "changefreq";
	private static final String KEY_PRIORITY = "priority";
	private static final String KEY_XMLNS = "xmlns";
	private static final String XMLNS_VAL = "http://www.sitemaps.org/schemas/sitemap/0.9";
	
	public SiteMapWriter(String siteMapFileName, SitemapContentFecher contentFetcher){
		this.siteMapFileName = siteMapFileName;
		this.contentFetcher = contentFetcher;
	}
	
	
	public void writeSiteMap(){
		LOG.info("Creating sitemap file: "+siteMapFileName);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = null;
		try{
			// get the contents to check if there are documents that need to be written
			List<SiteMapEntry> entries = contentFetcher.getInitialContent();
			boolean continueLooping = true;
			if(entries.size() > 0){
				LOG.debug("Creating sitemap file");
				writer = factory.createXMLStreamWriter(new FileWriter(siteMapFileName));
				// start the document
				writeHeader(writer, true);
				// write the xmls
				writeUrlSet(writer, true);
				do{
					if(entries != null){
						// iterating to set the content
						for(SiteMapEntry entry: entries){
							// start the url tag
							writeURLElement(writer, true);
							LOG.debug("Setting sitemap entry: "+entry.toString());
							// Write the location
							if(entry.getLocation() != null && !entry.getLocation().isEmpty()){
								writeElement(writer, KEY_LOC, entry.getLocation());
							}
							// write last modified
							if(entry.getLastModifiedDate() != null && !entry.getLastModifiedDate().isEmpty()){
								writeElement(writer, KEY_LASTMOD, entry.getLastModifiedDate());
							}
							// write change frequency
							if(entry.getChangeFrequency() != null && !entry.getChangeFrequency().isEmpty()){
								writeElement(writer, KEY_CHANGEFREQ, entry.getChangeFrequency());
							}
							// write change priority
							if(entry.getPriority() > 0f){
								writeElement(writer, KEY_PRIORITY, String.valueOf(entry.getPriority()));
							}
							// end the url tag
							writeURLElement(writer, false);
						}
					}
					continueLooping = contentFetcher.hasNext();
					if(continueLooping){
						entries = contentFetcher.nextBatch();
					}
				}while(continueLooping);
				// end the xmls
				writeUrlSet(writer, false);
				// end the document
				writeHeader(writer, false);
			}
		}catch(XMLStreamException xse){
			LOG.error("Exception while creating "+siteMapFileName+". Reason: "+xse.getMessage());
			xse.printStackTrace();
			System.exit(1);
		}catch(IOException ioe){
			LOG.error("Exception while creating "+siteMapFileName+". Reason: "+ioe.getMessage());
			ioe.printStackTrace();
			System.exit(1);
		}finally{
			if(writer != null){
				try {
					writer.flush();
					writer.close();
				}
				catch (XMLStreamException e) {
					LOG.error("Error while flushing the stream while writing "+siteMapFileName);
					e.printStackTrace();
				}
			    
			}
		}
	}
	
	private void writeHeader(XMLStreamWriter writer, boolean isStart) throws XMLStreamException{
		LOG.debug("Writing the header with isStart "+isStart);
		if(isStart){
			LOG.debug("Starting document");
			writer.writeStartDocument();
		}else{
			LOG.debug("Ending document");
			writer.writeEndDocument();
		}
	}
	
	private void writeUrlSet(XMLStreamWriter writer, boolean isStart) throws XMLStreamException{
		LOG.debug("Writing urlset with isStart "+isStart);
		if(isStart){
			LOG.debug("Starting xmlns element");
			writer.writeStartElement(KEY_URLSET);
			writer.writeAttribute(KEY_XMLNS, XMLNS_VAL);
		}else{
			LOG.debug("Ending xmlns element");
			writer.writeEndElement();
		}
	}
	
	private void writeElement(XMLStreamWriter writer, String elementName, String elementValue) throws XMLStreamException{
		LOG.debug("Writing "+elementName+" with value "+elementValue);
		writer.writeStartElement(elementName);
		writer.writeCharacters(elementValue);
		writer.writeEndElement();
	}
	
	private void writeURLElement(XMLStreamWriter writer, boolean isStart) throws XMLStreamException{
		LOG.debug("Writing url element with isStart "+isStart);
		if(isStart){
			LOG.debug("Starting url element");
			writer.writeStartElement(KEY_URL);
		}else{
			LOG.debug("Ending url element");
			writer.writeEndElement();
		}
	}
}
