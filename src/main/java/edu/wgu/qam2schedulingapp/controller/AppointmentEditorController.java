package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.*;
import edu.wgu.qam2schedulingapp.repository.AppointmentRepository;
import edu.wgu.qam2schedulingapp.repository.ContactRepository;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.repository.UserRepository;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.TimeHelper;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class AppointmentEditorController implements Initializable {
    private static final String TAG = "AppointmentEditorController";
    private EditorMode mode = EditorMode.Add;
    public Label lbTitle;
    public TextField tfAppointmentId;
    public TextField tfTitle;
    public TextField tfType;
    public TextField tfLocation;
    public TextField tfDescription;
    public ComboBox<Customer> cbCustomerId;
    public ComboBox<User> cbUserId;
    public ComboBox<Contact> cbContact;
    public ComboBox<String> cbStartTime;
    public ComboBox<String> cbEndTime;
    public DatePicker dpStart;
    public DatePicker dpEnd;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initLog(TAG);
        cbStartTime.setItems(TimeHelper.getBusinessHours(0));
        cbEndTime.setItems(TimeHelper.getBusinessHours(+1));
        cbCustomerId.setItems(CustomerRepository.getInstance().allCustomers);
        cbCustomerId.setCellFactory(customerCellFactory());
        cbUserId.setItems(UserRepository.getInstance().allUsers);
        cbUserId.setCellFactory(usernameCellFactory());
        cbUserId.setValue(UserRepository.getInstance().getCurrentUser());
        cbContact.setItems(ContactRepository.getInstance().allContacts);
    }

    public void updateUI(Appointment appointment) {
        Logs.info(TAG, "Updating AppointmentEditorController UI in " +
                       (appointment == null ? "adding" : "modifying") + " mode");
        if (appointment != null) {
            mode = EditorMode.Modify;
            lbTitle.setText("Modify Appointment");
            tfAppointmentId.setText(String.valueOf(appointment.getId()));
            tfTitle.setText(appointment.getTitle());
            tfType.setText(appointment.getType());
            tfLocation.setText(appointment.getLocation());
            tfDescription.setText(appointment.getDescription());
            cbCustomerId.setValue(
                    CustomerRepository.getInstance().getCustomerById(
                            appointment.getCustomerId()));
            cbUserId.setValue(
                    UserRepository.getInstance().getUserById(appointment.getUserId()));
            cbContact.setValue(
                    ContactRepository.getInstance().getContactById(appointment.getContactId()));
            dpStart.setValue(TimeHelper.dateToLocalDate(appointment.getStart()));
            cbStartTime.setDisable(false);
            cbStartTime.setValue(
                    TimeHelper.dateToStringLocalTime(appointment.getStart()));
            dpEnd.setValue(TimeHelper.dateToLocalDate(appointment.getEnd()));
            cbEndTime.setDisable(false);
            cbEndTime.setValue(
                    TimeHelper.dateToStringLocalTime(appointment.getEnd()));
        }
    }

    public void startDateChosen() {
        var startDate = dpStart.getValue();
        var endDate = dpEnd.getValue();
        if (endDate == null || endDate.isBefore(startDate))
            dpEnd.setValue(dpStart.getValue());
        cbStartTime.setDisable(startDate == null);
        Logs.info(TAG, "Start Date chosen: " + startDate);
    }

    public void endDateChosen(ActionEvent actionEvent) {
        cbEndTime.setDisable(dpEnd.getValue() == null);
        Logs.info(TAG, "End Date chosen: " + dpEnd.getValue());
    }

    public void applyChanges() {
        if (areEntriesValid()) {
            Appointment appointment = constructAppointment();
            AppointmentRepository repo = AppointmentRepository.getInstance();
            switch (mode) {
                case Add -> repo.addAppointment(appointment);
                case Modify -> {
                    appointment.setId(Integer.parseInt(tfAppointmentId.getText()));
                    repo.updateAppointment(appointment);
                }
            }
            cancel();
        }

    }

    public void cancel() {
        Logs.info(TAG, "Closing the appointment editor");
        Stage stage = (Stage) tfAppointmentId.getScene().getWindow();
        stage.close();
    }

    private Appointment constructAppointment() {
        Appointment ap = new Appointment();
        ap.setTitle(tfTitle.getText());
        ap.setType(tfType.getText());
        ap.setLocation(tfLocation.getText());
        ap.setDescription(tfDescription.getText());
        ap.setCustomerId(cbCustomerId.getValue().getId());
        ap.setUserId(cbUserId.getValue().getId());
        ap.setContactId(cbContact.getValue().getId());
        ap.setStart(TimeHelper.strTimeAndDateToDate(
                dpStart.getValue(), cbStartTime.getValue()));
        ap.setEnd(TimeHelper.strTimeAndDateToDate(
                dpEnd.getValue(), cbEndTime.getValue()));
        return ap;
    }

    private boolean areEntriesValid() {
        StringBuilder potentialErrors = new StringBuilder();
        if (tfTitle.getText().isBlank()) potentialErrors.append(decorate("Title cannot be empty"));
        if (tfType.getText().isBlank()) potentialErrors.append(decorate("Type cannot be empty"));
        if (tfLocation.getText().isBlank()) potentialErrors.append(decorate("Location cannot be empty"));
        if (tfDescription.getText().isBlank()) potentialErrors.append(decorate("Description cannot be empty"));
        if (cbCustomerId.getValue() == null) potentialErrors.append(decorate("Customer ID must be assigned"));
        if (cbUserId.getValue() == null) potentialErrors.append(decorate("User ID must be assigned"));
        if (cbContact.getValue() == null) potentialErrors.append(decorate("Contact ID must be assigned"));
        var dateStart = dpStart.getValue();
        var timeStart = cbStartTime.getValue();
        var dateEnd = dpEnd.getValue();
        var timeEnd = cbEndTime.getValue();
        if (dateStart == null) potentialErrors.append(decorate("Start date must be assigned"));
        if (timeStart == null) potentialErrors.append(decorate("Start time must be assigned"));
        if (dateEnd == null) potentialErrors.append(decorate("End date must be assigned"));
        if (timeEnd == null) potentialErrors.append(decorate("End time must be assigned"));
        if (dateStart != null && timeStart != null
            && dateEnd != null && timeEnd != null) {
            var start = TimeHelper.strTimeAndDateToDate(dateStart, timeStart);
            var end = TimeHelper.strTimeAndDateToDate(dateEnd, timeEnd);
            validateDates(potentialErrors, start, end);
        }
        if (!potentialErrors.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Entries are not valid");
            alert.setHeaderText("The following issue(s) need to be addressed before going any further");
            alert.setContentText(potentialErrors.toString());
            alert.showAndWait();
        }
        return potentialErrors.isEmpty();
    }

    private void validateDates(StringBuilder potentialErrors, Date start, Date end) {
        if (!(start.before(end)))
            potentialErrors.append(decorate("An appointment must end at least 15 minutes after it starts"));
        if (!(
                AppointmentRepository.getInstance().checkTimeSlotAvailability(
                        start, end, mode == EditorMode.Modify ? Integer.parseInt(tfAppointmentId.getText()) : -1)))
            potentialErrors.append(decorate("Time slot is not available since it is overlapping another appointment"));
    }

    private Callback<ListView<Customer>, ListCell<Customer>> customerCellFactory() {
        return col -> new ListCell<>() {
            @Override
            protected void updateItem(Customer customer, boolean b) {
                super.updateItem(customer, b);
                if (customer != null)
                    setText(customer.getId() + " - " + customer.getName());
            }
        };
    }

    private Callback<ListView<User>, ListCell<User>> usernameCellFactory() {
        return col -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean b) {
                super.updateItem(user, b);
                if (user != null)
                    setText(user.getId() + " - " + user.getUsername());
            }
        };
    }

    private String decorate(String entry) {
        return "\t‣ " + entry + "\n";
    }
}
