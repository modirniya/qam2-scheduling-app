package edu.wgu.qam2schedulingapp.utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeHelper {

    public static Date utcToSystemLocalDate(Timestamp tsUTC) {
        LocalDateTime ldtUTC = tsUTC.toLocalDateTime();
        ZonedDateTime zdtLocal =
                ldtUTC.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
        return Date.from(zdtLocal.toInstant());

    }

    public static String getTimeZone() {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zone);
        String region = zone.getId();
        String offset = zonedDateTime.format(DateTimeFormatter.ofPattern("zzzz"));
        return region + " - " + offset;
    }

    public static ObservableList<String> getBusinessHours(int hoursOffset) {
        ObservableList<String> timeSlots = FXCollections.observableArrayList();
        var openingHour = 5;
        var closingHour = 18;
        for (int hour = openingHour + hoursOffset; hour <= closingHour + hoursOffset; hour++) {
            for (int mins = 0; mins < 4; mins++) {
                if (hour == closingHour + hoursOffset && mins != 0) break;
                timeSlots.add(hour + ":" + (mins != 0 ? (mins * 15) : "00"));
            }
        }
        return timeSlots;
    }
}
