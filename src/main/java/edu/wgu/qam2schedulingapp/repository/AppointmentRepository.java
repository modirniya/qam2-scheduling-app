package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.SqlHelper;
import edu.wgu.qam2schedulingapp.helper.TimeHelper;
import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.model.Month;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * This is a singleton class that fetches and stores all appointment-related data from the database.
 * The class provides methods to retrieve, add, update, and delete appointments.
 * <p>
 * The class utilizes the {@link SqlHelper} class to execute SQL queries and
 * the {@link Logs} class to log events during its operations.
 *
 * @author Parham Modirniya
 */
public class AppointmentRepository {
    public static final String BASE_SELECT = "SELECT * FROM client_schedule.appointments";

    /**
     * Enumeration that represents the filter types available for appointment data.
     * <p>
     * It has three types:
     * 1. None: No filter is applied.
     * 2. Weekly: Filter appointments on a weekly basis.
     * 3. Monthly: Filter appointments on a monthly basis.
     */
    private enum FilterType {
        None,
        Weekly,
        Monthly
    }

    private FilterType currentFilter = FilterType.None;
    private final String TAG = "AppointmentRepository";
    public final ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
    private final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    public ObservableList<String> allTypes = FXCollections.observableArrayList();
    private static AppointmentRepository instance;

    /**
     * Returns the singleton instance of the AppointmentRepository class.
     *
     * @return the singleton instance of the AppointmentRepository class
     */
    public static AppointmentRepository getInstance() {
        if (instance == null)
            instance = new AppointmentRepository();
        return instance;
    }

    /**
     * Constructs a new AppointmentRepository and fetches all appointments from the database.
     */
    private AppointmentRepository() {
        Logs.initLog(TAG);
        fetchAppointments(FilterType.None);
    }

    /**
     * Fetches appointments from the database based on the specified filter type.
     *
     * @param filterType the type of filter to apply
     */
    private void fetchAppointments(FilterType filterType) {
        currentFilter = filterType;
        String statement = BASE_SELECT;
        switch (currentFilter) {
            case Weekly -> statement += " WHERE YEARWEEK(CONVERT_TZ(Start, '+00:00', '-05:00'), 1) =" +
                                        " YEARWEEK(CONVERT_TZ(NOW(), '+00:00', '-05:00'), 1)";
            case Monthly -> statement += " WHERE MONTH(CONVERT_TZ(Start, '+00:00', '-05:00')) =" +
                                         " MONTH(CONVERT_TZ(NOW(), '+00:00', '-05:00'))";
        }
        executeFetch(statement);
        if (currentFilter == FilterType.None) {
            allAppointments.clear();
            allAppointments.addAll(filteredAppointments);
        }
    }

    /**
     * Fetches the upcoming appointment from the database.
     */
    public void fetchUpcomingAppointment() {
        executeFetch(BASE_SELECT + " WHERE Start BETWEEN UTC_TIMESTAMP() " +
                     "AND DATE_ADD(UTC_TIMESTAMP(), INTERVAL 15 MINUTE)");
    }

    /**
     * Fetches appointments from the database for the specified contact.
     *
     * @param contactId the ID of the contact to fetch appointments for
     */
    public void fetchAppointmentsByContact(int contactId) {
        executeFetch(BASE_SELECT + " WHERE Contact_ID = " + contactId);
    }

    /**
     * Fetches appointments from the database of the specified type and month.
     *
     * @param type  the type of appointments to fetch
     * @param month the month of appointments to fetch
     */
    public void fetchAppointmentsByTypeMonth(String type, Month month) {
        executeFetch(BASE_SELECT + " WHERE Type = '" + type +
                     "' AND MONTH(CONVERT_TZ(Start, '+00:00', '-05:00')) = " + month.getNumber());
    }

    /**
     * Fetches appointments from the database for the specified user and year.
     *
     * @param userId the ID of the user to fetch appointments for
     * @param year   the year of appointments to fetch
     */
    public void fetchAppointmentsByUserYear(String userId, String year) {
        executeFetch(BASE_SELECT + " WHERE User_ID = " + userId +
                     " AND YEAR(CONVERT_TZ(Start, '+00:00', '-05:00')) = " + year);
    }

