package com.demo.SpringDBwithUI.api;

import org.springframework.http.HttpStatus;

/**
 * Class describing the Error returned whenever there is an exception in the REST API.
 */
public class ApiError {

    private String timeStamp;
    private HttpStatus status;
    private String error;
    private String message;

    /**
     * Constructor for ApiError.
     *
     * @param timeStamp Timestamp when the exception was thrown.
     * @param status HTTP status explaining the error.
     * @param error Internal exception message.
     * @param message Explanatory message for the user.
     */
    public ApiError(String timeStamp, HttpStatus status, String error, String message) {
        this.timeStamp = timeStamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
