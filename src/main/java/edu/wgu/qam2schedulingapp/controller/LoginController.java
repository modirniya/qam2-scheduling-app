package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.repository.UserRepository;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.TimeHelper;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private static final String TAG = "LoginController";
    private static final String HOME_FXML = "/edu/wgu/qam2schedulingapp/view/home.fxml";
    public Label lbTitle;
    public Label lbUsername;
    public Label lbPassword;
    public Label lbTimeZone;
    public Label lbError;
    public TextField tfUsername;
    public PasswordField pfPassword;
    public Button btLogin;

    @Override
    public void initialize(URL url, ResourceBundle res) {
        Logs.initLog(TAG);
        try {
            res = ResourceBundle.getBundle("edu.wgu.qam2schedulingapp.bundle.language", Locale.getDefault());
        } catch (Exception e) {
            Logs.error(TAG, e.getMessage());
        }
        lbUsername.setText(res.getString("lbUsername"));
        lbPassword.setText(res.getString("lbPassword"));
        btLogin.setText(res.getString("btLogin"));
        lbError.setText(res.getString("errLogin"));

        String timeZoneText = res.getString("lbTimeZone") + "\t" + TimeHelper.getTimeZone();
        lbTimeZone.setText(timeZoneText);
    }

    public void login() {
        var username = tfUsername.getText();
        boolean loginSucceed = UserRepository.
                getInstance().loginUser(username, pfPassword.getText());
        if (loginSucceed) {
            try {
                Parent parent = FXMLLoader.load(
                        Objects.requireNonNull(getClass().getResource(HOME_FXML)));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) btLogin.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                Logs.error(TAG, "IOException occurred while loading home.fxml\n" + e);
            }
        } else {
            lbError.setVisible(true);
        }
        Logs.loginLog(username, loginSucceed);
    }
}
