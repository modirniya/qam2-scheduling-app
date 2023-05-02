package edu.wgu.qam2schedulingapp.utility;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;

public class LogsManager {

    private enum LogType {
        Info,
        Warning,
        Error
    }

    private static final String allLogsFileName = "all-logs.txt";
    private static final String loginActivityFileName = "login-activity.txt";

    private static void recordLog(LogType type, String origin, String content) {
        var buffer = new StringBuffer();
        buffer.append(origin).append("\t");
        switch (type) {
            case Info -> buffer.append("[INFO]");
            case Warning -> buffer.append("[WARNING] ");
            case Error -> buffer.append("[ERROR] ");
        }
        buffer.append("\t").append(content);
        buffer.append("\t").append(ZonedDateTime.now());
        System.out.println(buffer);
        recordIntoFile(allLogsFileName, buffer.toString());
    }

    private static void recordIntoFile(String fileName, String content) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(fileName, true));
            writer.println(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error in LogManager: " + e);
        }
    }

    public static void loginLog(String user, boolean isSuccessful) {
        var buffer = new StringBuffer();
        buffer.append(user).append("'s attempt to login was ");
        buffer.append(isSuccessful ? "successful" : "unsuccessful");
        buffer.append("\t").append(ZonedDateTime.now());
        recordIntoFile(loginActivityFileName, buffer.toString());
    }

    public static void infoLog(String origin, String content) {
        recordLog(LogType.Info, origin, content);
    }

    public static void errorLog(String origin, String content) {
        recordLog(LogType.Error, origin, content);
    }

    public static void warningLog(String origin, String content) {
        recordLog(LogType.Warning, origin, content);
    }

}
