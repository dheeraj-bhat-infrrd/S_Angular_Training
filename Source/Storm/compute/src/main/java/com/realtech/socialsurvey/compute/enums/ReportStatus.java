package com.realtech.socialsurvey.compute.enums;

public enum ReportStatus {
    PROCESSING ("PROCESSING"),
    PROCESSED("PROCESSED"),
    BLANK("BLANK"),
    FAILED("FAILED");

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
