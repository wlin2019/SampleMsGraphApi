package com.ms360service.graphAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.graph.models.*;
import com.ms360service.graphAPI.exception.GraphAPIException;
import com.ms360service.graphAPI.model.Email;
import com.ms360service.graphAPI.model.MyMessage;
import com.ms360service.graphAPI.service.MailAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/msgraph")
public class GraphAPIController {

    @Autowired
    private MailAPIService service;

    @Autowired
    private ObjectMapper mapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailAPIService.class);

    @GetMapping("/status")
    public String status() {
        LOGGER.info("Check status...");
        return "Greeting from graphAPI service.\n";
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

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getUser() {
        try {
            User result = service.getMe();
            String data = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (GraphAPIException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getUsers() {
        try {
            List<User> result = service.getUsers();
            String data = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (GraphAPIException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/mailfolders")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getMailFolder() {
        try {
            List<MailFolder> result = service.getMailFolders();

            String data = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (GraphAPIException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/myinbox")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getInbox() {
        try {
            List<Message> retValue = service.getInbox();

            List<MyMessage> result = retValue.stream()
                    .map(v -> new MyMessage(v))
                    .collect(Collectors.toList());
            LOGGER.info("Total mail read in inbox: " +  result.size());
            String data = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (GraphAPIException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/mails/{folderName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getMails(@PathVariable("folderName")String folder) {
        try {
            List<Message> retValue = service.getMails(folder);

            List<MyMessage> result = retValue.stream()
                    .map(v -> new MyMessage(v))
                    .collect(Collectors.toList());
            LOGGER.info(String.format("Total mails read in %s: %d", folder, result.size()));
            String data = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (GraphAPIException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/message/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getMessage(@RequestParam("id")String userId) {
        try {
            List<Message> retValue = service.getMessages(userId);
            List<MyMessage> result = retValue.stream()
                    .map(v -> new MyMessage(v))
                    .collect(Collectors.toList());
            LOGGER.info(String.format("Total mails read for user, %s: %d", userId, result.size()));
            String data = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (GraphAPIException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/message/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> postMessage(@PathVariable("id")String userId,
                                              @RequestParam(name = "toAddress", required = true) String toAddress,
                                              @RequestParam(name = "subject", required = false) String subject,
                                              @RequestBody String message)
    {
        try {
            String result = service.sendMail(userId, toAddress, subject, message);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (GraphAPIException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }


}
