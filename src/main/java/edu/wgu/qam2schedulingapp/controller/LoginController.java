package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.utility.LogsManager;
import edu.wgu.qam2schedulingapp.utility.ZoneHelper;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private static final String TAG = "LoginController";
    public Label lbUsername;
    public Label lbPassword;
    public TextField tfUsername;
    public TextField tfPassword;
    public Label lbTimeZone;
    public Label lbError;
    public Button btLogin;

    @Override
    public void initialize(URL url, ResourceBundle res) {
        try {
            res = ResourceBundle.getBundle("edu.wgu.qam2schedulingapp.bundle.language", Locale.getDefault());
        } catch (Exception e) {
            LogsManager.errorLog(TAG, e.getMessage());
        }
        lbUsername.setText(res.getString("lbUsername"));
        lbPassword.setText(res.getString("lbPassword"));
        btLogin.setText(res.getString("btLogin"));
        lbError.setText(res.getString("errLogin"));
        String timeZoneText = res.getString("lbTimeZone") + "\t" + ZoneHelper.getTimeZone();
        lbTimeZone.setText(timeZoneText);
    }
}
