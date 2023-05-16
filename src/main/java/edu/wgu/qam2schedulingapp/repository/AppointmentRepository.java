package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.model.Month;
import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.SqlHelper;
import edu.wgu.qam2schedulingapp.helper.TimeHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AppointmentRepository {
    public static final String BASE_SELECT = "SELECT * FROM client_schedule.appointments";

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

    public static AppointmentRepository getInstance() {
        if (instance == null)
            instance = new AppointmentRepository();
        return instance;
    }

    private AppointmentRepository() {
        Logs.initLog(TAG);
        fetchAppointments(FilterType.None);
    }

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

    public void fetchUpcomingAppointment() {
        executeFetch(BASE_SELECT + " WHERE Start BETWEEN UTC_TIMESTAMP() " +
                     "AND DATE_ADD(UTC_TIMESTAMP(), INTERVAL 15 MINUTE)");
    }

    public void fetchAppointmentsByContact(int contactId) {
        executeFetch(BASE_SELECT + " WHERE Contact_ID = " + contactId);
    }

    public void fetchAppointmentsByTypeMonth(String type, Month month) {
        executeFetch(BASE_SELECT + " WHERE Type = '" + type +
                     "' AND MONTH(CONVERT_TZ(Start, '+00:00', '-05:00')) = " + month.getNumber());
    }

    public void fetchAppointmentsByUserYear(String userId, String year) {
        executeFetch(BASE_SELECT + " WHERE User_ID = " + userId +
                     " AND YEAR(CONVERT_TZ(Start, '+00:00', '-05:00')) = " + year);
    }

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

    public void removeAppointment(Appointment appointment) {
        String strStatement = "DELETE FROM client_schedule.appointments WHERE Appointment_ID =" + appointment.getId();
        try {
            SqlHelper.getConnection().createStatement().executeUpdate(strStatement);
            fetchAppointments(currentFilter);
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while removing the appointment");
        }
    }

    public void removeAppointmentsOfCustomer(int customerId) {
        String statement = "DELETE FROM client_schedule.appointments WHERE Customer_ID = " + customerId;
        try {
            SqlHelper.getConnection().createStatement().executeUpdate(statement);
            fetchAppointments(currentFilter);
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while removing all appointments of a customer");
        }
    }

    public void filterWeekly() {
        fetchAppointments(FilterType.Weekly);
    }

    public void filterMonthly() {
        fetchAppointments(FilterType.Monthly);
    }

    public void removeFilter() {
        fetchAppointments(FilterType.None);
    }

    private void populateCommonStatementsThenExecute(Appointment ap, PreparedStatement ps, String username) throws SQLException {
        ps.setString(1, ap.getTitle());
        ps.setString(2, ap.getDescription());
        ps.setString(3, ap.getLocation());
        ps.setString(4, ap.getType());
        ps.setTimestamp(5, TimeHelper.dateToUTCTimestamp(ap.getStart()));
        ps.setTimestamp(6, TimeHelper.dateToUTCTimestamp(ap.getEnd()));
        ps.setInt(7, ap.getCustomerId());
        ps.setInt(8, ap.getUserId());
        ps.setInt(9, ap.getContactId());
        ps.setString(10, username);
        ps.executeUpdate();
        ps.close();
        if (currentFilter != FilterType.None) allAppointments.add(ap);
        fetchAppointments(currentFilter);
    }

    public boolean checkTimeSlotAvailability(Date start, Date end, int id) {
        for (Appointment appointment : allAppointments) {
            if (appointment.getId() == id) continue;
            Date appointmentStart = appointment.getStart();
            Date appointmentEnd = appointment.getEnd();
            if ((start.equals(appointmentStart)) || (end.equals(appointmentEnd)) ||
                (start.after(appointmentStart) && start.before(appointmentEnd)) ||
                (end.after(appointmentStart) && end.before(appointmentEnd)) ||
                (appointmentStart.after(start) && appointmentStart.before(end)) ||
                (appointmentEnd.after(start) && appointmentEnd.before(end))) {
                return false;
            }
        }
        return true;
    }

    public ObservableList<String> getAllTypes() {
        ObservableSet<String> allTypesSet = FXCollections.observableSet();
        for (Appointment appointment : allAppointments)
            allTypesSet.add(appointment.getType());
        return FXCollections.observableArrayList(allTypesSet);
    }
}
