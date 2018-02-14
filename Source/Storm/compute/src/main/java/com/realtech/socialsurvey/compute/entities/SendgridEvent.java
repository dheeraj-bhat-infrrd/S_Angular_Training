package com.realtech.socialsurvey.compute.entities;


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
    @JsonProperty( value = "smtp-id" )
    private String smtpId;
    private String event;
    private String category;
    private String sg_event_id;
    private String sg_message_id;
    private String response;
    private String attempt;
    private String useragent;
    private String ip;
    private String url;
    private String reason;
    private String status;
    private String asm_group_id;
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


    public String getSg_event_id()
    {
        return sg_event_id;
    }


    public void setSg_event_id( String sg_event_id )
    {
        this.sg_event_id = sg_event_id;
    }


    public String getSg_message_id()
    {
        return sg_message_id;
    }


    public void setSg_message_id( String sg_message_id )
    {
        this.sg_message_id = sg_message_id;
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


    public String getAsm_group_id()
    {
        return asm_group_id;
    }


    public void setAsm_group_id( String asm_group_id )
    {
        this.asm_group_id = asm_group_id;
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
				+ ", category=" + category + ", sg_event_id=" + sg_event_id + ", sg_message_id=" + sg_message_id
				+ ", response=" + response + ", attempt=" + attempt + ", useragent=" + useragent + ", ip=" + ip
				+ ", url=" + url + ", reason=" + reason + ", status=" + status + ", asm_group_id=" + asm_group_id
				+ ", uuid=" + uuid + "]";
	}

	

}
