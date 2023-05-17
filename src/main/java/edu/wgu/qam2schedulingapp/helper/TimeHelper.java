package edu.wgu.qam2schedulingapp.helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * This class is a utility/helper class which provides various date and time related methods.
 * It primarily deals with time conversions between different timezones, and to and from different formats (Date, LocalDate, LocalTime, Timestamp).
 * It also provides methods to get business hours and timezone information.
 *
 * @author Parham Modirniya
 */
public class TimeHelper {
    public static final SimpleDateFormat TABLE_DATE_FORMAT = new SimpleDateFormat("MM-dd-yy HH:mm");

    /**
     * Converts a UTC Timestamp to the system's local date and time.
     *
     * @param tsUTC The UTC Timestamp to be converted.
     * @return The local Date corresponding to the UTC Timestamp.
     */
    public static Date utcToLocalDate(Timestamp tsUTC) {
        LocalDateTime ldtUTC = tsUTC.toLocalDateTime();
        ZonedDateTime zdtLocal =
                ldtUTC.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
        return Date.from(zdtLocal.toInstant());
    }

    /**
     * Retrieves the system's timezone in the format "Region - Offset".
     *
     * @return A string representing the system's timezone.
     */
    public static String getSystemTimeZone() {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zone);
        String region = zone.getId();
        String offset = zonedDateTime.format(DateTimeFormatter.ofPattern("zzzz"));
        return region + " - " + offset;
    }

    /**
     * Generates a list of business hours with 15 minute intervals, adjusted to the user's timezone.
     *
     * @param hoursOffset The number of hours to offset from the base business hours (8 AM to 9 PM EST).
     * @return An ObservableList of strings representing the business hours.
     */
    public static ObservableList<String> generateBusinessHours(int hoursOffset) {
        ObservableList<String> timeSlots = FXCollections.observableArrayList();
        var openingHour = 8 + hoursOffset + getESTTimeDifference();
        var closingHour = 21 + hoursOffset + getESTTimeDifference();
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

    /**
     * Calculates the timezone difference between the system's timezone and Eastern Standard Time (EST).
     *
     * @return The difference in hours as a double.
     */
    public static double getESTTimeDifference() {
        ZoneId systemZone = ZoneId.systemDefault();
        ZoneId estZone = ZoneId.of("America/New_York");
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime systemZonedDateTime = now.atZone(systemZone);
        ZonedDateTime estZonedDateTime = now.atZone(estZone);
        double hoursDifference = ChronoUnit.MINUTES.between(systemZonedDateTime, estZonedDateTime);
        return hoursDifference / 60.0;
    }

    /**
     * Converts a Date to a LocalDate in the system's timezone.
     *
     * @param start The Date to be converted.
     * @return The LocalDate corresponding to the Date.
     */
    public static LocalDate toLocalDate(Date start) {
        LocalDateTime ldtStart =
                start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ldtStart.toLocalDate();
    }

    /**
     * Converts a Date to a string representation of local time in the format "HH:mm".
     *
     * @param start The Date to be converted.
     * @return A string representing the local time.
     */
    public static String toTimeString(Date start) {
        LocalDateTime ldtStart =
                start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ldtStart.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Converts a Date to a UTC Timestamp.
     *
     * @param date The Date to be converted.
     * @return The UTC Timestamp corresponding to the Date.
     */
    public static Timestamp toUTCTimestamp(Date date) {
        LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        ZonedDateTime zdtUTC = zdt.withZoneSameInstant(ZoneOffset.UTC);
        return Timestamp.valueOf(zdtUTC.toLocalDateTime());
    }

    /**
     * Converts a string representation of time and a LocalDate to a Date.
     *
     * @param localDate The LocalDate part of the date-time.
     * @param strTime   The string representation of the time in the format "HH:mm".
     * @return The Date corresponding to the combined date-time.
     */
    public static Date toDateTime(LocalDate localDate, String strTime) {
        LocalTime localTime = LocalTime.parse(strTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime ldt = LocalDateTime.of(localDate, localTime);
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}