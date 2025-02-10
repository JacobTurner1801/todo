package com.todo.fxui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TodoApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
         Label label = new Label("Hello, JavaFX!"); // Create a Label
        StackPane root = new StackPane(); // Use StackPane for simple centering
        root.getChildren().add(label); // Add Label to the layout

        Scene scene = new Scene(root, 1920, 1080);
        primaryStage.setTitle("HelloFX"); // Set window title
        primaryStage.setFullScreen(false);
        primaryStage.setScene(scene); // Set Scene to Stage
        primaryStage.show(); // Display the window
    }

    public static void main(String[] args) {
        // to run javafx: mvn clean install ; mvn javafx:run
        launch(args);
    }
}
