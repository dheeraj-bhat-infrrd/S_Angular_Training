package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

import org.springframework.data.annotation.Id;


public class FailedStreamMessage<T> implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;
}
