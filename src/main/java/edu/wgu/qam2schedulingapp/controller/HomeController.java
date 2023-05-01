package edu.wgu.qam2schedulingapp.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {

    @FXML
    protected void onHelloButtonClick() {
        Platform.exit();
    }
}