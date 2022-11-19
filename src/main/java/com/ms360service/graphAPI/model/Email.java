package com.ms360service.graphAPI.model;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
public class Email {
    private String subject;
    private String content;
    private String author;
}
