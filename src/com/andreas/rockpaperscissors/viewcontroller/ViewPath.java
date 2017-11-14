package com.andreas.rockpaperscissors.viewcontroller;

public enum ViewPath {
    MAIN_VIEW("/view/main_view.fxml"),
    START_VIEW("/view/start_view.fxml"),
    CONNECT_VIEW("/view/connect_view.fxml");

    public final String name;

    ViewPath(String s) {
        this.name = s;
    }
}
