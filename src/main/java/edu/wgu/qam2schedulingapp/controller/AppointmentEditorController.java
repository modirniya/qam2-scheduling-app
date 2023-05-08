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

import java.net.URL;
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
        cbUserId.setItems(UserRepository.getInstance().allUsers);
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
            cbCustomerId.setValue(Customer.withOnlyId(appointment.getCustomerId()));
            cbUserId.setValue(User.withOnlyId(appointment.getUserId()));
            cbContact.setValue(Contact.withOnlyId(appointment.getContactId()));
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

    public void startDateChosen(ActionEvent actionEvent) {
        var datePicker = (DatePicker) actionEvent.getTarget();
        cbStartTime.setDisable(datePicker.getValue() == null);
        Logs.info(TAG, "Start Date chosen: " + datePicker.getValue());
    }

    public void endDateChosen(ActionEvent actionEvent) {
        var datePicker = (DatePicker) actionEvent.getTarget();
        cbEndTime.setDisable(datePicker.getValue() == null);
        Logs.info(TAG, "End Date chosen: " + datePicker.getValue());
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
        if (tfTitle.getText().isBlank()) potentialErrors.append("\t‣ Title cannot be empty\n");
        if (tfType.getText().isBlank()) potentialErrors.append("\t‣ Type cannot be empty\n");
        if (tfLocation.getText().isBlank()) potentialErrors.append("\t‣ Location cannot be empty\n");
        if (tfDescription.getText().isBlank()) potentialErrors.append("\t‣ Description cannot be empty\n");
        if (cbCustomerId.getValue() == null) potentialErrors.append("\t‣ Customer ID must be assigned\n");
        if (cbUserId.getValue() == null) potentialErrors.append("\t‣ User ID must be assigned\n");
        if (cbContact.getValue() == null) potentialErrors.append("\t‣ Contact ID must be assigned\n");
        if (dpStart.getValue() == null) potentialErrors.append("\t‣ Start date must be assigned\n");
        if (cbStartTime.getValue() == null) potentialErrors.append("\t‣ Start time must be assigned\n");
        if (dpEnd.getValue() == null) potentialErrors.append("\t‣ End date must be assigned\n");
        if (cbEndTime.getValue() == null) potentialErrors.append("\t‣ End time must be assigned\n");
        if (!potentialErrors.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Entries are not valid");
            alert.setHeaderText("The following issues needs to be addressed before going any further:");
            alert.setContentText(potentialErrors.toString());
            alert.showAndWait();
        }
        return potentialErrors.isEmpty();
    }


}
