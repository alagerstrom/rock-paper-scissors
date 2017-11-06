package com.andreas.rockpaperscissors.controller;

import com.andreas.rockpaperscissors.util.Constants;
import com.andreas.rockpaperscissors.util.Logger;
import com.andreas.rockpaperscissors.viewcontroller.ConnectViewController;
import com.andreas.rockpaperscissors.viewcontroller.MainViewController;
import com.andreas.rockpaperscissors.viewcontroller.StartViewController;
import com.andreas.rockpaperscissors.viewcontroller.ViewPath;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class ViewCoordinator implements
        StartViewController.Delegate,
        ConnectViewController.Delegate,
        MainViewController.Delegate {

    private static ViewCoordinator instance = new ViewCoordinator();
    private AppController controller = AppController.getInstance();

    private ViewCoordinator() {
    }

    public static ViewCoordinator getInstance() {
        return instance;
    }

    public void start() throws IOException {
        showView(ViewPath.START_VIEW);
    }

    private void hideWindow(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        node.getScene().getWindow().hide();
    }

    private void showView(ViewPath viewPath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath.name));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            Logger.log("Failed to show view " + viewPath);
        }
        Object viewController = loader.getController();

        if (viewController instanceof StartViewController) {
            ((StartViewController) viewController).setDelegate(this);
        } else if (viewController instanceof ConnectViewController) {
            ((ConnectViewController) viewController).setDelegate(this);
        } else if (viewController instanceof MainViewController) {
            ((MainViewController) viewController).setDelegate(this);
        } else {
            Logger.log("AppController was not a known instance");
        }
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(Constants.WINDOW_TITLE);
        stage.show();
    }

    @Override
    public void startGame(String name, int port, ActionEvent actionEvent, Consumer<Exception> onError) throws IOException {

        StartGameService startGameService = new StartGameService(AppController.getInstance(), port, name, onError);
        startGameService.setOnSucceeded(event -> {
            Logger.log("StartGameService complete");
            showView(ViewPath.MAIN_VIEW);
            showView(ViewPath.CONNECT_VIEW);
            hideWindow(actionEvent);
        });
        startGameService.start();

    }

    @Override
    public void skip(ActionEvent actionEvent) throws IOException {
        hideWindow(actionEvent);
    }

    @Override
    public void connect(String remoteHostString, int remotePort, ActionEvent actionEvent) throws IOException {
        controller.connectTo(remoteHostString, remotePort);
        hideWindow(actionEvent);
    }

    @Override
    public void connectButtonClicked() {
        showView(ViewPath.CONNECT_VIEW);
    }
}
