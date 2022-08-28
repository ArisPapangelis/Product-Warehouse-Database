package com.demo.SpringDBwithUI.api;

/**
 * Exception thrown when a company or product attempting to be saved already exists in the database.
 * This means a product with the same name, weight and company, or a company with the same name.
 */
public class DuplicateEntryException extends RuntimeException{

    public DuplicateEntryException(String message) {
        super(message);
    }

}
