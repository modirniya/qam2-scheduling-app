package edu.wgu.qam2schedulingapp.controller;

import edu.wgu.qam2schedulingapp.model.Appointment;
import edu.wgu.qam2schedulingapp.model.Contact;
import edu.wgu.qam2schedulingapp.repository.AppointmentRepository;
import edu.wgu.qam2schedulingapp.repository.ContactRepository;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class ReportController implements Initializable {
    public TableView<Appointment> tbAppointments;
    public TableColumn<Appointment, Date> tcStart;
    public TableColumn<Appointment, Date> tcEnd;
    public ComboBox<Contact> cbContact;
    private AppointmentRepository appointmentRepository = AppointmentRepository.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbContact.setItems(ContactRepository.getInstance().allContacts);
        var firstContact = ContactRepository.getInstance().allContacts.get(0);
        cbContact.setValue(firstContact);
        appointmentRepository.filterByContact(firstContact.getId());
        tbAppointments.setItems(appointmentRepository.allAppointments);
    }

    public void onTargetContactChange() {
        appointmentRepository.filterByContact(cbContact.getValue().getId());
    }
}
