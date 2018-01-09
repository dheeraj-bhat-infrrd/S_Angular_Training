package com.realtech.socialsurvey.compute.entities;

import java.util.Map;
import java.io.Serializable;


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


    @Override
    public String toString()
    {
        return "TrieNode [value=" + value + ", children=" + children + ", phraseId=" + phraseId + "]";
    }
}