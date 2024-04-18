package loganalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogParser {
    private static final Pattern ipAddrPattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
    private static final Pattern timestampPattern = Pattern.compile("\\[(\\d{2}/[A-Za-z]{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} [+\\-]\\d{4})]");
    private static final Pattern userAgentPattern = Pattern.compile("\"([^\"]*)\"[^\"]*$");
    private static final Pattern allInOnePattern = Pattern.compile("\"(GET|HEAD|POST|PUT|DELETE|CONNECT|OPTIONS|TRACE|PATCH) \\/[a-zA-Z0-9.\\-_~!$&'()*+,;=:@]+ (HTTP|HTTPS)\\/\\d+(\\.\\d+)*\" \\d{3}(\\.\\d+)? \\d+");
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(LogParser.class.getName());
        String logFilePath = System.getProperty("user.dir") + "/logs/apache_nginx/access_log_50000.log";
        Path logPath = Paths.get(logFilePath);

        try {
            if (Files.exists(logPath)) {
                List<String> lines = Files.readAllLines(logPath);
                int lineCount = 0;
                for (String workingLine : lines) {
                    System.out.println(workingLine);
                    System.out.println("IP Address: " + parseIpAddress(workingLine));
                    System.out.println("Timestamp: " + parseTimestamp(workingLine));
                    System.out.println("UserAgent: " + parseUserAgent(workingLine));
                    String[] allInOne = parseAllInOne(workingLine);
                    if (allInOne != null) {
                        System.out.println("Method: " + allInOne[0]);
                        System.out.println("Path: " + allInOne[1]);
                        System.out.println("Protocol: " + allInOne[2]);
                        System.out.println("Status Code: " + allInOne[3]);
                        System.out.println("Content Length: " + allInOne[4]);
                    }
                    System.out.println("------------------------------------------------");
                    lineCount++;
                    if (lineCount >= 50000) {
                        break;
                    }
                }
            } else {
                logger.log(Level.SEVERE, "Log file not found at location " + logFilePath);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading log file: " + e.getMessage());
        }
    }

    public static String findFirstMatch(String line, String pattern) {
        Pattern patternToMatch = Pattern.compile(pattern);
        Matcher matcher = patternToMatch.matcher(line);
        if (matcher.find()) {
            return line.substring(matcher.start(), matcher.end());
        }
        return null;
    }

    public static String parseIpAddress(String logLine) {
        // chua handle truong hop ipv6
        return findFirstMatch(logLine, ipAddrPattern.pattern());
    }
    public static String parseTimestamp(String logLine) {
        String parsedLog = findFirstMatch(logLine, timestampPattern.pattern());
        if (parsedLog != null) {
            return parsedLog.substring(1, parsedLog.length() - 1);
        } else {
            return null;
        }
    }
    public static String parseUserAgent(String logLine) {
        String parsedLog = findFirstMatch(logLine, userAgentPattern.pattern());
        if (parsedLog != null) {
            return parsedLog.substring(1, parsedLog.length() - 1);
        } else {
            return null;
        }
    }
    public static String[] parseAllInOne(String logLine) {
        String parsedLog = findFirstMatch(logLine, allInOnePattern.pattern());
        if (parsedLog != null) {
            return (parsedLog.replace("\"", "")).split(" ");
        } else {
            return null;
        }
    }
}