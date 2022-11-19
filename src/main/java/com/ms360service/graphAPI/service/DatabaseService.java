package com.ms360service.graphAPI.service;

import com.ms360service.graphAPI.exception.GraphAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Service
public class DatabaseService {
    private static String SAVE_MAIL = "select ";

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public String readMail(String sender, Date fromDate) throws GraphAPIException {
        return "call jdbcTemplate to get mails.";
    }

    public int saveMail(String sender,
                        Date receivedDate,
                        String subject,
                        String receiver,
                        String message) throws GraphAPIException {
        return 0;
    }

}

