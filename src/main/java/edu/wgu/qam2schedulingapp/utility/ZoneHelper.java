package edu.wgu.qam2schedulingapp.utility;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ZoneHelper {

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
}
