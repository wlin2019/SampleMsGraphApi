package com.ms360service.graphAPI.service;

import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.ms360service.graphAPI.exception.GraphAPIException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class MyGraphServiceClient {

    private static final String EMAIL_URL = "https://graph.microsoft.com/v1.0/users/%s/messages";

    @Autowired
    @Qualifier("graphAuth")
    private ConfidentialClientApplication authApp;

    @Autowired
    @Qualifier("tokenParam")
    private ClientCredentialParameters param;

    public String getAccessToken() throws ExecutionException, InterruptedException {
        CompletableFuture<IAuthenticationResult> future = authApp.acquireToken(param);
        IAuthenticationResult result = future.get();
        return result.accessToken();
    }

    public String getMessages(String clientId) throws GraphAPIException {
        try {
            String accessToken = getAccessToken();
            URL url = new URL(String.format(EMAIL_URL, clientId));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Accept", "application/json");
            int httpResponseCode = conn.getResponseCode();
            if(httpResponseCode == HTTPResponse.SC_OK) {

                System.out.println("return ok. Read return content......");
                StringBuilder response;
                try(BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))){

                    String inputLine;
                    response = new StringBuilder();
                    while (( inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                return response.toString();
            } else {
                throw new GraphAPIException(String.format("Connection returned HTTP code: %s with message: %s",
                        httpResponseCode, conn.getResponseMessage()));
            }
        } catch (Exception e) {
            throw new GraphAPIException(e.getMessage(), e);
        }
    }

}
