package com.realtech.socialsurvey.compute.enums;

public enum FileUploadStatus {
    STATUS_COMPLETED (0),
    STATUS_INITIATED (1),
    STATUS_UNDER_PROCESSING (2),
    STATUS_BLANK (3),
    STATUS_FAILED (4);

    private final int value;

    FileUploadStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
