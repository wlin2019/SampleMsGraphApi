package com.ms360service.graphAPI.service;

import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.ms360service.graphAPI.exception.GraphAPIException;
import com.ms360service.graphAPI.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GraphMailAPIService {

    @Autowired
    private MyGraphServiceClient graghClient;

    public List<Email> getEmail(String user) throws GraphAPIException {
        try {
            List<Email> result = new ArrayList<>();

            Email email = new Email("subject", "contents...", user);
            result.add(email);
            return result;
        } catch (Exception e) {
            throw new GraphAPIException("Failed to get email.", e);
        }
    }

    public String getMessage(String user) throws GraphAPIException {
        try {
            return graghClient.getMessages(user);
        } catch (Exception e) {
            throw new GraphAPIException(e.getMessage(), e);
        }
    }
}
