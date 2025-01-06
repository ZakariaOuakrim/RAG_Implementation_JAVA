package org.example.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private int id;
    private Date sendAt;
    private int conversationId;
    private String text;
    private byte[] image;
}
