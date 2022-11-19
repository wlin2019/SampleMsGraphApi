package com.ms360service.graphAPI.model;

import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class MyMessage {
    private String conversationId;
    private int conversationIndex;
    private Date receivedDateTime;
    private String receivedFrom;
    private String sender;
    private String subject;
    private String body;

    public MyMessage(Message m) {
        conversationId = m.conversationId;
        conversationIndex = convertByteToInt(m.conversationIndex);
        receivedDateTime = getDateFromOffsetDatetime(m.receivedDateTime);
        receivedFrom = getRecipiantAddress(m.from);
        sender = getRecipiantAddress(m.sender);
        subject = m.subject;
        body = getBodyContent(m.body);
    }

    private Date getDateFromOffsetDatetime(OffsetDateTime dt) {
        long epochMill = dt.toInstant().toEpochMilli();
        return new Date(epochMill);
    }

    private String getRecipiantAddress(Recipient sender) {
        if (sender != null) {
            return sender.emailAddress.address;
        }
        return "";
    }

    private String getBodyContent(ItemBody body) {
        if (body != null) {
            return body.content;
        }
        return "";
    }

    private int convertByteToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
}
