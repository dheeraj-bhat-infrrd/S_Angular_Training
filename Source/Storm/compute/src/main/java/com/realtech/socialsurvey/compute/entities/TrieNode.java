package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;
import java.util.Map;


/**
 * Trie Node, which stores a String and the children in a HashMap
 * @author manish
 * 
 */
public class TrieNode implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String value;
    private Map<String, TrieNode> children;
    private String phraseId;
    private Long modifiedOn;


    public TrieNode()
    {}


    public TrieNode( String phraseId )
    {
        this.phraseId = phraseId;
    }


    public String getValue()
    {
        return value;
    }


    public Map<String, TrieNode> getChildren()
    {
        return children;
    }


    public String getPhraseId()
    {
        return phraseId;
    }


    public void setValue( String value )
    {
        this.value = value;
    }


    public void setChildren( Map<String, TrieNode> children )
    {
        this.children = children;
    }


    public void setPhraseId( String phraseId )
    {
        this.phraseId = phraseId;
    }


    public Long getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Long modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    @Override
    public String toString()
    {
        return "TrieNode [value=" + value + ", children=" + children + ", phraseId=" + phraseId + "]";
    }
}