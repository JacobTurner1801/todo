package com.todo.fxui;

import java.util.List;

import com.todo.TodoItem.TodoItem;
import com.todo.TodoItem.TodoItemDAO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TodoApplication extends Application {

    private TodoItemDAO dao;

    private Task<List<TodoItem>> fetchTodoDataTask;

    private ListView<TodoItem> todoList;

    private ObservableList<TodoItem> todoItems = FXCollections.observableArrayList(); // can notify of updates to content

    @Override
    public void start(Stage primaryStage) {
        
        dao = new TodoItemDAO("todos.db");

        todoList = new ListView<>();
        
        // dao.addTodoItem("Finish Interface");
        // dao.addTodoItem("Complete Other things");
        todoList.setItems(todoItems);
        // System.out.println(todoItems);
        todoList.setCellFactory(p -> new ListCell<TodoItem>() {
            @Override
            protected void updateItem(TodoItem item, boolean empty) {
                super.updateItem(item, empty);
                // System.out.println("empty: " + empty);
                // System.out.println("item: " + item);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);

                    if (!todoItems.isEmpty() && item == null) { // Data is loaded, but item is null (Virtualization)
                        // TODO: add in a loading thing here
                        System.out.println("Loading...");
                    } else {
                        System.out.println("There may be a problem as both empty is true and item is null");
                        setGraphic(null);
                    }
                } else if (!empty && item != null) {
                    Label taskL = new Label();
                    taskL.setPrefWidth(200);
                    taskL.setWrapText(true);
                    taskL.textProperty().bind(item.getTaskProperty());
                    HBox cellContent = new HBox(taskL);
                    cellContent.setAlignment(Pos.CENTER);
                    cellContent.setSpacing(10);
                    cellContent.setPrefWidth(300);
                    cellContent.setPadding(new Insets(15));
                    HBox.setHgrow(taskL, Priority.ALWAYS);
                    HBox.setHgrow(cellContent, Priority.ALWAYS);

                    // System.out.println("Item Task: " + item.getTask());
                    // System.out.println("Label Text: " + taskL.getText());
                    // System.out.println("Label Width: " + taskL.getWidth());
                    // System.out.println("HBox Width: " + cellContent.getWidth());

                    setGraphic(cellContent);
                    setText(null); // reset
                }
            }
        });

        // fetch items from the database
        fetchTodoDataTask = new Task<>() {
            @Override
            protected List<TodoItem> call() throws Exception {
                try (TodoItemDAO dao = new TodoItemDAO("todos.db")) {
                    List<TodoItem> items = dao.getAllTodoItems();
                    // System.out.println(items.size());
                    return items;
                }
            }
        };
        fetchTodoDataTask.setOnSucceeded(event -> {
            List<TodoItem> tasks = fetchTodoDataTask.getValue();
            if (tasks != null) {
                // Initialise List View UI
                Platform.runLater(() -> {
                    // System.out.println(tasks);
                    todoItems.addAll(tasks);
                    // System.out.println(todoItems.size() + " " + todoItems);
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
        
        // VBox
        VBox vb = new VBox(todoList);
        VBox.setVgrow(todoList, Priority.ALWAYS);
        // StackPane root = new StackPane(vb);
        // root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vb, 1920, 1080);
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
