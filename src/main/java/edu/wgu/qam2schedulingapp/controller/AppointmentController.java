package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.repository.AppointmentRepository;
import edu.wgu.qam2schedulingapp.repository.ContactRepository;
import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.TimeHelper;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {
    private static final String TAG = "AppointmentController";
    private static final String APPOINTMENT_EDITOR_FXML = "/edu/wgu/qam2schedulingapp/view/appointment-editor.fxml";
    private final AppointmentRepository repo = AppointmentRepository.getInstance();
    public TableView<Appointment> tbAppointments;
    public Label lbEvent;
    public TableColumn<Appointment, Date> tcStart;
    public TableColumn<Appointment, Date> tcEnd;
    public TableColumn<Appointment, Integer> tcContact;
    public ToggleGroup appFilter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initLog(TAG);
        repo.removeFilter();
        tbAppointments.setItems(repo.filteredAppointments);
        tcStart.setCellFactory(tableDateFormatCellFactory());
        tcEnd.setCellFactory(tableDateFormatCellFactory());
        tcContact.setCellFactory(tableContactCellFactory());
    }

    public void navigateToHome() {
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

    public void addAppointment() {
        navigateToEditor(null);
    }

    public void modifyAppointment() {
        Appointment appointment = tbAppointments.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            lbEvent.setText("");
            navigateToEditor(appointment);
        } else
            lbEvent.setText("Unknown target: Select the appointment in the table and press modify again.");

    }

    public void deleteAppointment() {
        lbEvent.setText("");
        Appointment appointment = tbAppointments.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Removing appointment");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This appointment information will be gone irreversibly.");
            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresentOrElse(
                    bt -> {
                        repo.removeAppointment(appointment);
                        var innerAlert = new Alert(Alert.AlertType.INFORMATION);
                        innerAlert.setTitle("Deletion successful");
                        innerAlert.setHeaderText("Appointment was successfully removed from the database");
                        innerAlert.setContentText("Appointment ID: " + appointment.getId()
                                                  + "\nType: " + appointment.getType());
                        innerAlert.showAndWait();
                    }, () -> lbEvent.setText("Deletion aborted: No changes has been made to the database."));
        } else
            lbEvent.setText("Unknown target: Select the appointment in the table and press modify again.");
    }

    public void showCurrentWeek() {
        repo.filterWeekly();
    }

    public void showCurrentMonth() {
        repo.filterMonthly();
    }

    public void showAllAppointments() {
        repo.removeFilter();
    }

    private Callback<TableColumn<Appointment, Integer>, TableCell<Appointment, Integer>> tableContactCellFactory() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer id, boolean isContactEmpty) {
                super.updateItem(id, isContactEmpty);
                setText(isContactEmpty ? null :
                        ContactRepository.getInstance().getContactById(id).getName());
            }
        };
    }

    private Callback<TableColumn<Appointment, Date>, TableCell<Appointment, Date>> tableDateFormatCellFactory() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(Date date, boolean isDateEmpty) {
                super.updateItem(date, isDateEmpty);
                setText(isDateEmpty ? null : TimeHelper.TABLE_DATE_FORMAT.format(date));
            }
        };
    }
}
