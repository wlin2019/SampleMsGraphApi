package com.ms360service.graphAPI.controller;

import com.ms360service.graphAPI.exception.GraphAPIException;
import com.ms360service.graphAPI.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "/data")
public class DataAPIController {

    @Autowired
    private DatabaseService service;

    @GetMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getReadEmail(@RequestParam(name = "sender", required = true) String sender,
                                               @RequestParam(name = "fromDate", required = false) Date fromDate) {
        try {
            String result = service.readMail(sender, fromDate);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (GraphAPIException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> postMessage(
                                              @RequestParam(name = "sender", required = true) String sender,
                                              @RequestParam(name = "receivedDate", required = false) Date receivedDate,
                                              @RequestParam(name = "subject", required = false) String subject,
                                              @RequestParam(name = "receiver", required = false) String receiver,
                                              @RequestBody String message)
    {
        try {
            int result = service.saveMail(sender, receivedDate, subject, receiver, message);
            return ResponseEntity.status(HttpStatus.OK).body(result + "");
        } catch (GraphAPIException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
