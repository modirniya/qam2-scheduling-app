package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.TimeHelper;
import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.repository.AppointmentRepository;
import edu.wgu.qam2schedulingapp.repository.ContactRepository;
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

/**
 * Controller for managing appointments.
 * <p>
 * This class is responsible for handling user actions related to appointments,
 * such as navigating to the appointment editor, adding, modifying, and deleting appointments.
 *
 * @author Parham Modirniya
 */
public class AppointmentController implements Initializable {
    public TableView<Appointment> tbAppointments;
    public TableColumn<Appointment, Date> tcStart;
    public TableColumn<Appointment, Date> tcEnd;
    public TableColumn<Appointment, Integer> tcContact;
    public ToggleGroup appFilter;
    public Label lbEvent;
    private static final String TAG = "AppointmentController";

    /**
     * A constant holding the path to the appointment editor FXML file.
     */
    private static final String APPOINTMENT_EDITOR_FXML = "/edu/wgu/qam2schedulingapp/view/appointment-editor.fxml";

    /**
     * The repository used for managing appointments.
     */
    private final AppointmentRepository repo = AppointmentRepository.getInstance();

    /**
     * Initializes the controller.
     * <p>
     * This method is called when the controller is first created. It initializes the
     * appointment table and sets up the cell factories for the table's columns.
     *
     * @param url            the location used to resolve relative paths for the root object, or null if the
     *                       location is not known
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not
     *                       localized
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initLog(TAG);
        repo.removeFilter();
        tbAppointments.setItems(repo.filteredAppointments);
        tcStart.setCellFactory(tableDateFormatCellFactory());
        tcEnd.setCellFactory(tableDateFormatCellFactory());
        tcContact.setCellFactory(tableContactCellFactory());
    }

    /**
     * Navigates to the home page.
     * <p>
     * This method closes the current stage and effectively navigates the user back to the home page.
     */
    public void navigateToHome() {
        Stage stage = (Stage) tbAppointments.getScene().getWindow();
        stage.close();
    }

    /**
     * Navigates to the appointment editor page.
     *
     * @param appointment An Appointment object to be edited. If the appointment is null,
     *                    a new appointment will be created in the editor.
     *                    <p>
     *                    This method opens the appointment editor stage. If an existing appointment is passed as the parameter,
     *                    its data will be loaded into the editor for modifications. If the parameter is null, the editor is set up
     *                    for creating a new appointment.
     */
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

    /**
     * Opens the appointment editor to create a new appointment.
     * <p>
     * This method is intended to be called when the user wishes to add a new appointment.
     * It uses the navigateToEditor method with null as an argument, which signifies the creation of a new appointment.
     */
    public void addAppointment() {
        navigateToEditor(null);
    }

    /**
     * Modifies the selected appointment.
     * <p>
     * This method retrieves the selected appointment from the table view and opens the appointment editor for modification.
     * If no appointment is selected, it displays an error message to the user.
     */
    public void modifyAppointment() {
        Appointment appointment = tbAppointments.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            lbEvent.setText("");
            navigateToEditor(appointment);
        } else
            lbEvent.setText("Unknown target: Select the appointment in the table and press modify again.");

    }

    /**
     * This method handles the deletion confirmation and the result of user's decision. It uses
     * lambda expressions to handle different user responses and perform actions
     * accordingly.
     *
     * <p> The first lambda expression is used as a Predicate to check if the user response
     * is {@link javafx.scene.control.ButtonType#OK}.
     *
     * <p> The second lambda is used as a Consumer that performs an action (removing an appointment
     * and showing an alert) when the user confirms the deletion.
     *
     * <p> The third lambda is used as a Runnable that updates a label text when the user aborts
     * the deletion.
     *
     * <p> This way of using lambda expressions improves readability and simplifies the code by
     * reducing the need for verbose anonymous class implementations.
     */
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

    /**
     * Filters the appointment list to show only the current week's appointments.
     * <p>
     * This method uses the AppointmentRepository's filterWeekly() method to apply a filter on the appointment list,
     * so that only the appointments of the current week are displayed in the table view.
     */
    public void showCurrentWeek() {
        repo.filterWeekly();
    }

    /**
     * Filters the appointment list to show only the current month's appointments.
     * <p>
     * This method uses the AppointmentRepository's filterMonthly() method to apply a filter on the appointment list,
     * so that only the appointments of the current month are displayed in the table view.
     */
    public void showCurrentMonth() {
        repo.filterMonthly();
    }

    /**
     * Displays all appointments without any filter.
     * <p>
     * This method uses the AppointmentRepository's removeFilter() method to remove any existing filter on the
     * appointment list, so that all appointments are displayed in the table view.
     */
    public void showAllAppointments() {
        repo.removeFilter();
    }

    /**
     * Provides a cell factory for the Contact column in the appointments table.
     * <p>
     * This method returns a Callback that creates a TableCell for displaying the contact name
     * associated with an appointment in the table view. The TableCell's text is updated based on
     * the contact ID of the appointment. If the ID is null or empty, the cell remains blank.
     *
     * @return a Callback object for creating TableCells.
     */
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

    /**
     * Provides a cell factory for the Date columns in the appointments table.
     * <p>
     * This method returns a Callback that creates a TableCell for displaying the start and end dates
     * associated with an appointment in the table view. The TableCell's text is updated based on
     * the date of the appointment. If the date is null or empty, the cell remains blank.
     *
     * @return a Callback object for creating TableCells.
     */
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
