package com.swapfy.backend.exceptions;

public class ErrorResponse {
    private String title;
    private String message;
    private String error;

    public ErrorResponse(String title, String message, String error) {
        this.title = title;
        this.message = message;
        this.error = error;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
