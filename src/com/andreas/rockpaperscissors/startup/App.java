package com.andreas.rockpaperscissors.startup;

import com.andreas.rockpaperscissors.viewcontroller.ViewCoordinator;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewCoordinator.getInstance().start();
    }
}
