package com.realtech.socialsurvey.stream.messages;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Events from Sendgrid webhook
 * @author nishit
 *
 */
public class SendgridEvent
{

    private String email;
    private long timestamp;
    @JsonProperty ( value = "smtp-id")
    private String smtpId;
    private String event;
    private String category;
    @JsonProperty ( value = "sg_event_id")
    private String sgEventId;
    @JsonProperty ( value = "sg_message_id")
    private String sgMessageId;
    private String response;
    private String attempt;
    private String useragent;
    private String ip;
    private String url;
    private String reason;
    private String status;
    @JsonProperty ( value = "asm_group_id")
    private String asmGroupId;
    private String uuid;


    public String getEmail()
    {
        return email;
    }


    public void setEmail( String email )
    {
        this.email = email;
    }


    public long getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp( long timestamp )
    {
        this.timestamp = timestamp;
    }


    public String getSmtpId()
    {
        return smtpId;
    }


    public void setSmtpId( String smtpId )
    {
        this.smtpId = smtpId;
    }


    public String getEvent()
    {
        return event;
    }


    public void setEvent( String event )
    {
        this.event = event;
    }


    public String getCategory()
    {
        return category;
    }


    public void setCategory( String category )
    {
        this.category = category;
    }


    public String getSgEventId()
    {
        return sgEventId;
    }


    public void setSgEventId( String sgEventId )
    {
        this.sgEventId = sgEventId;
    }


    public String getSgMessageId()
    {
        return sgMessageId;
    }


    public void setSgMessageId( String sgMessageId )
    {
        this.sgMessageId = sgMessageId;
    }


    public String getResponse()
    {
        return response;
    }


    public void setResponse( String response )
    {
        this.response = response;
    }


    public String getAttempt()
    {
        return attempt;
    }


    public void setAttempt( String attempt )
    {
        this.attempt = attempt;
    }


    public String getUseragent()
    {
        return useragent;
    }


    public void setUseragent( String useragent )
    {
        this.useragent = useragent;
    }


    public String getIp()
    {
        return ip;
    }


    public void setIp( String ip )
    {
        this.ip = ip;
    }


    public String getUrl()
    {
        return url;
    }


    public void setUrl( String url )
    {
        this.url = url;
    }


    public String getReason()
    {
        return reason;
    }


    public void setReason( String reason )
    {
        this.reason = reason;
    }


    public String getStatus()
    {
        return status;
    }


    public void setStatus( String status )
    {
        this.status = status;
    }


    public String getAsmGroupId()
    {
        return asmGroupId;
    }


    public void setAsmGroupId( String asmGroupId )
    {
        this.asmGroupId = asmGroupId;
    }


	public String getUuid() {
		return uuid;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	@Override
	public String toString() {
		return "SendgridEvent [email=" + email + ", timestamp=" + timestamp + ", smtpId=" + smtpId + ", event=" + event
				+ ", category=" + category + ", sgEventId=" + sgEventId + ", sgMessageId=" + sgMessageId + ", response="
				+ response + ", attempt=" + attempt + ", useragent=" + useragent + ", ip=" + ip + ", url=" + url
				+ ", reason=" + reason + ", status=" + status + ", asmGroupId=" + asmGroupId + ", uuid=" + uuid + "]";
	}
	
	


}
