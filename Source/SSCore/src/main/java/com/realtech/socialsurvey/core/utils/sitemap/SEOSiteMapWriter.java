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
public class SEOSiteMapWriter {
	
	private static final Logger LOG = LoggerFactory.getLogger(SEOSiteMapWriter.class);

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
	private final String[] locationTypes = {"state","city","zipcode"};
	
	public SEOSiteMapWriter(String siteMapFileName, SitemapContentFecher contentFetcher){
		this.siteMapFileName = siteMapFileName;
		this.contentFetcher = contentFetcher;
	}
	
	
	public void writeSiteMap(){
		LOG.info("Creating sitemap file: "+siteMapFileName);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = null;
		try{
			LOG.trace("Creating sitemap file");
			writer = factory.createXMLStreamWriter(new FileWriter(siteMapFileName));
			// start the document
			writeHeader(writer, true);
			// write the xmls
			writeUrlSet(writer, true);
			// get the contents to check if there are documents that need to be written
			boolean continueLooping = false;
			for(String locationType : locationTypes) {
				List<SiteMapEntry> entries = contentFetcher.getInitialSEOContent(locationType);
				
				if(entries != null && entries.size() > 0){
					// iterating to set the content
					do {
						for(SiteMapEntry entry: entries){
							// start the url tag
							writeURLElement(writer, true);
							LOG.trace("Setting sitemap entry: "+entry.toString());
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
						
						continueLooping = contentFetcher.hasNext();
						if(continueLooping){
							entries.clear();
							entries = contentFetcher.nextSEOBatch(locationType);
						}
					} while(continueLooping && entries != null && !entries.isEmpty());
					
				}
				
			}
			// end the xmls
			writeUrlSet(writer, false);
			// end the document
			writeHeader(writer, false);
			
		}catch(XMLStreamException xse){
			LOG.error("XMLStreamException while creating "+siteMapFileName+". Reason: "+ xse + xse.getMessage());
			xse.printStackTrace();
			System.exit(1);
		}catch(IOException ioe){
			LOG.error("IOException while creating "+siteMapFileName+". Reason: " + ioe + ioe.getMessage());
			ioe.printStackTrace();
			System.exit(1);
		}catch(Exception ioe){
			LOG.error("Exception while creating "+siteMapFileName+". Reason: "+ ioe + ioe.getMessage());
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
		LOG.trace("Writing the header with isStart "+isStart);
		if(isStart){
			LOG.trace("Starting document");
			writer.writeStartDocument();
		}else{
			LOG.trace("Ending document");
			writer.writeEndDocument();
		}
	}
	
	private void writeUrlSet(XMLStreamWriter writer, boolean isStart) throws XMLStreamException{
		LOG.trace("Writing urlset with isStart "+isStart);
		if(isStart){
			LOG.trace("Starting xmlns element");
			writer.writeStartElement(KEY_URLSET);
			writer.writeAttribute(KEY_XMLNS, XMLNS_VAL);
		}else{
			LOG.trace("Ending xmlns element");
			writer.writeEndElement();
		}
	}
	
	private void writeElement(XMLStreamWriter writer, String elementName, String elementValue) throws XMLStreamException{
		LOG.trace("Writing "+elementName+" with value "+elementValue);
		writer.writeStartElement(elementName);
		writer.writeCharacters(elementValue);
		writer.writeEndElement();
	}
	
	private void writeURLElement(XMLStreamWriter writer, boolean isStart) throws XMLStreamException{
		LOG.trace("Writing url element with isStart "+isStart);
		if(isStart){
			LOG.trace("Starting url element");
			writer.writeStartElement(KEY_URL);
		}else{
			LOG.trace("Ending url element");
			writer.writeEndElement();
		}
	}
}
