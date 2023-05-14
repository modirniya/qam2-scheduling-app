package edu.wgu.qam2schedulingapp.utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TimeHelper {
    public static final SimpleDateFormat TABLE_DATE_FORMAT = new SimpleDateFormat("MM-dd-yy HH:mm");

    public static final String EDT_TO_UTC_OFFSET = "'+00:00', '-04:00'";

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
        var openingHour = 8 + hoursOffset + getTimezoneDifferenceToEST();
        var closingHour = 21 + hoursOffset + getTimezoneDifferenceToEST();
        int hour, minute;
        String time;
        for (double d = openingHour; d <= closingHour; d += 0.25) {
            hour = ((int) (d - (d % 1))) % 24;
            minute = (int) ((d % 1) * 60);
            time = (hour > 9 ? hour : ("0" + hour)) + ":" + (minute == 0 ? "00" : minute);
            timeSlots.add(time);
        }
        return timeSlots;
    }

    public static double getTimezoneDifferenceToEST() {
        ZoneId systemZone = ZoneId.systemDefault();
        ZoneId estZone = ZoneId.of("America/New_York");
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime systemZonedDateTime = now.atZone(systemZone);
        ZonedDateTime estZonedDateTime = now.atZone(estZone);
        double hoursDifference = ChronoUnit.MINUTES.between(systemZonedDateTime, estZonedDateTime);
        return hoursDifference / 60.0;
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
        LocalTime localTime = LocalTime.parse(strTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime ldt = LocalDateTime.of(localDate, localTime);
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}