package edu.wgu.qam2schedulingapp.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class HomeController {

    @FXML
    protected void onHelloButtonClick() {
        Platform.exit();
    }
}