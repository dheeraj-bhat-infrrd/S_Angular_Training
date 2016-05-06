package com.realtech.socialsurvey.core.entities.api;

public class Industry
{
    private int id;
    private String vertical;
    private int priorityOrder;


    public int getId()
    {
        return id;
    }


    public void setId( int id )
    {
        this.id = id;
    }


    public String getVertical()
    {
        return vertical;
    }


    public void setVertical( String vertical )
    {
        this.vertical = vertical;
    }


    public int getPriorityOrder()
    {
        return priorityOrder;
    }


    public void setPriorityOrder( int priorityOrder )
    {
        this.priorityOrder = priorityOrder;
    }
}
