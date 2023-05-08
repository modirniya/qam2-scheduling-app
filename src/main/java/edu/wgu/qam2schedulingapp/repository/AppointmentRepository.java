package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.SqlDatabase;
import edu.wgu.qam2schedulingapp.utility.TimeHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AppointmentRepository {
    private final String TAG = "AppointmentRepository";
    public final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static AppointmentRepository instance;

    private AppointmentRepository() {
        Logs.initLog(TAG);
        fetchAllAppointments();
    }

    public static AppointmentRepository getInstance() {
        if (instance == null)
            instance = new AppointmentRepository();
        return instance;
    }

    private void fetchAllAppointments() {
        allAppointments.clear();
        try {
            String statement = "SELECT * FROM client_schedule.appointments";
            ResultSet resultSet = SqlDatabase.executeForResult(statement);
            while (resultSet.next()) {
                allAppointments.add(Appointment.fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Fetching all appointments has failed");
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
            ps = SqlDatabase.getConnection().prepareStatement(strStatement);
            ps.setString(11, username);
            populateCommonsThenExecute(ap, ps, username);
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
            ps = SqlDatabase.getConnection().prepareStatement(strStatement);
            ps.setInt(11, ap.getId());
            populateCommonsThenExecute(ap, ps, UserRepository.getInstance().getCurrentUser().getUsername());
        } catch (SQLException e) {
            Logs.error(TAG, "Updating appointment has failed");
            throw new RuntimeException(e);
        }

    }

    public void removeAppointment(Appointment appointment) {
        String strStatement = "DELETE FROM client_schedule.appointments WHERE Appointment_ID =" + appointment.getId();
        try {
            SqlDatabase.getConnection().createStatement().executeUpdate(strStatement);
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while deleting the customer");
        } finally {
            fetchAllAppointments();
        }
    }

    private void populateCommonsThenExecute(Appointment ap, PreparedStatement ps, String username) throws SQLException {
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
        fetchAllAppointments();
    }
}
