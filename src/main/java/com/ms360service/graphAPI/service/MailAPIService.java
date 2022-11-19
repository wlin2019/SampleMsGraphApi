package com.ms360service.graphAPI.service;

import com.microsoft.graph.models.*;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.*;
import com.ms360service.graphAPI.exception.GraphAPIException;
import com.ms360service.graphAPI.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MailAPIService {

    @Autowired
    @Qualifier("graphClient")
    private GraphServiceClient graphClient;

    @Value("${app.mailClientId}")
    private String userId;

    private static AtomicReference<String> deltaToken = new AtomicReference<String>("");

    private static final Logger LOGGER = LoggerFactory.getLogger(MailAPIService.class);

    public List<Email> getEmail(String user) throws GraphAPIException {
        try {
            LOGGER.info("getEmail " + user);
            List<Email> result = new ArrayList<>();
            Email email = new Email("subject", "contents...", user);
            result.add(email);
            return result;
        } catch (Exception e) {
            throw new GraphAPIException("Failed to get email.", e);
        }
    }

    public List<User> getUsers() throws GraphAPIException {
        if (graphClient == null) throw new GraphAPIException(
                "Graph client has not been initialized. Call initializeGraphAuth before calling this method");

        // GET /me to get authenticated user
        try {
            UserCollectionPage page = graphClient
                    .users()
                    .buildRequest()
                    .get();
            List<User> result = page.getCurrentPage();
            return result;
        } catch (Exception e) {
            throw new GraphAPIException("failed to getUsers. " + e.getMessage(), e);
        }
    }

    public User getMe() throws GraphAPIException {
        try {
            User me = graphClient
                    .me()
                    .buildRequest()
                    .get();

            return me;
        } catch (Exception e) {
            throw new GraphAPIException("failed to getMe. " + e.getMessage(), e);
        }
    }

    /**
     * Lists the mail folders.
     *
     * @return a list of {@link MailFolder}.
     * @throws GraphAPIException
     */
    public List<MailFolder> getMailFolders() throws GraphAPIException {
        try {
            MailFolderCollectionPage msg = graphClient
                    .users(userId)
                    .mailFolders()
                    .buildRequest()
                    .get();
            List<MailFolder> messages = msg.getCurrentPage();
            return messages;
        } catch (Exception e) {
            throw new GraphAPIException("failed to getMessages. " + e.getMessage(), e);
        }
    }

    /**
     * Gets a list of unread mails in inbox folder.
     *
     * @return a list of {@link Message}.
     * @throws GraphAPIException
     */
    public List<Message> getInbox() throws GraphAPIException {
        try {
            return getMailsDelta("inbox");
        } catch (Exception e) {
            throw new GraphAPIException("failed to getMessages. " + e.getMessage(), e);
        }
    }

    /**
     * Gets the mails in the given folder using delta read.
     *
     * @param folder folder name
     * @return a list of {@link Message}.
     * @throws GraphAPIException
     */
    public List<Message> getMailsDelta(String folder) throws GraphAPIException {
        try {
            List<Message> messages = new ArrayList<>();
            LinkedList<Option> requestOptions = new LinkedList<Option>();
            requestOptions.add(new HeaderOption("Prefer", "odata.maxpagesize=4"));
            //requestOptions.add(new HeaderOption("Content-Type", "application/json"));
            LOGGER.info(String.format("Delta reads folder, %s", folder));
            if (!deltaToken.get().isEmpty()) {
                LOGGER.info(String.format("Reads message with delta: %s", deltaToken.get()));
                requestOptions.add(new QueryOption("$skiptoken", deltaToken.get()));
            }


            MessageDeltaCollectionPage msg = getDeltaMessages(folder, requestOptions);
            assert msg != null;
            List<Message> deltaMsg = msg.getCurrentPage();
            messages.addAll(deltaMsg);
            String nextLink = msg.deltaLink();
            while ((deltaMsg.size() > 0) && (nextLink == null)) {
                LOGGER.info(String.format("Read %d messages. Reads next page in the folder, %s", deltaMsg.size(), folder));
                msg = msg.getNextPage().buildRequest(requestOptions).get();
                assert msg != null;
                deltaMsg = msg.getCurrentPage();
                messages.addAll(msg.getCurrentPage());
                nextLink = msg.deltaLink();
            }
            if (nextLink != null) {
                LOGGER.info("Delta read ended.");
                String[] data = nextLink.split("deltatoken=");
                if (data.length == 2) {
                    deltaToken.set(data[1]);
                    LOGGER.info("Save delta token.");
                }
            }
            LOGGER.info(String.format("The number of mails read from folder, %s, is %d.", folder, messages.size()));
            return messages;
        } catch (Exception e) {
            LOGGER.error(String.format("getMailsDelta failed. %s", e.getMessage()));
            throw new GraphAPIException("failed to getMessages. " + e.getMessage(), e);
        }
    }

    private MessageDeltaCollectionPage getDeltaMessages(String folder, LinkedList<Option> requestOptions) {
        return graphClient
                .users(userId)
                .mailFolders(folder)
                .messages()
                .delta()
                .buildRequest(requestOptions)
                .select("conversationId, conversationIndex, receivedDateTime, subject, body, sender, from")
                .get();
    }

    /**
     * Retrieves all the mails for the given folder.
     *
     * @param folder folder name.
     * @return a list of {@link Message}.
     * @throws GraphAPIException
     */
    public List<Message> getMails(String folder) throws GraphAPIException {
        try {

            MessageCollectionPage msg = graphClient
                    .users(userId)
                    .mailFolders(folder)
                    .messages()
                    .buildRequest()
                    .select("conversationId, conversationIndex, receivedDateTime, subject, body, sender, from")
                    .get();
            assert msg != null;
            List<Message> messages = msg.getCurrentPage();
            return messages;
        } catch (Exception e) {
            throw new GraphAPIException("failed to getMessages. " + e.getMessage(), e);
        }
    }

    public List<Message> getMessages(String id) throws GraphAPIException {
        try {
            MessageCollectionPage msg = graphClient
                    .users(id)
                    .messages()
                    .buildRequest()
                    .select("receivedDateTime, subject, body, sender, from")
                    .get();
            List<Message> messages = msg.getCurrentPage();
            return messages;
        } catch (Exception e) {
            throw new GraphAPIException("failed to getMessages. " + e.getMessage(), e);
        }
    }

    public String sendMail(String id, String toAddress, String subject, String content)
            throws GraphAPIException {
        try {
            Message message = new Message();
            message.subject = subject;
            ItemBody body = new ItemBody();
            body.content = content;
            message.body = body;
            Recipient recipient = new Recipient();
            EmailAddress address = new EmailAddress();
            address.address = toAddress;
            recipient.emailAddress = address;
            List<Recipient> recipientList = new ArrayList<>();
            recipientList.add(recipient);
            message.toRecipients = recipientList;

            Recipient sender = new Recipient();
            EmailAddress senderAddress = new EmailAddress();
            senderAddress.address = "o365sup8@microsoft.com";
            sender.emailAddress = senderAddress;
            message.sender = sender;

            UserSendMailParameterSet mailParameterSet = UserSendMailParameterSet.newBuilder().build();
            mailParameterSet.message = message;
            graphClient.users(id)
                    .sendMail(mailParameterSet)
                    .buildRequest()
                    .post();
            return "";
        } catch (Exception e) {
            throw new GraphAPIException("failed to sendEmail. " + e.getMessage(), e);
        }
    }
}
