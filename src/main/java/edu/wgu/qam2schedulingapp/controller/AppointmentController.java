package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.repository.AppointmentRepository;
import edu.wgu.qam2schedulingapp.utility.Logs;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {
    private static final String TAG = "AppointmentController";

    private static final String APPOINTMENT_EDITOR_FXML = "/edu/wgu/qam2schedulingapp/view/appointment-editor.fxml";

    public TableView<Appointment> tbAppointments;
    public Label lbEvent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initLog(TAG);
        tbAppointments.setItems(AppointmentRepository.getInstance().allAppointments);
    }

    public void navigateToHome(ActionEvent actionEvent) {
        Stage stage = (Stage) tbAppointments.getScene().getWindow();
        stage.close();
    }

    private void navigateToEditor(Appointment appointment) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(APPOINTMENT_EDITOR_FXML));
        try {
            Parent parent = loader.load();
            AppointmentEditorController controller = loader.getController();
            controller.updateUI(appointment);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            Logs.error(TAG, "Loading appointment editor has failed");
        }
    }

    public void addAppointment(ActionEvent actionEvent) {
        navigateToEditor(null);
    }

    public void modifyAppointment(ActionEvent actionEvent) {
        Appointment appointment = tbAppointments.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            lbEvent.setText("");
            navigateToEditor(appointment);
        } else
            lbEvent.setText("Unknown target: Select the appointment in the table and press modify again.");

    }

    public void deleteAppointment(ActionEvent actionEvent) {
        Appointment appointment = tbAppointments.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            lbEvent.setText("");
            AppointmentRepository.getInstance().removeAppointment(appointment);
        } else
            lbEvent.setText("Unknown target: Select the appointment in the table and press modify again.");
    }

    public void generateReport(ActionEvent actionEvent) {
    }
}
