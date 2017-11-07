package com.andreas.rockpaperscissors.viewcontroller;

import com.andreas.rockpaperscissors.controller.AppController;
import com.andreas.rockpaperscissors.util.Constants;
import com.andreas.rockpaperscissors.util.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.nio.channels.CompletionHandler;

public class StartViewController {

    @FXML
    TextField nameField;
    @FXML
    TextField portField;
    @FXML
    Text errorText;

    public void initialize() {
        portField.setText("" + Constants.DEFAULT_PORT);
        errorText.setText("");
        Platform.runLater(() -> nameField.requestFocus());
        Logger.log("Start view initialized");
    }

    @FXML
    public void startGame(ActionEvent actionEvent) throws IOException {
        String playerName = nameField.getText();
        String portNumber = portField.getText();

        Logger.log("Name: " + playerName);
        int port;
        try {
            port = Integer.parseInt(portNumber);
        } catch (NumberFormatException e) {
            errorText.setText("Invalid port number");
            return;
        }

        AppController.getInstance().createNewGame(playerName, port, new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                Platform.runLater(() -> {
                    ViewCoordinator viewCoordinator = ViewCoordinator.getInstance();
                    viewCoordinator.showView(ViewPath.MAIN_VIEW);
                    viewCoordinator.showView(ViewPath.CONNECT_VIEW);
                    viewCoordinator.hideWindow(actionEvent);
                });
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                errorText.setText("Failed to use that port, try another one.");
            }
        });

    }
}
