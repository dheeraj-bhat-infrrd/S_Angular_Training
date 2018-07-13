package com.realtech.socialsurvey.core.utils;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author manish
 *
 */
@Component
public class JsoupHtmlToTextUtils
{
    private static final Logger LOG = LoggerFactory.getLogger( JsoupHtmlToTextUtils.class );
    private static final String GMAIL_REPLY_REGEX = "\\n*On.*(\\r*\\s*\\n*).*? wrote:";
    private static final String OUTLOOK_EMAIL_REGEX = "From:.*(\\s*\\n*)Sent:.*(\\s*\\n*)To:.*(\\s*\\n*)";
    private static final String REPLY_ABOVE_THIS_LINE = "*** Reply above this line ***";
    
    /**
     * Method to extract reply from email chain
     * @param mailBodyHtml
     * @return
     */
    public String extractReplyFromHtml( String mailBodyHtml )
    {
        Document doc = Jsoup.parse( mailBodyHtml );
        String mailBodyText = getPlainText( doc );
        
        if(mailBodyText.indexOf( REPLY_ABOVE_THIS_LINE ) >=0 ) {
            mailBodyText = mailBodyText.substring( 0, mailBodyText.indexOf( REPLY_ABOVE_THIS_LINE ) );
        }
        
        String[] replyTextArr = mailBodyText.split( OUTLOOK_EMAIL_REGEX );
        if ( replyTextArr.length > 1 ) {
            return replyTextArr[0];
        }
        
        replyTextArr = mailBodyText.split( GMAIL_REPLY_REGEX );
        if ( replyTextArr.length > 1 ) {
            return replyTextArr[0];
        }
        
        return mailBodyText;
    }
    
    /**
     * Format an Element to plain-text
     * @param element the root element to format
     * @return formatted text
     */
    public String getPlainText( Element element )
    {
        LOG.debug( "Inside getPlainText" );
        FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor traversor = new NodeTraversor( formatter );
        traversor.traverse( element ); // walk the DOM, and call .head() and .tail() for each node

        return formatter.toString();
    }


    // the formatting rules, implemented in a breadth-first DOM traverse
    private class FormattingVisitor implements NodeVisitor
    {
        private static final int MAX_WIDTH = 200;
        private int width = 0;
        private StringBuilder accum = new StringBuilder(); // holds the accumulated text

        // hit when the node is first seen
        public void head( Node node, int depth )
        {
            String name = node.nodeName();
            if ( node instanceof TextNode )
                append( ( (TextNode) node ).text() ); // TextNodes carry all user-readable text in the DOM.
            else if ( name.equals( "li" ) )
                append( "\n * " );
            else if ( name.equals( "dt" ) )
                append( "  " );
            else if ( StringUtil.in( name, "p", "h1", "h2", "h3", "h4", "h5", "tr" ) )
                append( "\n" );
            else if(name.equals( "div" ) && node.childNodeSize() ==1 && node.childNodes().get( 0 ).nodeName().equals( "#text" )) {
                append( "\n" );
            }
        }


        // hit when all of the node's children (if any) have been visited
        public void tail( Node node, int depth )
        {
            String name = node.nodeName();
            if ( StringUtil.in( name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5", "hr" ) )
                append( "\n" );
            else if ( name.equals( "a" ) )
                append( String.format( " <%s>", node.absUrl( "href" ) ) );
        }


        // appends text to the string builder with a simple word wrap method
        private void append( String text )
        {
            if ( text.startsWith( "\n" ) )
                width = 0; // reset counter if starts with a newline. only from formats above, not in natural text
            if ( text.equals( " " )
                && ( accum.length() == 0 || StringUtil.in( accum.substring( accum.length() - 1 ), " ", "\n" ) ) )
                return; // don't accumulateL long runs of empty spaces

            if ( text.length() + width > MAX_WIDTH ) { // won't fit, needs to wrap
                String words[] = text.split( "\\s+" );
                for ( int i = 0; i < words.length; i++ ) {
                    String word = words[i];
                    boolean last = i == words.length - 1;
                    if ( !last ) // insert a space if not the last word
                        word = word + " ";
                    if ( word.length() + width > MAX_WIDTH ) { // wrap and reset counter
                        accum.append( "\n" ).append( word );
                        width = word.length();
                    } else {
                        accum.append( word );
                        width += word.length();
                    }
                }
            } else { // fits as is, without need to wrap text
                accum.append( text );
                width += text.length();
            }
        }

        @Override
        public String toString()
        {
            return accum.toString();
        }
    }
}