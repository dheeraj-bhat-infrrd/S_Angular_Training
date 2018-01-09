package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

public class FileUploadResponse implements Serializable{

    private static final long serialVersionUID = 1L;

    private int recordsUpdated;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRecordsUpdated() {
        return recordsUpdated;
    }

    public void setRecordsUpdated(int recordsUpdated) {
        this.recordsUpdated = recordsUpdated;
    }

    @Override
    public String toString() {
        return "FileUploadResponse{" +
                "recordsUpdated=" + recordsUpdated +
                ", message='" + message + '\'' +
                '}';
    }
}
