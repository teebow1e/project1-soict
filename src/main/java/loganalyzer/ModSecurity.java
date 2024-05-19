package loganalyzer;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class ModSecurity {
    /**
     * Generator for a ModSecurity log object.
     * @param version: version of the ModSecurity that generates the log
     * @param timestamp: in the form "DD/MMM/YYYY:HH:MM:SS.SSSSSS Z", example: "18/Apr/2024:15:57:42.807165 +0700"
     * @param transactionId: transactionId as generated by ModSecurity, example: "ZiDghn8pJCj4T7YiUFJH7AAAAEQ"
     * @param remoteAddress: IP address of remote client
     * @param remotePort: port of remote client
     * @param path: path that the request is sent to and injected data
     * @param attackName: type of attack detected by ModSecurity, example: "REQUEST-930-APPLICATION-ATTACK-LFI"
     * @param attackMsg: attack analysis by ModSecurity, example: "OS File Access Attempt"
     * @param attackData: detailed analysis of the attack, example: "Matched Data: etc/passwd found within ARGS:username: /etc/passwd"
     * @param severity: severity of the request
     */
    public ModSecurity (String version, String timestamp, String transactionId, String remoteAddress, String remotePort,
                        String path, String attackName, String attackMsg, String attackData, String severity) {
        this.version = version;
        this.timestamp = timestamp;
        this.transactionId = transactionId;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.path = path;
        this.attackName = attackName;
        this.attackMsg = attackMsg;
        this.attackData = attackData;
        this.severity = severity;
    }

    public String getVersion() {
        return version;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getRemotePort() {
        return remotePort;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAttackName() {
        return attackName;
    }

    public String getAttackMsg() {
        return attackMsg;
    }

    public String getAttackData() {
        return attackData;
    }

    public String getSeverity() {
        return severity;
    }

    public String getPath() {
        return path;
    }

    @CsvBindByName(column = "version", required = true)
    @CsvBindByPosition(position = 0)
    private final String version;

    @CsvBindByName(column = "timestamp", required = true)
    @CsvBindByPosition(position = 1)
    private final String timestamp;

    @CsvBindByName(column = "transactionId", required = true)
    @CsvBindByPosition(position = 2)
    private final String transactionId;

    @CsvBindByName(column = "remoteAddress", required = true)
    @CsvBindByPosition(position = 3)
    private final String remoteAddress;

    @CsvBindByName(column = "remotePort", required = true)
    @CsvBindByPosition(position = 4)
    private final String remotePort;

    @CsvBindByName(column = "path", required = true)
    @CsvBindByPosition(position = 5)
    private final String path;

    @CsvBindByName(column = "attackName", required = true)
    @CsvBindByPosition(position = 6)
    private final String attackName;

    @CsvBindByName(column = "attackMsg", required = true)
    @CsvBindByPosition(position = 7)
    private final String attackMsg;

    @CsvBindByName(column = "attackData", required = true)
    @CsvBindByPosition(position = 8)
    private final String attackData;

    @CsvBindByName(column = "severity", required = true)
    @CsvBindByPosition(position = 9)
    private final String severity;
}
