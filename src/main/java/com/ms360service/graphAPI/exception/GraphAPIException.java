package com.ms360service.graphAPI.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public class GraphAPIException extends Exception {

    @Getter
    @Setter
    private HttpStatus status;

    public GraphAPIException() { super("Graph-API-service"); }

    public GraphAPIException(String s) {
        super(s);
    }

    public GraphAPIException(String s, Throwable e) {
        super(s, e);
    }

    public GraphAPIException(String s, Throwable e, HttpStatus status) {
        super(s, e);
        this.status = status;
    }
}
