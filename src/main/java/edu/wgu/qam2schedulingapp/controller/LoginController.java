package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.User;
import edu.wgu.qam2schedulingapp.repository.LoginRepository;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.ZoneHelper;
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
        Logs.initControllerLog(TAG);
        try {
            res = ResourceBundle.getBundle("edu.wgu.qam2schedulingapp.bundle.language", Locale.getDefault());
        } catch (Exception e) {
            Logs.error(TAG, e.getMessage());
        }
        lbUsername.setText(res.getString("lbUsername"));
        lbPassword.setText(res.getString("lbPassword"));
        btLogin.setText(res.getString("btLogin"));
        lbError.setText(res.getString("errLogin"));

        String timeZoneText = res.getString("lbTimeZone") + "\t" + ZoneHelper.getTimeZone();
        lbTimeZone.setText(timeZoneText);
    }

    public void login() {
        var user = new User(tfUsername.getText(), pfPassword.getText());
        boolean loginSucceed = LoginRepository.loginUser(user);
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
        Logs.loginLog(user.getUsername(), loginSucceed);
    }
}
