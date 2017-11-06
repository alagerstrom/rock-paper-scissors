package com.andreas.rockpaperscissors.viewcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import com.andreas.rockpaperscissors.util.Constants;
import com.andreas.rockpaperscissors.util.Logger;

import java.io.IOException;

public class ConnectViewController implements ViewController<ConnectViewController.Delegate>{

    @FXML
    TextField hostField;
    @FXML
    TextField hostPortField;
    @FXML
    Text errorText;

    private Delegate delegate;

    public interface Delegate{
        void skip(ActionEvent actionEvent) throws IOException;
        void launchGame(String remoteHostString, int remotePort, ActionEvent actionEvent) throws IOException;
    }

    @Override
    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    @FXML
    public void skipConnect(ActionEvent actionEvent) throws IOException {
        if (delegate != null)
            delegate.skip(actionEvent);
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

            if (delegate != null){
                try {
                    delegate.launchGame(remoteHostString, remotePort, actionEvent);
                }catch (IOException e){
                    errorText.setText("Failed to connect.");
                }
            }else {
                Logger.log("Delegate was null");
            }
        } else {
            errorText.setText("You must enter both host and port.");
        }

    }


    public void initialize() {
        errorText.setText("");
        hostPortField.setText(Constants.DEFAULT_PORT + "");
        Logger.log("Join View initialized");
    }
}
