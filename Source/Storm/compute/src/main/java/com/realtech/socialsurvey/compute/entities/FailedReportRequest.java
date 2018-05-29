package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

@Entity( "failed_messages")
public class FailedReportRequest extends FailedMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Embedded
    private ReportRequest data;

    public ReportRequest getData() {
        return data;
    }

    public void setData(ReportRequest data) {
        this.data = data;
    }
}
