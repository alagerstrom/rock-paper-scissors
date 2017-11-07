package com.andreas.rockpaperscissors.net;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class SendMessageService<T> extends Service {

    private final T message;
    private final NetHandler netHandler;

    public SendMessageService(T message, NetHandler netHandler){
        this.netHandler = netHandler;
        this.message = message;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                NetMessage<T> netMessage = new NetMessage<>(NetMessageType.MESSAGE);
                netMessage.setContent(message);
                netHandler.sendNetMessage(netMessage);
                return null;
            }
        };
    }
}
