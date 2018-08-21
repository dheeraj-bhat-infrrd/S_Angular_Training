package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import com.realtech.socialsurvey.compute.dao.RedisCompanyKeywordsDao;
import com.realtech.socialsurvey.compute.dao.RedisTrustedSourcesDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisCompanyKeywordsDaoImpl;
import com.realtech.socialsurvey.compute.dao.impl.RedisTrustedSourcesDaoImpl;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialMonitorTrustedSource;
import com.realtech.socialsurvey.compute.entities.TrieNode;
import com.realtech.socialsurvey.compute.entities.response.ActionHistory;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.enums.ActionHistoryType;
import com.realtech.socialsurvey.compute.enums.SocialFeedStatus;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;


/**
 * @author manish
 *
 */
public class FilterSocialPostBolt extends BaseComputeBoltWithAck
{
    private static final String HIGHLIGHT_END = "</mark>";
    private static final String HIGHLIGHT_START = "<mark>";
    private static final String IGNORE_CASE_REGEX_PREFIX = "(?i)";
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( FilterSocialPostBolt.class );

    //regex for matching all non word characters except (%)
    private static final String WORD_SEPARATOR = "[^\\w(%)]|_";
    private static final String SPACE_SEPARATOR = " ";
    private static final String OWNERNAME_SYSTEM = "System";


    private Map<Long, TrieNode> companyTrie = new HashMap<>();

    private  RedisCompanyKeywordsDao redisCompanyKeywordsDao = new RedisCompanyKeywordsDaoImpl();

    private  RedisTrustedSourcesDao redisTrustedSourcesDao = new RedisTrustedSourcesDaoImpl();


    @Override
    public void executeTuple( Tuple input )
    {
        if(!TupleUtils.isTick( input )) {
            LOG.debug( "Executing filter social post bolt" );
            SocialResponseObject<?> post = (SocialResponseObject<?>) input.getValueByField( "post" );
            long companyId = input.getLongByField( "companyId" );
            if ( post != null ) {
                post.setActionHistory( new ArrayList<>() );
                ActionHistory actionHistory;
                if ( post.getText() != null ) {
                    String text = post.getText();
                    List<String> foundKeyWords;
                    TrieNode root = getTrieForCompany( companyId );
                    foundKeyWords = findPhrases( root, text );
                    if ( !foundKeyWords.isEmpty() ) {
                        post.setFoundKeywords( foundKeyWords );
                        post.setStatus( SocialFeedStatus.ALERT );
                        actionHistory = getFlaggedActionHistory( foundKeyWords );
                        post.getActionHistory().add( actionHistory );
                        post.setUpdatedTime( actionHistory.getCreatedDate() );
                        addTextHighlight( post );
                    }
                }

                // check if post from trusted source
                if ( isPostFromTrustedSource( post, companyId ) ) {
                    //post.setStatus( SocialFeedStatus.RESOLVED );
                    post.setFromTrustedSource( true );
                    //actionHistory = getTrustedSourceActionHistory( post.getPostSource() );
                    //post.getActionHistory().add( actionHistory );
                    //post.setUpdatedTime( actionHistory.getCreatedDate() );
                }
            }
            LOG.debug( "Emitting tuple with post having postId = {} and post = {}", post.getPostId(), post );
            _collector.emit( input, Arrays.asList( companyId, post ) );
        }
    }

    /**
     * Method to add text highlighting
     * @param post
     */
    private void addTextHighlight( SocialResponseObject<?> post )
    {
        LOG.debug( "Adding text highlighting for found keywords for post id {}",  post.getPostId());
        String textHighlighted = post.getText();
        for ( String keyword : post.getFoundKeywords() ) {
            textHighlighted = Pattern.compile( IGNORE_CASE_REGEX_PREFIX + keyword ).matcher(textHighlighted).replaceAll( HIGHLIGHT_START+keyword+HIGHLIGHT_END );
        }
        post.setTextHighlighted(textHighlighted);
        LOG.debug( "Success fully added text highlighting for found keywords {}", post.getPostId() );
    }

    @Override
    public List<Object> prepareTupleForFailure() {
        return new Values(0L, null);
    }


    private ActionHistory getFlaggedActionHistory( List<String> foundKeyWords )
    {
        ActionHistory actionHistory = new ActionHistory();
        actionHistory.setCreatedDate( new Date().getTime() );
        actionHistory.setActionType( ActionHistoryType.FLAGGED );
        actionHistory.setOwnerName( OWNERNAME_SYSTEM );
        actionHistory.setText( "The post was <b class='soc-mon-bold-text'>Flagged</b> for matching <b class='soc-mon-bold-text'>" + String.join( ",", foundKeyWords )  + "</b>");
        return actionHistory;
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

        companyTrie.put( companyIden, new TrieNode() );
        if ( keywordListResponse != null && !keywordListResponse.isEmpty() ) {
            for ( Keyword keyword : keywordListResponse ) {
                if(keyword.getPhrase() != null){
                    addPhrase( companyTrie.get( companyIden ), keyword.getPhrase().toLowerCase(), keyword.getId() );
                }
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

        if ( StringUtils.isEmpty( textBody ) ) {
            return Collections.emptyList();
        }

        String[] words = textBody.split( WORD_SEPARATOR );
        List<String> foundPhrases = new ArrayList<>();
        StringBuilder phraseBuffer = new StringBuilder();

        for ( int i = 0; i < words.length; ) {
            String word = words[i].toLowerCase();
            if ( node.getChildren() != null && node.getChildren().containsKey( word ) ) {
                // move trie pointer forward
                node = node.getChildren().get( word );
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

        //removed as keywords are added twice due to this condition
        /*if ( node.getPhraseId() != null ) {
            String phrase = phraseBuffer.toString().trim();
            foundPhrases.add( phrase );
            LOG.trace( "end matching {}", phrase );
        }*/

        LOG.debug( "End of findPhrase method, foundkeywords:{}", foundPhrases );
        return foundPhrases;
    }


    private boolean isPostFromTrustedSource( SocialResponseObject<?> post, long companyId )
    {
        String postSource = post.getPostSource();
        if ( StringUtils.isNotEmpty( postSource ) ) {
            List<SocialMonitorTrustedSource> trustedSources = redisTrustedSourcesDao
                .getCompanyTrustedSourcesForCompanyId( companyId );
            if ( trustedSources != null ) {
                for ( SocialMonitorTrustedSource trustedSource : trustedSources ) {
                    if ( StringUtils.equalsIgnoreCase( postSource, trustedSource.getSource() )
                        && trustedSource.getStatus() == 1 ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ActionHistory getTrustedSourceActionHistory( String source )
    {
        ActionHistory actionHistory = new ActionHistory();
        actionHistory.setCreatedDate( new Date().getTime() );
        actionHistory.setActionType( ActionHistoryType.RESOLVED );
        actionHistory.setOwnerName( OWNERNAME_SYSTEM );
        actionHistory.setText( "The post was <b class='soc-mon-bold-text'>Resolved</b> for having source <b class='soc-mon-bold-text'>" + source + "</b>");
        return actionHistory;
    }
}
