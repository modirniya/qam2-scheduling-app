package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.TimeHelper;
import edu.wgu.qam2schedulingapp.repository.UserRepository;
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

/**
 * This class is the controller for the Login view of the scheduling app.
 * It handles the user interactions with the Login interface, manages the login process,
 * and navigates to the Home screen upon successful login.
 *
 * @author Parham Modirniya
 * @since 2023-05-17
 */
public class LoginController implements Initializable {
    public TextField tfUsername;
    public PasswordField pfPassword;
    public Button btLogin;
    public Label lbTitle;
    public Label lbUsername;
    public Label lbPassword;
    public Label lbTimeZone;
    public Label lbError;
    private static final String TAG = "LoginController";
    private static final String HOME_FXML = "/edu/wgu/qam2schedulingapp/view/home.fxml";

    /**
     * Initializes the LoginController.
     * This method is called after all FXMLLoader has completely loaded the FXML.
     * It initializes the log and sets the text of the labels and buttons according to the current locale.
     *
     * @param url A location used to resolve relative paths for the root object, or null.
     * @param res The resources used to localize the root object, or null.
     */
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

        String timeZoneText = res.getString("lbTimeZone") + "\t" + TimeHelper.getSystemTimeZone();
        lbTimeZone.setText(timeZoneText);
    }

    /**
     * Handles the login process.
     * It checks the user credentials, if they are correct, it navigates to the Home screen.
     * If they are incorrect, it shows an error message.
     */
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
