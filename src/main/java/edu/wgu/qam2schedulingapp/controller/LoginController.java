package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.utility.LogsManager;
import javafx.fxml.Initializable;

import java.net.URL;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private static final String TAG = "LoginController";

    @Override
    public void initialize(URL url, ResourceBundle res) {
        //ResourceBundle.getBundle("lang", Locale.getDefault());
        LogsManager.infoLog(TAG, ZoneId.systemDefault().getDisplayName(TextStyle.FULL,Locale.getDefault()));
    }
}
