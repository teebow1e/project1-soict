package utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.control.Alert;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Utility {
    private static final Logger logger = Logger.getLogger(Utility.class.getName());
    private Utility() {
        throw new IllegalStateException("Utility class");
    }
    public static String findFirstMatch(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getElementSafely(String[] array, int index) {
        if (index >= 0 && index < array.length) {
            return array[index];
        } else {
            return null;
        }
    }

    public static String extractFileToLocal(String resourceFilePath, String extractedPath) {
        ClassLoader classLoader = Utility.class.getClassLoader();
        String targetPathStr = null;

        try (InputStream inputStream = classLoader.getResourceAsStream(resourceFilePath)) {
            if (inputStream == null) {
                logger.log(Level.SEVERE, "[EXTRACT_LOCAL] Resource file not found: " + resourceFilePath);
                return null;
            }

            File targetDirectory = new File(extractedPath);
            if (!targetDirectory.exists()) {
                if (!targetDirectory.mkdirs()) {
                    logger.log(Level.SEVERE, "[EXTRACT_LOCAL] Failed to create sample directory.");
                    return null;
                }
            }

            Path outputPath = Paths.get(extractedPath, Paths.get(resourceFilePath).getFileName().toString());
            Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            targetPathStr = outputPath.toAbsolutePath().toString();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "[EXTRACT_LOCAL] Error extracting file to local", e);
        }
        return targetPathStr;
    }

    public static void updateConfigValue(String fileName, String key, String newValue) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(fileName);
        try {
            JsonNode rootNode;
            if (file.exists() && file.length() != 0) {
                rootNode = objectMapper.readTree(file);
            } else {
                rootNode = objectMapper.createObjectNode();
            }

            if (rootNode instanceof ObjectNode objectNode) {
                objectNode.put(key, newValue);
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
                logger.log(Level.INFO,"Updated key \"" + key + "\" with value \"" + newValue + "\".");
            } else {
                logger.log(Level.WARNING,"Root node is not an object node.");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING,"An error occurred while updating the configuration file.", e);
        }
    }

    public static void showFirstRunMessage() {
        JPanel panel = new JPanel();
        panel.add(new JLabel(
                "This is the first time you run this app. You have to perform first-run initialization. Click OK to continue."
        ));

        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION
        );

        JDialog dialog = optionPane.createDialog("Notification");

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logger.log(Level.INFO,"[FirstRun] User aborted the first-run initialization.");
                System.exit(1);
            }
        });
        dialog.setVisible(true);
    }


    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
