package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;


public class Rating implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int value;

    public int getValue() { return this.value; }

    public void setValue(int value) { this.value = value; }

    private int scale;

    public int getScale() { return this.scale; }

    public void setScale(int scale) { this.scale = scale; }
}
