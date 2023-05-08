package edu.wgu.qam2schedulingapp.utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.time.*;
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

    public static LocalDate dateToLocalDate(Date start) {
        LocalDateTime ldtStart =
                start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ldtStart.toLocalDate();
    }


    public static String dateToStringLocalTime(Date start) {
        LocalDateTime ldtStart =
                start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ldtStart.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static Timestamp dateToUTCTimestamp(Date date) {
        LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        ZonedDateTime zdtUTC = zdt.withZoneSameInstant(ZoneOffset.UTC);
        return Timestamp.valueOf(zdtUTC.toLocalDateTime());
    }

    public static Date strTimeAndDateToDate(LocalDate localDate, String strTime) {
        LocalTime localTime = LocalTime.parse(strTime, DateTimeFormatter.ofPattern("H:mm"));
        LocalDateTime ldt = LocalDateTime.of(localDate, localTime);
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
