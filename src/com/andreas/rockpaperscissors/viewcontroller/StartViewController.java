package com.andreas.rockpaperscissors.viewcontroller;

import com.andreas.rockpaperscissors.util.Constants;
import com.andreas.rockpaperscissors.util.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.function.Consumer;

public class StartViewController implements ViewController<StartViewController.Delegate> {

    @FXML
    TextField nameField;
    @FXML
    TextField portField;
    @FXML
    Text errorText;

    public interface Delegate{
        void startGame(String name, int port, ActionEvent actionEvent, Consumer<Exception> onError) throws IOException;
    }

    private Delegate delegate;

    @Override
    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public void initialize(){
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
        try{
            port = Integer.parseInt(portNumber);
        }catch (NumberFormatException e){
            errorText.setText("Invalid port number");
            return;
        }

        if (delegate != null)
            delegate.startGame(playerName, port, actionEvent,
                    e -> errorText.setText("Failed to use that port, try another one."));
    }
}
