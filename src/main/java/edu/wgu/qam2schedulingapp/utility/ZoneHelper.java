package edu.wgu.qam2schedulingapp.utility;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZoneHelper {
    public static String getTimeZone() {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zone);
        String region = zone.getId();
        String offset = zonedDateTime.format(DateTimeFormatter.ofPattern("zzzz"));
        return region + " - " + offset;
    }
}
