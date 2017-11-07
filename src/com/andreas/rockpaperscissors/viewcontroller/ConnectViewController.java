package com.andreas.rockpaperscissors.viewcontroller;

import com.andreas.rockpaperscissors.controller.AppController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import com.andreas.rockpaperscissors.util.Constants;
import com.andreas.rockpaperscissors.util.Logger;

import java.io.IOException;
import java.nio.channels.CompletionHandler;

public class ConnectViewController {

    @FXML
    TextField hostField;
    @FXML
    TextField hostPortField;
    @FXML
    Text errorText;


    @FXML
    public void skipConnect(ActionEvent actionEvent) throws IOException {
        ViewCoordinator.getInstance().hideWindow(actionEvent);
    }

    @FXML
    public void connect(ActionEvent actionEvent) throws IOException {
        String remoteHostString = hostField.getText();
        String remotePortString = hostPortField.getText();

        if (!remoteHostString.equals("") && !remotePortString.equals("")) {
            int remotePort;
            try {
                remotePort = Integer.parseInt(remotePortString);
            } catch (NumberFormatException e) {
                errorText.setText("Invalid port number");
                return;
            }

            AppController.getInstance().connectTo(remoteHostString, remotePort, new CompletionHandler<Void, Void>() {
                @Override
                public void completed(Void result, Void attachment) {
                    Platform.runLater(()->{
                        ViewCoordinator.getInstance().hideWindow(actionEvent);
                    });
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    Platform.runLater(()-> errorText.setText("Failed to connect"));
                }
            });
        } else {
            errorText.setText("You must enter both host and port.");
        }

    }


    public void initialize() {
        errorText.setText("");
        hostField.setText("localhost");
        hostPortField.setText(Constants.DEFAULT_PORT + "");
        Logger.log("Join View initialized");
    }
}
