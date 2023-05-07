package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.SqlDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppointmentRepository {
    private final String TAG = "AppointmentRepository";
    public final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static AppointmentRepository instance;

    private AppointmentRepository() {
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
            while (resultSet.next()){
                allAppointments.add(Appointment.fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Fetching all appointments has failed");
            throw new RuntimeException(e);
        }
    }

    public void addAppointment() {
    }

    public void updateAppointment() {
    }

    public void removeAppointment() {
    }
}
