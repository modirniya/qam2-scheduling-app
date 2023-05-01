module edu.wgu.qam2schedulingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    exports edu.wgu.qam2schedulingapp;
    exports edu.wgu.qam2schedulingapp.controller;
    exports edu.wgu.qam2schedulingapp.utility;
    opens edu.wgu.qam2schedulingapp.controller to javafx.fxml;
    // opens edu.wgu.qam2schedulingapp.utility to javafx.fxml;
    // opens edu.wgu.qam2schedulingapp to javafx.fxml;
}