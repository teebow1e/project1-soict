package dataanalyzer;

import controller.TableCheckBox;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static csvgenerator.CSVReader.read;

public class TableVisualizer {
    private static Timer timer = new Timer();

    public static void LogTable(TableView<String[]> tableView, String columnStyle) throws IOException {
        List<String> data = read("logs/parsed/log.csv");
        // Clear existing table content
        tableView.getItems().clear();
        tableView.getColumns().clear();

        String[] headers = data.get(0).split(",");
        for (int i = 0; i < headers.length; i++) {
            final int columnIndex = i;
            TableColumn<String[], String> column = new TableColumn<>(headers[i]);
            column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue()[columnIndex]));
            column.setResizable(true); // Ensure the column can resize
            column.getStyleClass().add(columnStyle + (i + 1));
            tableView.getColumns().add(column);
        }

        ObservableList<String[]> rows = FXCollections.observableArrayList();
        for (int i = 1; i < data.size(); i++) {
            String[] rowData = data.get(i).split(",");
            rows.add(rowData);
        }

        tableView.setItems(rows);
        tableView.setEditable(true);

        tableView.setRowFactory(tv -> {
            TableRow<String[]> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    String[] rowData = row.getItem();
                    showRowContent(rowData);
                    System.out.println("Double clicked row: " + Arrays.toString(rowData));
                }
            });
            return row;
        });

        // Set column resize policy to ensure all columns cover the full width of the table
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public static void ShowLogTable(Parent root) throws IOException {
        VBox mainVBox = (VBox) root.lookup("#mainVBox");
        TableView<String[]> tableView = (TableView<String[]>) mainVBox.lookup("#Table");
        LogTable(tableView, "access-log-table-column-");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTableView(tableView);
            }
        }, 0, 10000); // Update every 10 seconds

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            timer.cancel();
        });
    }

    public static void ManageLogTable(Parent root) throws IOException {
        VBox mainVBox = (VBox) root.lookup("#mainVBox");
        TableView<String[]> tableView = (TableView<String[]>) mainVBox.lookup("#Table");
        LogTable(tableView, "manage-access-log-table-column-");

        // Add a checkbox column
        TableColumn<String[], Boolean> checkboxColumn = new TableColumn<>("Checkbox");
        checkboxColumn.setCellValueFactory(param -> {
            String[] rowData = param.getValue();
            TableCheckBox checkBox = new TableCheckBox(rowData); // Create TableCheckBox instance for each row
            BooleanProperty selectedProperty = checkBox.selectedProperty();
            selectedProperty.addListener((observable, oldValue, newValue) -> {
                System.out.println(String.join(",", rowData)); // Print the row data when checkbox is toggled
            });
            return selectedProperty;
        });
        checkboxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkboxColumn));
        checkboxColumn.getStyleClass().add("manage-access-log-table-column-" + 9);
        checkboxColumn.setEditable(true); // Make the checkbox column editable
        checkboxColumn.setResizable(true);
        tableView.getColumns().add(checkboxColumn);

        // Set column resize policy to ensure all columns cover the full width of the table
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            timer.cancel();
        });
    }

    private static void updateTableView(TableView<String[]> tableView) {
        Platform.runLater(() -> {
            try {
                LogTable(tableView, "access-log-table-column-");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void showRowContent(String[] rowData) {
        // Create a VBox to hold the content
        VBox contentBox = new VBox();
        contentBox.setSpacing(5);

        // Iterate through the rowData and create Text nodes for each element
        for (String rowdata : rowData) {
            Text text = new Text(rowdata);
            contentBox.getChildren().add(text);
        }

        // Create a dialog to display the content
        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(contentBox);
        dialog.setTitle("Row Details");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}
