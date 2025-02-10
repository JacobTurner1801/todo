package com.todo.fxui;

import java.util.List;

import com.todo.TodoItem.TodoItem;
import com.todo.TodoItem.TodoItemDAO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TodoApplication extends Application {

    private TodoItemDAO dao;

    private Task<List<TodoItem>> fetchTodoDataTask;

    private boolean loadedItems = false;

    @Override
    public void start(Stage primaryStage) {
        
        dao = new TodoItemDAO("todos.db");
        
        // dao.addTodoItem("Finish Interface");
        // dao.addTodoItem("Complete Other things");
        ObservableList<TodoItem> todoItems = FXCollections.observableArrayList(); // can notify of updates to content
        
        // fetch items from the database
        fetchTodoDataTask = new Task<>() {
            @Override
            protected List<TodoItem> call() throws Exception {
                try (TodoItemDAO dao = new TodoItemDAO("todos.db")) {
                    return dao.getAllTodoItems();
                }
            }
        };
        fetchTodoDataTask.setOnSucceeded(event -> {
            List<TodoItem> tasks = fetchTodoDataTask.getValue();
            if (tasks != null) {
                // Initialise List View UI
                Platform.runLater(() -> {
                    todoItems.addAll(tasks);
                    System.out.println();
                    // todoList.setStyle("-fx-background-color: #312e2d; -fx-alignment: center");
                });
            } else {
                System.out.println("tasks are null");
                Alert a = new Alert(AlertType.ERROR);
                a.setContentText("tasks are null");
                a.show();
            }
        });
        
        fetchTodoDataTask.setOnFailed(event -> {
            Throwable e = fetchTodoDataTask.getException();
            System.err.println(e.getMessage() + "\n" + e.getLocalizedMessage());
            e.printStackTrace();
        });
        
        new Thread(fetchTodoDataTask).start();
        
        ListView<TodoItem> todoList = new ListView<TodoItem>(todoItems);
        todoList.setCellFactory(p -> new ListCell<TodoItem>() {
            @Override
            protected void updateItem(TodoItem item, boolean empty) {
                super.updateItem(item, empty);
                if ((item == null || empty == true)) {
                    // System.out.println("here");
                    setText(null);
                    setGraphic(null);
                } else {
                    System.out.println(item.getTask());
                    Label taskL = new Label(item.getTask());
                    taskL.setPrefWidth(Double.MAX_VALUE);
                    taskL.setWrapText(true);
                    HBox cellContent = new HBox(taskL);
                    cellContent.setAlignment(Pos.CENTER);
                    cellContent.setSpacing(10);
                    HBox.setHgrow(taskL, Priority.ALWAYS);
                    setGraphic(cellContent);
                    setText(null);
                }
            }
        });
        
        
        // VBox
        VBox.setVgrow(todoList, Priority.ALWAYS);

        VBox vb = new VBox(todoList);
        StackPane root = new StackPane(vb);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setTitle("Todo");
        primaryStage.setScene(scene); // Set Scene to Stage
        primaryStage.show(); // Display the window
    }

    @Override
    public void stop() {
        if (dao != null) {
            dao.close();
        }
        try {
            super.stop();
        } catch (Exception e) {
            System.err.println("ERROR DURING STOPPING APPLICATION");
            System.err.println(e.getMessage() + "\n" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // to run javafx: mvn clean ; mvn javafx:run
        launch(args);
    }
}
