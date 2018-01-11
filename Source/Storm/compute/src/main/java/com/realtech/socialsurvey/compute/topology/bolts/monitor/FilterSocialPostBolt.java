package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialPost;
import com.realtech.socialsurvey.compute.entities.TrieNode;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;


/**
 * @author manish
 *
 */
public class FilterSocialPostBolt extends BaseComputeBolt
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( FilterSocialPostBolt.class );

    private static final String WORD_SEPARATOR = "\\W+";
    private static final String SPACE_SEPARATOR = " ";

    private Map<Long, TrieNode> companyTrie = new HashMap<>();


    @Override
    public void execute( Tuple input )
    {
        LOG.info( "Executing filter social post bolt" );
        SocialPost post = (SocialPost) input.getValueByField( "post" );
        long companyId = input.getLongByField( "companyId" );
        List<String> foundKeyWords = null;
        if ( post != null ) {
            TrieNode root = getTrieForCompany( companyId );
            foundKeyWords = findPhrases( root, post.getMessage() );
            post.setFoundKeywords( foundKeyWords );
        }
        LOG.debug( "Emitting tuple with companyId {}, post {}, foundKeyWords {}.", companyId, post, foundKeyWords );
        _collector.emit( input, Arrays.asList( companyId, post ) );
        _collector.ack( input );
    }


    /**
     * Method to check and construct trie data structure for company by company id.
     * @param companyId
     * @return
     */
    private synchronized TrieNode getTrieForCompany( long companyId )
    {
        LOG.debug( "Inside method getTrieForCompany for company {}",companyId);
        TrieNode node = companyTrie.get( companyId );
        if ( node == null || isKeywordsUpdated( companyId ) ) {
            node = getCompanyKeywordsAndConstructTrie( companyId );
            companyTrie.put( companyId, node );
        }
        return node;
    }


    /**
     * Method to check if keyword is updated for company id
     * @param companyIden
     * @return true - keyword updated
     */
    private boolean isKeywordsUpdated( long companyIden )
    {
        // TODO write logic to check for keyword update for company by company id.
        return true;
    }


    /**
     * Method to get trieNode for company id.
     * @param companyIden
     * @return
     */
    private TrieNode getCompanyKeywordsAndConstructTrie( long companyIden )
    {
        LOG.debug( "Inside getCompanyKeywordsAndConstructTrie method. companyId: {}", companyIden );
        Optional<List<Keyword>> keywordListResponse = SSAPIOperations.getInstance().getKeywordsForCompany( companyIden );

        if ( keywordListResponse.isPresent() ) {
            companyTrie.put( companyIden, new TrieNode() );
            for ( Keyword keyword : keywordListResponse.get() ) {
                addPhrase( companyTrie.get( companyIden ), keyword.getPhrase(), keyword.getId() );
            }
        }
        return companyTrie.get( companyIden );
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post" ) );
    }


    /**
     * Method to add phrase in trie data structure
     * @param root : root of tire tree
     * @param phrase : phrase to insert in trie tree
     * @param phraseId : phraseId is unique id(incremented by one)
     */
    private void addPhrase( TrieNode root, String phrase, String phraseId )
    {
        LOG.debug( "Inside addPhrase method" );
        // a pointer to traverse the trie without damaging
        // the original reference
        TrieNode node = root;
        // break phrase into words
        String[] words = phrase.split( WORD_SEPARATOR );

        // start traversal at root
        for ( int i = 0; i < words.length; ++i ) {
            // if the current word does not exist as a child
            // to current node, add it

            if ( node.getChildren() == null ) {
                node.setChildren( new HashMap<String, TrieNode>() );
            }

            if ( !node.getChildren().containsKey( words[i] ) ) {
                node.getChildren().put( words[i], new TrieNode() );
            }

            // move traversal pointer to current word
            node = node.getChildren().get( words[i] );

            // if current word is the last one, mark it with
            // phrase Id
            if ( i == words.length - 1 ) {
                node.setPhraseId( phraseId );
            }
        }
        LOG.debug( "End of addPhrase method" );
    }


    private List<String> findPhrases( TrieNode root, String textBody )
    {
        LOG.debug( "Inside findPhrase method." );
        // a pointer to traverse the trie without damaging
        // the original reference
        TrieNode node = root;

        // a list of found ids
        List<String> foundPhrases = new ArrayList<>();

        // break text body into words
        String[] words = textBody.split( WORD_SEPARATOR );

        StringBuilder phraseBuffer = new StringBuilder();
        // starting traversal at trie root and first
        // word in text body
        for ( int i = 0; i < words.length; ) {
            // if current node has current word as a child
            // move both node and words pointer forward
            if ( node.getChildren() != null && node.getChildren().containsKey( words[i] ) ) {
                // move trie pointer forward
                node = node.getChildren().get( words[i] );
                phraseBuffer.append( words[i] + SPACE_SEPARATOR );
                // if there is a phrase Id, then the previous
                // sequence of words matched a phrase, add Id to
                // found list
                if ( node.getPhraseId() != null ) {
                    String phrase = phraseBuffer.toString().trim();
                    foundPhrases.add( phrase );
                    LOG.trace( "matched at {}", phrase );
                }
                ++i;
            } else {
                // current node does not have current
                // word in its children
                phraseBuffer = new StringBuilder();
                if ( node == root ) {
                    // if trie pointer is already at root, increment
                    // words pointer
                    ++i;
                } else {
                    // if not, leave words pointer at current word
                    // and return trie pointer to root
                    node = root;
                }
            }
        }

        // one case remains, word pointer as reached the end
        // and the loop is over but the trie pointer is pointing to
        // a phrase Id
        if ( node.getPhraseId() != null ) {
            String phrase = phraseBuffer.toString().trim();
            foundPhrases.add( phrase );
            LOG.trace( "end matching {}", phrase );
        }

        LOG.debug( "End of findPhrase method, foundkeywords:{}", foundPhrases );
        return foundPhrases;
    }

}
