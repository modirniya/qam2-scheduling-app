package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.TimeHelper;
import edu.wgu.qam2schedulingapp.model.*;
import edu.wgu.qam2schedulingapp.repository.AppointmentRepository;
import edu.wgu.qam2schedulingapp.repository.ContactRepository;
import edu.wgu.qam2schedulingapp.repository.CustomerRepository;
import edu.wgu.qam2schedulingapp.repository.UserRepository;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controller for the Appointment Editor view.
 * <p>
 * This controller manages the operations related to
 * the creation and modification of appointments in the application.
 * It allows for the initialization of the relevant form fields,
 * validation of user entries, and application of changes to the appointment data.
 * <p>
 * Implements the Initializable interface for setup upon loading the view.
 *
 * @author Parham Modirniya
 */
public class AppointmentEditorController implements Initializable {
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
    public Label lbTitle;
    private static final String TAG = "AppointmentEditorController";
    private EditorMode mode = EditorMode.Add;

    /**
     * Initializes the AppointmentEditorController.
     * <p>
     * This method is called to initialize the AppointmentEditorController after its root element has been
     * completely processed. It initializes the log, sets the ComboBox items and values, and applies cell
     * factories to certain ComboBoxes.
     *
     * @param url            The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logs.initLog(TAG);
        cbStartTime.setItems(TimeHelper.generateBusinessHours(0));
        cbEndTime.setItems(TimeHelper.generateBusinessHours(+1));
        cbCustomerId.setItems(CustomerRepository.getInstance().allCustomers);
        cbCustomerId.setCellFactory(customerCellFactory());
        cbUserId.setItems(UserRepository.getInstance().allUsers);
        cbUserId.setCellFactory(usernameCellFactory());
        cbUserId.setValue(UserRepository.getInstance().getCurrentUser());
        cbContact.setItems(ContactRepository.getInstance().allContacts);
    }

    /**
     * Updates the AppointmentEditorController user interface based on the provided Appointment.
     * <p>
     * This method is used to populate the UI fields with data from an existing appointment, setting the
     * editor mode to "Modify" or "Add" depending on whether an appointment is provided. If an appointment is
     * provided, its data is used to populate the fields of the editor.
     *
     * @param appointment The Appointment object containing the data to populate the UI fields.
     */
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
            dpStart.setValue(TimeHelper.toLocalDate(appointment.getStart()));
            cbStartTime.setDisable(false);
            cbStartTime.setValue(
                    TimeHelper.toTimeString(appointment.getStart()));
            dpEnd.setValue(TimeHelper.toLocalDate(appointment.getEnd()));
            cbEndTime.setDisable(false);
            cbEndTime.setValue(
                    TimeHelper.toTimeString(appointment.getEnd()));
        }
    }

    /**
     * Handles the event of a start date being chosen.
     * <p>
     * This method validates the chosen start date and end date. If the end date is null or is before
     * the start date, it sets the end date to be the same as the start date. It also disables the start
     * time ComboBox if the start date is null.
     */
    public void startDateChosen() {
        var startDate = dpStart.getValue();
        var endDate = dpEnd.getValue();
        if (endDate == null || endDate.isBefore(startDate))
            dpEnd.setValue(dpStart.getValue());
        cbStartTime.setDisable(startDate == null);
        Logs.info(TAG, "Start Date chosen: " + startDate);
    }

    /**
     * Handles the event of an end date being chosen.
     * <p>
     * This method disables the end time ComboBox if the end date is null.
     */
    public void endDateChosen() {
        cbEndTime.setDisable(dpEnd.getValue() == null);
        Logs.info(TAG, "End Date chosen: " + dpEnd.getValue());
    }

    /**
     * Applies changes to the appointment.
     * <p>
     * This method validates the entered appointment details and, if valid, constructs an Appointment
     * object and either adds a new appointment or modifies an existing one in the repository.
     * After applying the changes, it closes the appointment editor.
     */
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

    /**
     * Closes the appointment editor.
     * <p>
     * This method is used to close the AppointmentEditorController window without saving any changes.
     */
    public void cancel() {
        Logs.info(TAG, "Closing the appointment editor");
        Stage stage = (Stage) tfAppointmentId.getScene().getWindow();
        stage.close();
    }

    /**
     * Constructs an Appointment object from the form inputs.
     * <p>
     * This method is used to create a new Appointment instance based on the current form inputs. It assigns all relevant
     * properties of the Appointment using the current values in the form fields.
     *
     * @return Appointment object constructed from form inputs.
     */
    private Appointment constructAppointment() {
        Appointment ap = new Appointment();
        ap.setTitle(tfTitle.getText());
        ap.setType(tfType.getText());
        ap.setLocation(tfLocation.getText());
        ap.setDescription(tfDescription.getText());
        ap.setCustomerId(cbCustomerId.getValue().getId());
        ap.setUserId(cbUserId.getValue().getId());
        ap.setContactId(cbContact.getValue().getId());
        ap.setStart(TimeHelper.toDateTime(
                dpStart.getValue(), cbStartTime.getValue()));
        ap.setEnd(TimeHelper.toDateTime(
                dpEnd.getValue(), cbEndTime.getValue()));
        return ap;
    }

    /**
     * Checks if the form entries are valid.
     * <p>
     * This method validates the form inputs for creating or updating an appointment. It checks if all required fields
     * are filled out and if the entered data meets certain conditions (e.g., start time is before end time). If any
     * field is invalid, it constructs an error message which is then displayed in an alert dialog.
     *
     * @return Boolean indicating whether all form entries are valid.
     */
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
            var start = TimeHelper.toDateTime(dateStart, timeStart);
            var end = TimeHelper.toDateTime(dateEnd, timeEnd);
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

    /**
     * Validates the start and end dates for an appointment.
     * <p>
     * This method checks if the start date/time is before the end date/time and if the appointment overlaps with
     * any existing ones. If either of these conditions is not met, corresponding error messages are appended
     * to the passed StringBuilder.
     *
     * @param potentialErrors a StringBuilder to which error messages are appended.
     * @param start           the start date/time of the appointment.
     * @param end             the end date/time of the appointment.
     */
    private void validateDates(StringBuilder potentialErrors, Date start, Date end) {
        if (!(start.before(end)))
            potentialErrors.append(decorate("An appointment must end at least 15 minutes after it starts"));
        if (!(
                AppointmentRepository.getInstance().checkTimeSlotAvailability(
                        start, end, mode == EditorMode.Modify ? Integer.parseInt(tfAppointmentId.getText()) : -1)))
            potentialErrors.append(decorate("Time slot is not available since it is overlapping another appointment"));
    }

    /**
     * Factory method for creating a new ListCell that can be used in a ListView for Customer objects.
     * <p>
     * This method creates a new ListCell that displays the ID and name of a Customer object.
     * The text of the ListCell is set to the ID and name of the Customer if the Customer object is not null.
     *
     * @return a Callback that creates a new ListCell for Customer objects.
     */
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

    /**
     * Factory method for creating a new ListCell that can be used in a ListView for User objects.
     * <p>
     * This method creates a new ListCell that displays the ID and username of a User object.
     * The text of the ListCell is set to the ID and username of the User if the User object is not null.
     *
     * @return a Callback that creates a new ListCell for User objects.
     */
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

    /**
     * Decorates a given string entry for display purposes.
     * <p>
     * This method prepares the string for display in a UI context,
     * adding a bullet point and a newline character for readability.
     *
     * @param entry The string to be decorated.
     * @return A decorated version of the input string.
     */
    private String decorate(String entry) {
        return "\t‣ " + entry + "\n";
    }
}
