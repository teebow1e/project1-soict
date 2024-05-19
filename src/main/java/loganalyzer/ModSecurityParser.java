package loganalyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import csvgenerator.CsvGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModSecurityParser {
    private ModSecurityParser() {
        throw new IllegalStateException("Utility class");
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(ModSecurityParser.class.getName());
        String logFilePath = System.getProperty("user.dir") + "/logs/modsecurity/modsec_audit.json";
        Path logPath = Paths.get(logFilePath);
        ObjectMapper objectMapper = new ObjectMapper();

        if (Files.exists(logPath)) {
            LinkedList<ModSecurity> logList = new LinkedList<>();
            try (BufferedReader reader = Files.newBufferedReader(logPath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    JsonNode jsonNode = objectMapper.readTree(line);
                    String timestamp = jsonNode.path("transaction").path("time").asText();
                    String transactionId = jsonNode.path("transaction").path("transaction_id").asText();
                    String remoteAddress = jsonNode.path("transaction").path("remote_address").asText();
                    String remotePort = String.valueOf(jsonNode.path("transaction").path("remote_port").asInt());
                    String requestLine = jsonNode.path("request").path("request_line").asText();
                    String auditData = jsonNode.path("audit_data").path("messages").get(0).asText();
                    JsonNode versionSection = jsonNode.path("audit_data").path("producer");
                    String potentialVersion;
                    if (versionSection.isMissingNode() || versionSection.isNull()) {
                        potentialVersion = "";
                    } else {
                        potentialVersion = parseVersion(versionSection.get(0).asText());
                    }
                    String attackType = parseAttackType(auditData);
                    String attackMsg = parseAttackMsg(auditData);
                    String attackData = parseAttackData(auditData);
                    String severity = parseSeverity(auditData);

                    logList.add(new ModSecurity(
                        potentialVersion,
                        timestamp,
                        transactionId,
                        remoteAddress,
                        remotePort,
                        requestLine,
                        attackType,
                        attackMsg,
                        attackData,
                        severity
                    ));
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error reading the log file");
            }
            try {
                System.out.println("generating csv now..");
                CsvGenerator.generateCSVModSecurity(logList);
                System.out.println("done generate");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error generating CSV: {0}", e.getMessage());
            }
        } else {
            logger.log(Level.SEVERE, "Log file not found at location {0}", logFilePath);
        }
    }

    private static String parseAttackType(String data) {
        String regex = "\\[file\\s+\"([^\"]*)\"\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            String filePath = matcher.group(1);
            String filename = filePath.substring(filePath.lastIndexOf('/') + 1);
            return filename.replace(".conf", "");
        } else {
            return null;
        }
    }

    private static String parseAttackMsg(String data) {
        String regex = "\\[msg\\s+\"([^\"]*)\"\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private static String parseAttackData(String logEntry) {
        String regex = "\\[data\\s+\"([^\"]*)\"\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(logEntry);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private static String parseSeverity(String data) {
        String regex = "\\[severity\\s+\"([^\"]*)\"\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private static String parseVersion(String data) {
        String regex = "(?<=\\/)(\\d+\\.\\d+\\.\\d+)(?= \\()";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}

// todo: hoàn thành parser cho modsec + gen log modsec để test
// todo: suy nghĩ về phương pháp đọc file để gen + benchmark
// todo: suy nghĩ về cách detect file đã dc thêm data/xóa data đi
// todo: bảng không cần phải đọc từ csv mà đọc từ đối tượng Log
