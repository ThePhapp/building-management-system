package com.javaweb.model.response;

import java.util.Collections;
import java.util.List;

public class ApiErrorResponse {
    private final long timestamp = System.currentTimeMillis();
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<String> details;

    public ApiErrorResponse(int status, String error, String message, String path, List<String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details == null ? Collections.emptyList() : details;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<String> getDetails() {
        return details;
    }
}
