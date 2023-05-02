package edu.wgu.qam2schedulingapp;

import edu.wgu.qam2schedulingapp.utility.LogsManager;
import edu.wgu.qam2schedulingapp.utility.SqlDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SchedulingApp extends Application {
    private static final String TAG = "SchedulingApp";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SchedulingApp.class.getResource("view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        LogsManager.infoLog(TAG,"Starting the app");
        SqlDatabase.connect();
        launch();
        SqlDatabase.disconnect();
        LogsManager.infoLog(TAG,"App is terminated successfully");
    }
}