    /**
     * Executes the specified SQL statement and adds the fetched appointments to the filteredAppointments list.
     *
     * @param statement the SQL statement to execute
     */
    private void executeFetch(String statement) {
        filteredAppointments.clear();
        try {
            ResultSet resultSet = SqlHelper.executeForResult(statement);
            while (resultSet.next()) {
                filteredAppointments.add(Appointment.fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Fetching appointments has failed");
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds the specified appointment to the database.
     *
     * @param ap the appointment to add
     */
    public void addAppointment(Appointment ap) {
        Logs.info(TAG, "Adding appointment to the database in progress:\n" + ap.toString());
        var username = UserRepository.getInstance().getCurrentUser().getUsername();
        PreparedStatement ps;
        var strStatement = """
                INSERT INTO client_schedule.appointments
                (Title,Description,Location,Type,
                Start,End,
                Customer_ID,User_ID,Contact_ID,
                Last_Update,Last_Updated_By,Create_Date,Created_By
                )
                VALUES(?,?,?,?,?,?,?,?,?,UTC_TIMESTAMP(),?,UTC_TIMESTAMP(),?)""";
        try {
            ps = SqlHelper.getConnection().prepareStatement(strStatement);
            ps.setString(11, username);
            populateCommonStatementsThenExecute(ap, ps, username);
        } catch (SQLException e) {
            Logs.error(TAG, "Adding appointment has failed");
            throw new RuntimeException(e);
        }

    }

    /**
     * Updates the specified appointment in the database.
     *
     * @param ap the appointment to update
     */
    public void updateAppointment(Appointment ap) {
        Logs.info(TAG, "Updating appointment in progress:\n" + ap.toString());
        PreparedStatement ps;
        try {
            String strStatement = """
                    UPDATE client_schedule.appointments SET
                    Title = ?,Description = ?,Location = ?,Type = ?,
                    Start = ?,End = ?,
                    Customer_ID = ?,User_ID = ?,Contact_ID = ?,
                    Last_Update = UTC_TIMESTAMP(),Last_Updated_By = ?
                    WHERE Appointment_ID = ?""";
            ps = SqlHelper.getConnection().prepareStatement(strStatement);
            ps.setInt(11, ap.getId());
            populateCommonStatementsThenExecute(ap, ps, UserRepository.getInstance().getCurrentUser().getUsername());
        } catch (SQLException e) {
            Logs.error(TAG, "Updating appointment has failed");
            throw new RuntimeException(e);
        }

    }

    /**
     * Removes the specified appointment from the database.
     *
     * @param appointment the appointment to remove
     */
    public void removeAppointment(Appointment appointment) {
        String strStatement = "DELETE FROM client_schedule.appointments WHERE Appointment_ID =" + appointment.getId();
        try {
            SqlHelper.getConnection().createStatement().executeUpdate(strStatement);
            fetchAppointments(currentFilter);
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while removing the appointment");
        }
    }

    /**
     * Removes all appointments of the specified customer from the database.
     *
     * @param customerId the ID of the customer to remove appointments for
     */
    public void removeAppointmentsOfCustomer(int customerId) {
        String statement = "DELETE FROM client_schedule.appointments WHERE Customer_ID = " + customerId;
        try {
            SqlHelper.getConnection().createStatement().executeUpdate(statement);
            fetchAppointments(currentFilter);
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while removing all appointments of a customer");
        }
    }

    /**
     * Applies a weekly filter to the fetched appointments.
     */
    public void filterWeekly() {
        fetchAppointments(FilterType.Weekly);
    }

    /**
     * Applies a monthly filter to the fetched appointments.
     */
    public void filterMonthly() {
        fetchAppointments(FilterType.Monthly);
    }

    /**
     * Removes any filter currently applied to the fetched appointments.
     */
    public void removeFilter() {
        fetchAppointments(FilterType.None);
    }

    /**
     * Populates the common parameters in the specified PreparedStatement, executes the statement, and refreshes the fetched appointments.
     *
     * @param ps       the PreparedStatement to populate and execute
     * @param username the username of the user executing the statement
     * @param ap       the appointment to use for populating the statement
     */
    private void populateCommonStatementsThenExecute(Appointment ap, PreparedStatement ps, String username) throws SQLException {
        ps.setString(1, ap.getTitle());
        ps.setString(2, ap.getDescription());
        ps.setString(3, ap.getLocation());
        ps.setString(4, ap.getType());
        ps.setTimestamp(5, TimeHelper.toUTCTimestamp(ap.getStart()));
        ps.setTimestamp(6, TimeHelper.toUTCTimestamp(ap.getEnd()));
        ps.setInt(7, ap.getCustomerId());
        ps.setInt(8, ap.getUserId());
        ps.setInt(9, ap.getContactId());
        ps.setString(10, username);
        ps.executeUpdate();
        ps.close();
        if (currentFilter != FilterType.None) allAppointments.add(ap);
        fetchAppointments(currentFilter);
    }

    /**
     * Checks the availability of the specified time slot.
     * The time slot is considered available if it does not overlap with any existing appointment (except for the appointment with the specified ID).
     *
     * @param start the start time of the time slot
     * @param end   the end time of the time slot
     * @param id    the ID of the appointment to ignore when checking availability
     * @return true if the time slot is available, false otherwise
     */
    public boolean checkTimeSlotAvailability(Date start, Date end, int id) {
        for (Appointment appointment : allAppointments) {
            if (appointment.getId() == id) continue;
            Date appointmentStart = appointment.getStart();
            Date appointmentEnd = appointment.getEnd();
            boolean startsAtSameTime = start.equals(appointmentStart);
            boolean endsAtSameTime = end.equals(appointmentEnd);
            boolean startsDuringAppointment = start.after(appointmentStart) && start.before(appointmentEnd);
            boolean endsDuringAppointment = end.after(appointmentStart) && end.before(appointmentEnd);
            boolean appointmentStartsDuring = appointmentStart.after(start) && appointmentStart.before(end);
            boolean appointmentEndsDuring = appointmentEnd.after(start) && appointmentEnd.before(end);
            if (startsAtSameTime || endsAtSameTime || startsDuringAppointment || endsDuringAppointment || appointmentStartsDuring || appointmentEndsDuring) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets a list of all unique types of appointments in the list of all appointments.
     *
     * @return an ObservableList containing all unique types of appointments
     */
    public ObservableList<String> getAllTypes() {
        ObservableSet<String> allTypesSet = FXCollections.observableSet();
        for (Appointment appointment : allAppointments)
            allTypesSet.add(appointment.getType());
        return FXCollections.observableArrayList(allTypesSet);
    }
}
