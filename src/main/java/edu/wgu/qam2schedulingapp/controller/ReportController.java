package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.model.Contact;
import edu.wgu.qam2schedulingapp.model.Month;
import edu.wgu.qam2schedulingapp.model.User;
import edu.wgu.qam2schedulingapp.repository.AppointmentRepository;
import edu.wgu.qam2schedulingapp.repository.ContactRepository;
import edu.wgu.qam2schedulingapp.repository.UserRepository;
import edu.wgu.qam2schedulingapp.helper.TimeHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.time.Year;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReportController implements Initializable {
    private static final String TAG = "ReportController";
    public TableView<Appointment> tbAppointments;
    public TableColumn<Appointment, Date> tcStart;
    public TableColumn<Appointment, Date> tcEnd;
    public ComboBox<Contact> cbContact;
    private final AppointmentRepository repo = AppointmentRepository.getInstance();
    public Label lbCount;
    public ComboBox<User> cbUser;
    public ComboBox<String> cbYear;
    public ComboBox<String> cbType;
    public ComboBox<Month> cbMonth;
    private final ObservableList<String> years = FXCollections.observableArrayList();
    public Label lbSubtitle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbContact.setItems(ContactRepository.getInstance().allContacts);
        var firstContact = ContactRepository.getInstance().allContacts.get(0);
        cbContact.setValue(firstContact);
        cbUser.setItems(UserRepository.getInstance().allUsers);
        cbUser.setCellFactory(usernameCellFactory());
        cbType.setItems(repo.getAllTypes());
        tcStart.setCellFactory(tableDateFormatFactory());
        tcEnd.setCellFactory(tableDateFormatFactory());
        repo.fetchAppointmentsByContact(firstContact.getId());
        tbAppointments.setItems(repo.filteredAppointments);
        ObservableList<Month> months = FXCollections.observableArrayList(
                Arrays.stream(Month.values()).collect(Collectors.toList()));
        cbMonth.setItems(months);
        for (int i = 2000; i <= 2050; i++) {
            years.add(String.valueOf(i));
        }
        cbYear.setItems(years);
        cbYear.setValue(String.valueOf(Year.now().getValue()));
        updateSizeLabel();
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

    public void onTargetContactChange() {
        lbSubtitle.setText("Report by contact");
        repo.fetchAppointmentsByContact(cbContact.getValue().getId());
        updateSizeLabel();
    }

    public void onTargetMonthChange() {
        if (cbType.getValue() != null)
            reportByTypeMonth();
    }

    public void onTargetTypeChange() {
        if (cbMonth.getValue() != null)
            reportByTypeMonth();

    }

    public void onTargetYearChange() {
        if (cbUser.getValue() != null)
            reportByUserYear();
    }

    public void onTargetUserChange() {
        if (cbYear.getValue() != null)
            reportByUserYear();
    }

    private void reportByTypeMonth() {
        lbSubtitle.setText("Report by type and month");
        repo.fetchAppointmentsByTypeMonth(cbType.getValue(), cbMonth.getValue());
        updateSizeLabel();
    }

    private void reportByUserYear() {
        lbSubtitle.setText("Report by user and year");
        repo.fetchAppointmentsByUserYear(String.valueOf(cbUser.getValue().getId()),
                cbYear.getValue());
        updateSizeLabel();
    }

    private void updateSizeLabel() {
        lbCount.setText(repo.filteredAppointments.size() + " Record(s) Found");
    }

    public void navigateToHome() {
        Stage stage = (Stage) tbAppointments.getScene().getWindow();
        stage.close();
    }
}
