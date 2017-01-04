package com.realtech.socialsurvey.api.models.v2;

import org.springframework.stereotype.Component;


@Component
public class SurveyResponseV2VO
{
    private String question;
    private String type;
    private String answer;


    public String getQuestion()
    {
        return question;
    }


    public void setQuestion( String question )
    {
        this.question = question;
    }


    public String getType()
    {
        return type;
    }


    public void setType( String type )
    {
        this.type = type;
    }


    public String getAnswer()
    {
        return answer;
    }


    public void setAnswer( String answer )
    {
        this.answer = answer;
    }
}
