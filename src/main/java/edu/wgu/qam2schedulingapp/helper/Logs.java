package edu.wgu.qam2schedulingapp.helper;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;

/**
 * Logs is a utility class responsible for handling all log operations within the application.
 * It provides methods to log information, warnings, errors, and login activity.
 * All logged data is output to the console and stored in a text file for future reference.
 *
 * @author Parham Modirniya
 */
public class Logs {
    /**
     * Enum representing the different types of logs that can be created.
     */
    private enum LogType {
        Info,
        Warning,
        Error
    }

    private static final String allLogsFileName = "all-logs.txt";
    private static final String loginActivityFileName = "login-activity.txt";

    /**
     * Records a log of the specified type, with the given origin and content.
     * The log is output to the console and stored in a file for future reference.
     *
     * @param type    the type of log to record
     * @param origin  the origin of the log
     * @param content the content of the log
     */
    private static void recordLog(LogType type, String origin, String content) {
        var buffer = new StringBuffer();
        switch (type) {
            case Info -> buffer.append("[INFO]");
            case Warning -> buffer.append("[WARNING] ");
            case Error -> buffer.append("[ERROR] ");
        }
        buffer.append(" -- ").append(origin);
        buffer.append(":\t").append(content);
        buffer.append("\t").append(ZonedDateTime.now().toLocalTime());
        System.out.println(buffer);
        recordIntoFile(allLogsFileName, buffer.toString());
    }

    /**
     * Records the specified content into a file with the given file name.
     *
     * @param fileName the name of the file to record the content into
     * @param content  the content to record
     */
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

    /**
     * Records a log of a login attempt.
     * The log includes the username and whether or not the login was successful.
     *
     * @param user         the username of the user who attempted to login
     * @param isSuccessful true if the login was successful, false otherwise
     */
    public static void loginLog(String user, boolean isSuccessful) {
        String buffer = "[LOGIN] --" + user + "-- Result: " +
                        (isSuccessful ? "SUCCESSFUL" : "UNSUCCESSFUL") +
                        "\t" + ZonedDateTime.now();
        recordIntoFile(loginActivityFileName, buffer);
    }

    /**
     * Records an information log with the given origin and content.
     *
     * @param origin  the origin of the log
     * @param content the content of the log
     */
    public static void info(String origin, String content) {
        recordLog(LogType.Info, origin, content);
    }

    /**
     * Records an error log with the given origin and content.
     *
     * @param origin  the origin of the log
     * @param content the content of the log
     */
    public static void error(String origin, String content) {
        recordLog(LogType.Error, origin, content);
    }

    /**
     * Records a warning log with the given origin and content.
     *
     * @param origin  the origin of the log
     * @param content the content of the log
     */
    public static void warning(String origin, String content) {
        recordLog(LogType.Warning, origin, content);
    }

    /**
     * Records an information log indicating the start of initialization for the specified origin.
     *
     * @param origin the origin of the log
     */
    public static void initLog(String origin) {
        info(origin, "Initializing...");
    }
}
