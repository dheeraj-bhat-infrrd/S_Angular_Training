package com.realtech.socialsurvey.stream.common;

/**
 * Created by nishit on 04/01/18.
 */
public class FailedMessageConstants
{
    private FailedMessageConstants(){}

    public static final String EMAIL_MESSAGES = "EMAIL_MESSAGES";
    public static final String SOCIAL_POST_MESSAGE = "SOCIAL_POST_MESSAGE";
    public static final String SURVEY_PROCESSOR_MESSAGE = "SURVEY_PROCESSOR_MESSAGE";
    
    public static final String SMS_CATEGORY_UNSUBSCRIBE = "Unsubscribe";
    public static final String SMS_CATEGORY_RESUBSCRIBE = "Resubscribe";
    public static final String SMS_CATEGORY_INVALID = "InvalidContentsReply";
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String UNSUBSCRIBE_SMS_CONTENT = "Thanks for subscribing, reply STOP to unsubscribe.";
    public static final String RESUBSCRIBE_SMS_CONTENT = "You have been unsubscribed, reply START to subscribe.";
    public static final String INVALID_SMS_CONTENT = "Sorry we can only accept START or STOP";
}
