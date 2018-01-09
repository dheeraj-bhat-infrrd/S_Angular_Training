package com.realtech.socialsurvey.compute.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Finds if a phrase is present 
 * @author nishit
 *
 */
public class PhraseMatcher
{

    private static final Logger LOG = LoggerFactory.getLogger( PhraseMatcher.class );


    private final PhraseTrieNode root;


    public PhraseMatcher( List<String> phrases )
    {
        root = new PhraseTrieNode();
        buildTrieNodes( phrases );
        LOG.debug( "Built Trie {}", root );
    }


    public PhraseTrieNode getRoot()
    {
        return root;
    }


    /**
     * Finds all the possible phrases in the given text.
     * @param text
     * @return
     */
    public List<String> findPhrasesLocationMap( String text )
    {
        if ( text == null || text.isEmpty() ) {
            LOG.error( "Passed text is blank or null" );
            return new ArrayList<>();
        }
        PhraseTrieNode current = root;
        String[] wordArray = text.split( "\\s+" );
        StringBuilder wordsInPhrase = new StringBuilder();
        List<String> capturedPhrases = new ArrayList<>();
        for ( String word : wordArray ) {
            PhraseTrieNode node = current.children.get( word );
            // The word is not there. Reset the the wordsInPhrase. Move current node to root and check the word in root.
            if ( node == null && wordsInPhrase.length() > 0 ) {
                wordsInPhrase = new StringBuilder();
            }
        }
        return capturedPhrases;
    }


    private class PhraseTrieNode
    {
        Map<String, PhraseTrieNode> children;
        boolean isEndOfPhrase;


        PhraseTrieNode()
        {
            children = new HashMap<>();
            isEndOfPhrase = false;
        }


        @Override
        public String toString()
        {
            return "Children: " + children.toString() + "\t isEndOfPhrase: " + isEndOfPhrase;
        }


    }


    // Builds a trie data structure
    private void buildTrieNodes( List<String> phrases )
    {
        LOG.debug( "Building trie nodes for phrases: {}", phrases );
        for ( String phrase : phrases ) {
            insertPhrase( phrase );
        }
    }


    private boolean insertPhrase( String phrase )
    {
        LOG.debug( "Inserting phrase: {}", phrase );
        PhraseTrieNode current = root;
        String[] words = phrase.split( "\\s+" );
        for ( String word : words ) {
            PhraseTrieNode node = current.children.get( word );
            if ( node == null ) {
                node = new PhraseTrieNode();
                current.children.put( word, node );
            }
            current = node;
        }
        current.isEndOfPhrase = true;
        return true;
    }
}

