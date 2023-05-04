package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.utility.Logs;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {
    private static final String TAG = "AppointmentController";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initControllerLog(TAG);
    }
}
