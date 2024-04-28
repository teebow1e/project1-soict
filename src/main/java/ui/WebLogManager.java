package ui;

import controller.*;
import dataanalyzer.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

public class WebLogManager extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        start(primaryStage, 1);
    }

    public void start(Stage primaryStage, int mode) throws Exception {
        primaryStage.setResizable(false);
        FXMLLoader loader = null;
        String fxmlFile = switch (mode) {
            case 1 -> "/fxml/main/Dashboard.fxml";
            case 2 -> "/fxml/main/Viewlog.fxml";
            case 3 -> "/fxml/main/Feedback.fxml";
            case 4 -> "/fxml/main/Export.fxml";
            case 5 -> "/fxml/main/Managelog.fxml";
            default -> throw new IllegalArgumentException("Invalid mode specified: " + mode);
        };

        // System.out.println("FXML File Path: " + getClass().getResource(fxmlFile));
        loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/WebLog.css").toExternalForm());

        primaryStage.setTitle("Web Log Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();

        // root.setOnMouseClicked(event -> scene.getRoot().requestFocus());

        Controller controller = loader.getController();
        controller.init(primaryStage);

        if (mode == 1) {
            PieChartVisualizer.CreatePieChart(root);
        }
        else if (mode == 2) {
            TableVisualizer.ShowLogTable(root);
            scene.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());
        }
        else if(mode == 5) {
            TableVisualizer.ManageLogTable(root);
            scene.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
