package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.dao.RedisCompanyKeywordsDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisCompanyKeywordsDaoImpl;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialResponseType;
import com.realtech.socialsurvey.compute.entities.TrieNode;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
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

    private RedisCompanyKeywordsDao redisCompanyKeywordsDao = new RedisCompanyKeywordsDaoImpl();

    @Override
    public void execute( Tuple input )
    {
        LOG.info( "Executing filter social post bolt" );
        SocialResponseObject<?> post = (SocialResponseObject<?>) input.getValueByField( "post" );
        SocialResponseType socialResponseType = (SocialResponseType) input.getValueByField( "type" );
        long companyId = input.getLongByField( "companyId" );

        if ( post != null && post.getText() != null ) {
            String text = post.getText();
            List<String> foundKeyWords = null;
            TrieNode root = getTrieForCompany( companyId );
            foundKeyWords = findPhrases( root, text );
            if ( !foundKeyWords.isEmpty() ) {
                post.setFoundKeywords( foundKeyWords );
                post.setFlagged( Boolean.TRUE );
            }
            LOG.debug( "Emitting tuple with companyId {}, post {}, foundKeyWords {}.", companyId, post, foundKeyWords );
        }
        _collector.emit( input, Arrays.asList( companyId, post, socialResponseType ) );
        _collector.ack( input );
    }


    /**
     * Method to check and construct trie data structure for company by company id.
     * @param companyId
     * @return
     */
    private synchronized TrieNode getTrieForCompany( long companyId )
    {
        LOG.debug( "Inside method getTrieForCompany for company {}", companyId );
        TrieNode node = companyTrie.get( companyId );
        if ( node == null || isKeywordsUpdated( companyId, node ) ) {
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
    private boolean isKeywordsUpdated( long companyIden, TrieNode rootNode )
    {
        long modifiedOn = redisCompanyKeywordsDao.getKeywordModifiedOn( companyIden );
        return ( modifiedOn > rootNode.getModifiedOn() ) ? true : false;
    }


    /**
     * Method to get trieNode for company id.
     * @param companyIden
     * @return
     */
    private TrieNode getCompanyKeywordsAndConstructTrie( long companyIden )
    {
        LOG.debug( "Inside getCompanyKeywordsAndConstructTrie method. companyId: {}", companyIden );
        List<Keyword> keywordListResponse = redisCompanyKeywordsDao.getCompanyKeywordsForCompanyId( companyIden );
        long keywordModifiedOn = redisCompanyKeywordsDao.getKeywordModifiedOn( companyIden );

        if ( keywordListResponse != null && !keywordListResponse.isEmpty() ) {
            companyTrie.put( companyIden, new TrieNode() );
            for ( Keyword keyword : keywordListResponse ) {
                addPhrase( companyTrie.get( companyIden ), keyword.getPhrase(), keyword.getId() );
            }
        }
        if ( keywordModifiedOn != 0L ) {
            companyTrie.get( companyIden ).setModifiedOn( keywordModifiedOn );
        } else {
            companyTrie.get( companyIden ).setModifiedOn( System.currentTimeMillis() );
        }

        return companyTrie.get( companyIden );
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post", "type" ) );
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
        TrieNode node = root;
        // break phrase into words
        String[] words = phrase.split( WORD_SEPARATOR );

        // start traversal at root
        for ( int i = 0; i < words.length; ++i ) {

            if ( node.getChildren() == null ) {
                node.setChildren( new HashMap<String, TrieNode>() );
            }

            if ( !node.getChildren().containsKey( words[i] ) ) {
                node.getChildren().put( words[i], new TrieNode() );
            }

            node = node.getChildren().get( words[i] );

            if ( i == words.length - 1 ) {
                node.setPhraseId( phraseId );
            }
        }
        LOG.debug( "End of addPhrase method" );
    }


    private List<String> findPhrases( TrieNode root, String textBody )
    {
        LOG.debug( "Inside findPhrase method." );
        TrieNode node = root;

        List<String> foundPhrases = new ArrayList<>();

        if ( StringUtils.isEmpty( textBody ) ) {
            return foundPhrases;
        }

        String[] words = textBody.split( WORD_SEPARATOR );

        StringBuilder phraseBuffer = new StringBuilder();
        for ( int i = 0; i < words.length; ) {
            if ( node.getChildren() != null && node.getChildren().containsKey( words[i] ) ) {
                // move trie pointer forward
                node = node.getChildren().get( words[i] );
                phraseBuffer.append( words[i] + SPACE_SEPARATOR );
                if ( node.getPhraseId() != null ) {
                    String phrase = phraseBuffer.toString().trim();
                    foundPhrases.add( phrase );
                    LOG.trace( "matched at {}", phrase );
                }
                ++i;
            } else {
                phraseBuffer = new StringBuilder();
                if ( node == root ) {
                    ++i;
                } else {

                    node = root;
                }
            }
        }

        if ( node.getPhraseId() != null ) {
            String phrase = phraseBuffer.toString().trim();
            foundPhrases.add( phrase );
            LOG.trace( "end matching {}", phrase );
        }

        LOG.debug( "End of findPhrase method, foundkeywords:{}", foundPhrases );
        return foundPhrases;
    }

}
