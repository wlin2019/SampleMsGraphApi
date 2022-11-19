package com.ms360service.graphAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.User;
import com.ms360service.graphAPI.exception.GraphAPIException;
import com.ms360service.graphAPI.model.Email;
import com.ms360service.graphAPI.model.MessageResponse;
import com.ms360service.graphAPI.service.GraphMailAPIService;
import com.ms360service.graphAPI.service.MailAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/msgraph2")
public class GraphMailAPIController {
    @Autowired
    private GraphMailAPIService service;

    @Autowired
    private ObjectMapper mapper;

    @GetMapping("/status")
    public String status() {
        return "Greeting from Graph Mail API service.\n";
    }

    @GetMapping("/email/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getEmail(@PathVariable("id")String userId) {
        try {
            List<Email> result = service.getEmail(userId);
            String data = mapper.writeValueAsString(result);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (GraphAPIException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/message/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getMessages(@PathVariable("id")String userId) {
        try {
            String respData = service.getMessage(userId);
            return ResponseEntity.status(HttpStatus.OK).body(respData);
        } catch (GraphAPIException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
