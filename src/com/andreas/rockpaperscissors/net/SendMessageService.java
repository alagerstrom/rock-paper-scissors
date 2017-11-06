package com.andreas.rockpaperscissors.net;

import com.andreas.rockpaperscissors.model.Message;
import com.andreas.rockpaperscissors.util.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class SendMessageService extends Service {

    private final Message message;

    public SendMessageService(Message message){
        this.message = message;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                NetHandler netHandler = NetHandler.getInstance();
                netHandler.sendMessage(message);
                Logger.log("Sent message " + message.getType() + ", " + message.getContent());
                return null;
            }
        };
    }
}
