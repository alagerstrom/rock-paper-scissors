package com.andreas.rockpaperscissors.net;

import java.io.Serializable;

public class NetMessage<T> implements Serializable{
    private NetMessageType type;
    private int number;
    private Peer sender;
    private T content;



    public NetMessage(NetMessageType type) {
        this.type = type;
    }

    public NetMessageType getType() {
        return type;
    }

    public NetMessage setType(NetMessageType type) {
        this.type = type;
        return this;
    }

    public T getContent() {
        return content;
    }

    public NetMessage setContent(T content) {
        this.content = content;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public NetMessage setNumber(int number) {
        this.number = number;
        return this;
    }

    public Peer getSender() {
        return sender;
    }

    public NetMessage setSender(Peer sender) {
        this.sender = sender;
        return this;
    }

}
