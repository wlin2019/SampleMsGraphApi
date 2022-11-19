package com.ms360service.graphAPI.configure;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class GraphAPIConfig {

    @Value("${app.id}")
    private String clientId;

    @Value("${app.clientSecret}")
    private String clientSecret;

    @Value("${app.tenantId}")
    private String tenantId;

    @Value("${app.scopes}")
    private String scope;

    @Value("${app.authority}")
    private String authHost;

    private static final Logger logger = LoggerFactory.getLogger(GraphAPIConfig.class);

    @Bean
    protected TokenCredentialAuthProvider authProvider() {
        // Create the auth provider
        List<String> scopes = Arrays.asList(scope.split(","));
        final ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .tenantId(tenantId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorityHost(authHost)
                .build();
        return new TokenCredentialAuthProvider(scopes, credential);
    }

    @Bean(name = "graphClient")
    public GraphServiceClient<Request> graphClient() {
        return GraphServiceClient.builder()
                .authenticationProvider(authProvider())
                .buildClient();
    }

    @Bean(name = "graphAuth")
    public ConfidentialClientApplication getAuthClient() throws Exception {
        return ConfidentialClientApplication.builder(
                clientId,
                ClientCredentialFactory.createFromSecret(clientSecret))
                .authority(authHost)
                .build();
    }

    @Bean(name = "tokenParam")
    public ClientCredentialParameters getTokeParameterw() {
        return ClientCredentialParameters.builder(
                Collections.singleton(scope))
                .build();
    }


}
