package edu.wgu.qam2schedulingapp;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.SqlHelper;
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
        stage.setTitle("Scheduling Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Logs.info(TAG,"Starting the app");
        SqlHelper.connect();
        launch();
        SqlHelper.disconnect();
        Logs.info(TAG,"App is terminated successfully");
    }
}