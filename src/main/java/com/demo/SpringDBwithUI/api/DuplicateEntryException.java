package com.demo.SpringDBwithUI.api;

public class DuplicateEntryException extends RuntimeException{

    public DuplicateEntryException(String message) {
        super(message);
    }

}
