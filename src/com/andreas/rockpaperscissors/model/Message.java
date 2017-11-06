package com.andreas.rockpaperscissors.model;

import java.io.Serializable;

public class Message implements Serializable{
    private MessageType type;
    private String content;
    private int number;
    private String senderName;
    private PlayCommand playCommand = PlayCommand.ROCK;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public Message setType(MessageType type) {
        this.type = type;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public Message setNumber(int number) {
        this.number = number;
        return this;
    }

    public String getSenderName() {
        return senderName;
    }

    public Message setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public PlayCommand getPlayCommand() {
        return playCommand;
    }

    public Message setPlayCommand(PlayCommand playCommand) {
        this.playCommand = playCommand;
        return this;
    }
}
