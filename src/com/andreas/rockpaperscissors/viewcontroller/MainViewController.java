package com.andreas.rockpaperscissors.viewcontroller;

import com.andreas.rockpaperscissors.controller.AppController;
import com.andreas.rockpaperscissors.model.GameObserver;
import com.andreas.rockpaperscissors.util.Constants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import com.andreas.rockpaperscissors.util.Logger;

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
    TextField messageField;
    @FXML
    TextArea messages;
    @FXML
    ListView playerList;
    @FXML
    Text totalText, roundText, ipText, portText, nameText, statusText;
    @FXML
    Button rockButton, scissorsButton, paperButton;


    public void initialize() {
        printMessage("Rock Paper Scissors game started");
        Logger.log("Main view initialized");

        nameText.setText(appController.getPlayerName());
        statusText.setText("Welcome, make your choice!");

        updateScoreText(0, 0);

        messages.setFocusTraversable(false);
        messages.setEditable(false);

        appController.addGameObserver(new GameHandler());
        initializeIpAndPortTexts();
        appController.sendPlayerInfo();
    }

    private void updateScoreText(int roundScore, int totalScore) {
        totalText.setText(Constants.TOTAL_PREFIX + totalScore);
        roundText.setText(Constants.ROUND_PREFIX + roundScore);
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
    public void playRock() {
        disableButtons();
        appController.sendPlayRock();
        statusText.setText("You played Rock!");
    }

    @FXML
    public void playPaper() {
        disableButtons();
        appController.sendPlayPaper();
        statusText.setText("You played Paper!");
    }

    @FXML
    public void playScissors() {
        disableButtons();
        appController.sendPlayScissors();
        statusText.setText("You played Scissors!");
    }

    @FXML
    private void connectButtonClicked() {
        if (delegate != null)
            delegate.connectButtonClicked();
    }

    private class GameHandler implements GameObserver {

        @Override
        public void allPlayers(List<String> allPlayers) {
            Platform.runLater(() -> {
                playerList.getItems().clear();
                playerList.getItems().addAll(allPlayers);
            });
        }

        @Override
        public void playerJoinedTheGame(String player) {
            Platform.runLater(() -> printMessage(player + " joined the game."));
        }

        @Override
        public void playerLeftTheGame(String player) {
            Platform.runLater(() -> printMessage(player + " left the game."));
        }

        @Override
        public void chatMessage(String message) {
            Platform.runLater(() -> printMessage(message));
        }

        @Override
        public void draw() {
            Platform.runLater(()-> statusText.setText("Draw!"));
        }

        @Override
        public void victory(int roundScore, int totalScore) {
            Platform.runLater(() -> {
                updateScoreText(roundScore, totalScore);
                statusText.setText("You win!");
            });

        }

        @Override
        public void loss() {
            Platform.runLater(() -> statusText.setText("You loose!"));

        }

        @Override
        public void newRound(int totalScore) {
            Platform.runLater(()->{
                enableButtons();
                updateScoreText(0, totalScore);
                statusText.setText("Make your choice!");
            });
        }
    }

    private void enableButtons() {
        rockButton.setDisable(false);
        paperButton.setDisable(false);
        scissorsButton.setDisable(false);
    }
    private void disableButtons(){
        rockButton.setDisable(true);
        paperButton.setDisable(true);
        scissorsButton.setDisable(true);
    }

    private class LocalHostHandler implements Consumer {
        @Override
        public void accept(Object o) {
            ipText.setText(o.toString());
        }
    }
}
