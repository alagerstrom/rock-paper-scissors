package com.andreas.rockpaperscissors.viewcontroller;

import com.andreas.rockpaperscissors.controller.AppController;
import com.andreas.rockpaperscissors.model.PlayersObserver;
import com.andreas.rockpaperscissors.model.RoundObserver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import com.andreas.rockpaperscissors.model.ChatObserver;
import com.andreas.rockpaperscissors.util.Constants;
import com.andreas.rockpaperscissors.util.Logger;

import java.net.UnknownHostException;
import java.util.List;
import java.util.function.Consumer;

public class MainViewController implements ViewController<MainViewController.Delegate> {

    private AppController appController = AppController.getInstance();

    public interface Delegate {
        void connectButtonClicked();
    }

    private Delegate delegate;

    @Override
    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    @FXML
    Text portText;
    @FXML
    TextField messageField;
    @FXML
    TextArea messages;
    @FXML
    Text ipText;
    @FXML
    ListView playerList;

    public void initialize() {
        printMessage("Rock Paper Scissors game started");
        Logger.log("Main view initialized");

        appController.addPlayersObserver(new PlayersHandler());
        appController.addChatObserver(new ChatHandler());
        appController.addRoundObserver(new RoundHandler());
        initializeIpAndPortTexts();
        appController.sendPlayerInfo();
    }


    @FXML
    public void sendChatMessage() {
        String messageContent = messageField.getText();
        appController.sendChatMessage(messageContent);
        messageField.setText("");
    }

    private synchronized void printMessage(String message) {
        messages.appendText(message + "\n");
    }

    private void initializeIpAndPortTexts() {
        String portNumber = "" + appController.getLocalPort();
        portText.setText(portNumber);
        appController.getLocalHost(new LocalHostHandler());
    }

    @FXML
    public void playRock(){
        appController.sendPlayRock();
    }

    @FXML
    public void playPaper(){
        appController.sendPlayPaper();
    }

    @FXML
    public void playScissors(){
        appController.sendPlayScissors();

    }

    @FXML
    private void connectButtonClicked() {
        if (delegate != null)
            delegate.connectButtonClicked();
    }

    private class ChatHandler implements ChatObserver {
        @Override
        public void newMessage(String message) {
            Platform.runLater(() -> {
                printMessage(message);
            });
        }
    }

    private class PlayersHandler implements PlayersObserver {
        @Override
        public void allPlayers(List<String> allPlayers) {
            Platform.runLater(() -> {
                playerList.getItems().clear();
                playerList.getItems().addAll(allPlayers);
            });
        }

        @Override
        public void newPlayer(String playerName) {
            Platform.runLater(() -> {
                printMessage(playerName + " joined the game.");
            });
        }

        @Override
        public void playerLeft(String lostPlayer) {
            Platform.runLater(() -> {
                printMessage(lostPlayer + " left the game.");
            });
        }
    }

    private class RoundHandler implements RoundObserver{

        @Override
        public void draw() {
            Platform.runLater(()->{
                printMessage("The round was a draw");
            });
        }

        @Override
        public void victory(int roundScore, int totalScore) {
            Platform.runLater(()->{
                printMessage("You won! Round: " + roundScore + ", Total: " + totalScore);
            });
        }

        @Override
        public void loss() {
            Platform.runLater(()->{
                printMessage("You loose!");
            });

        }
    }

    private class LocalHostHandler implements Consumer {
        @Override
        public void accept(Object o) {
            ipText.setText(o.toString());
        }
    }
}
