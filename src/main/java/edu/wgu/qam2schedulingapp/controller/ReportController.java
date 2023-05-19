package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.helper.TimeHelper;
import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.model.Contact;
import edu.wgu.qam2schedulingapp.model.Month;
import edu.wgu.qam2schedulingapp.model.User;
import edu.wgu.qam2schedulingapp.repository.AppointmentRepository;
import edu.wgu.qam2schedulingapp.repository.ContactRepository;
import edu.wgu.qam2schedulingapp.repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This class is the controller for the Report view of the scheduling app.
 * It handles the user interactions with the Report interface, manages the process of
 * fetching appointments data and presenting the reports.
 *
 * @author Parham Modirniya
 */

public class ReportController implements Initializable {
    public TableView<Appointment> tbAppointments;
    public TableColumn<Appointment, Date> tcStart;
    public TableColumn<Appointment, Date> tcEnd;
    public ComboBox<Contact> cbContact;
    public ComboBox<User> cbUser;
    public ComboBox<String> cbYear;
    public ComboBox<String> cbType;
    public ComboBox<Month> cbMonth;
    public Label lbCount;
    public Label lbSubtitle;
    private final AppointmentRepository repo = AppointmentRepository.getInstance();
    private static final String TAG = "ReportController";
    private final ObservableList<String> years = FXCollections.observableArrayList();
    public Tab tabContact;
    public Tab tabTypeMonth;
    public Tab tabUserYear;
    private boolean isInitComplete = false;

    /**
     * Initializes the ReportController.
     * This method is called after the FXMLLoader has completely loaded the FXML.
     * It initializes the reports components and fetches the initial data.
     *
     * @param url            A location used to resolve relative paths for the root object, or null.
     * @param resourceBundle The resources used to localize the root object, or null.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int i = 2000; i <= 2050; i++) {
            years.add(String.valueOf(i));
        }
        ObservableList<Month> months = FXCollections.observableArrayList(
                Arrays.stream(Month.values()).collect(Collectors.toList()));
        cbContact.setItems(ContactRepository.getInstance().allContacts);
        cbUser.setItems(UserRepository.getInstance().allUsers);
        cbType.setItems(repo.getAllTypes());
        cbMonth.setItems(months);
        cbYear.setItems(years);
        tbAppointments.setItems(repo.filteredAppointments);
        cbUser.setCellFactory(usernameCellFactory());
        tcStart.setCellFactory(tableDateFormatFactory());
        tcEnd.setCellFactory(tableDateFormatFactory());
        var firstContact = ContactRepository.getInstance().allContacts.get(0);
        isInitComplete = true;
        cbContact.setValue(firstContact);
        onTargetContactChange();
//        cbYear.setValue(String.valueOf(Year.now().getValue()));
//        updateSizeLabel();
    }

    /**
     * Updates the report according to the selected contact.
     * It fetches the appointments data by the selected contact and updates the table.
     */
    public void onTargetContactChange() {
        repo.fetchAppointmentsByContact(cbContact.getValue().getId());
        updateSizeLabel();
        lbSubtitle.setText("Report by contact");
    }

    /**
     * Updates the report according to the selected month.
     * It fetches the appointments data by the selected type and month if the type is selected, and updates the table.
     */
    public void onTargetMonthChange() {
        if (cbType.getValue() != null)
            reportByTypeMonth();
    }

    /**
     * Updates the report according to the selected type.
     * It fetches the appointments data by the selected type and month if the month is selected, and updates the table.
     */
    public void onTargetTypeChange() {
        if (cbMonth.getValue() != null)
            reportByTypeMonth();

    }

    /**
     * Updates the report according to the selected year.
     * It fetches the appointments data by the selected user and year if the user is selected, and updates the table.
     */
    public void onTargetYearChange() {
        if (cbUser.getValue() != null)
            reportByUserYear();
    }

    /**
     * Updates the report according to the selected user.
     * It fetches the appointments data by the selected user and year if the year is selected, and updates the table.
     */
    public void onTargetUserChange() {
        if (cbYear.getValue() != null)
            reportByUserYear();
    }

    /**
     * This method generates a report based on the selected type and month.
     * It fetches the appointments data by the selected type and month, updates the table,
     * and updates the size of the records found.
     */
    private void reportByTypeMonth() {
        repo.fetchAppointmentsByTypeMonth(cbType.getValue(), cbMonth.getValue());
        updateSizeLabel();
        lbSubtitle.setText("Report by type and month");
    }

    /**
     * This method generates a report based on the selected user and year.
     * It fetches the appointments data by the selected user and year, updates the table,
     * and updates the size of the records found.
     */
    private void reportByUserYear() {
        repo.fetchAppointmentsByUserYear(String.valueOf(cbUser.getValue().getId()),
                cbYear.getValue());
        updateSizeLabel();
        lbSubtitle.setText("Report by user and year");
    }

    /**
     * This method updates the label that shows the number of records found in the current filter view.
     * It gets the size of the filteredAppointments from the AppointmentRepository and
     * sets it to the lbCount label in the format of "{size} Record(s) Found".
     */
    private void updateSizeLabel() {
        lbCount.setText(repo.filteredAppointments.size() + " Record(s) Found");
    }

    /**
     * This method is used to navigate back to the home screen of the application.
     * It closes the current stage (window) which is assumed to be the 'Report' stage.
     * The method should be triggered by an action event, typically a button click.
     */
    public void navigateToHome() {
        Stage stage = (Stage) tbAppointments.getScene().getWindow();
        stage.close();
    }

    public void onTabSelectionChange(Event event) {
        if (isInitComplete) {
            if (event.getSource() == tabContact) onTargetContactChange();
            else if (event.getSource() == tabTypeMonth) onTargetMonthChange();
            else if (event.getSource() == tabUserYear) onTargetYearChange();
            updateSizeLabel();
        }
    }

    private Callback<TableColumn<Appointment, Date>, TableCell<Appointment, Date>> tableDateFormatFactory() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(Date date, boolean isDateEmpty) {
                super.updateItem(date, isDateEmpty);
                setText(isDateEmpty ? null : TimeHelper.TABLE_DATE_FORMAT.format(date));
            }
        };
    }

    private Callback<ListView<User>, ListCell<User>> usernameCellFactory() {
        return col -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean b) {
                super.updateItem(user, b);
                if (user != null)
                    setText(user.getUsername());
            }
        };
    }
}
