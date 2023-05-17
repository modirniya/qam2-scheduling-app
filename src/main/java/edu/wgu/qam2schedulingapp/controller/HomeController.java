package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.TimeHelper;
import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.repository.AppointmentRepository;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller for the Home view of the scheduling app.
 * It handles the user interactions with the Home interface, manages navigation to different screens,
 * and checks for upcoming appointments.
 *
 * @author Parham Modirniya
 */
public class HomeController implements Initializable {
    private static final String RES_PATH = "/edu/wgu/qam2schedulingapp/view/";
    private static final String TAG = "HomeController";

    /**
     * Initializes the HomeController.
     * This method is called after all FXMLLoader has completely loaded the FXML.
     * It initializes the log and checks for upcoming appointments.
     *
     * @param url            A location used to resolve relative paths for the root object, or null.
     * @param resourceBundle The resources used to localize the root object, or null.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initLog(TAG);
        var repo = AppointmentRepository.getInstance();
        repo.fetchUpcomingAppointment();
        if (repo.filteredAppointments.size() == 0) {
            Logs.info(TAG, "No upcoming appointment");
        } else if (repo.filteredAppointments.size() == 1) {
            alertUpcomingAppointment(repo.filteredAppointments.get(0));
            Logs.info(TAG, "There is a upcoming appointment:\n" + repo.filteredAppointments.get(0).toString());
        } else {
            Logs.error(TAG, "There is inconsistency in appointments");
        }
    }

    /**
     * Shows the Customers screen.
     * It opens the Customers view in a new modal stage.
     *
     * @throws IOException If an I/O error occurs during the loading of the FXML file.
     */
    public void showCustomersScreen() throws IOException {
        Stage stageCustomer = new Stage();
        FXMLLoader loaderCustomer = new FXMLLoader(getClass().getResource(RES_PATH + "customer.fxml"));
        stageCustomer.initModality(Modality.APPLICATION_MODAL);
        stageCustomer.setScene(new Scene(loaderCustomer.load()));
        stageCustomer.showAndWait();
    }

    /**
     * Shows the Appointments screen.
     * It opens the Appointments view in a new modal stage.
     *
     * @throws IOException If an I/O error occurs during the loading of the FXML file.
     */
    public void showAppointmentScreen() throws IOException {
        Stage stageAppointment = new Stage();
        FXMLLoader loaderAppointment = new FXMLLoader(getClass().getResource(RES_PATH + "appointment.fxml"));
        stageAppointment.initModality(Modality.APPLICATION_MODAL);
        stageAppointment.setScene(new Scene(loaderAppointment.load()));
        stageAppointment.showAndWait();
    }

    /**
     * Shows the Reports screen.
     * It opens the Reports view in a new modal stage.
     *
     * @throws IOException If an I/O error occurs during the loading of the FXML file.
     */
    public void showReportScreen() throws IOException {
        Stage stageReport = new Stage();
        FXMLLoader loaderReport = new FXMLLoader(getClass().getResource(RES_PATH + "report.fxml"));
        stageReport.initModality(Modality.APPLICATION_MODAL);
        stageReport.setScene(new Scene(loaderReport.load()));
        stageReport.showAndWait();
    }

    /**
     * Exits the application.
     */
    public void exit() {
        Platform.exit();
    }

    /**
     * Alerts the user of an upcoming appointment.
     * It shows an alert dialog with the details of the upcoming appointment.
     *
     * @param appointment The upcoming appointment.
     */
    private void alertUpcomingAppointment(Appointment appointment) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upcoming appointment");
        alert.setHeaderText("There is an appointment within 15 minute");
        String content = "Appointment ID: " + appointment.getId() +
                         "\nAt: " + TimeHelper.TABLE_DATE_FORMAT.format(appointment.getStart()) +
                         "\nUser ID: " + appointment.getUserId();
        alert.setContentText(content);
        alert.showAndWait();
    }
}