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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TodoApplication extends Application {

    private TodoItemDAO dao;

    private TodoItemService todoItemService;

    private ListView<TodoItem> todoList;

    private ObservableList<TodoItem> todoItems = FXCollections.observableArrayList(); // can notify of updates to content

    @Override
    public void start(Stage primaryStage) {
        
        dao = new TodoItemDAO("todos.db");

        todoItemService = new TodoItemService(dao);

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
                    }
                } else if (!empty && item != null) {
                    CheckBox checkbox = new CheckBox();
                    Label taskL = new Label();
                    taskL.setPrefWidth(200);
                    taskL.setWrapText(false);

                    taskL.textProperty().bind(item.getTaskProperty());
                    // bind bidirectional should update the database as well
                    checkbox.selectedProperty().bindBidirectional(item.getCompletedProperty());
                    
                    taskL.getStyleClass().removeAll("completed-task");
                    System.out.println(item.isCompleted());
                    System.out.println(dao.getTodoItemById(item.getId()));
                    if (item.isCompleted()) {
                        taskL.getStyleClass().add("completed-task");
                    }

                    checkbox.selectedProperty().addListener((obs, old, newVal) -> {
                        item.complete(newVal);
                        Task<Void> updateTask = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                dao.updateItemCompleted(item.getId(), newVal);
                                return null;
                            }

                            @Override
                            protected void failed() {
                                Throwable e = getException();
                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            }
                        }; 
                        new Thread(updateTask).start();
                        todoItemService.restart();
                        this.getListView().refresh(); // force a refresh of the list view
                    });
                    
                    HBox cellContent = new HBox(checkbox, taskL);
                    cellContent.setAlignment(Pos.CENTER);
                    cellContent.setSpacing(10);
                    cellContent.setPrefWidth(300);
                    cellContent.setPadding(new Insets(5));
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

        // filtering the items shown according to the checkbox
        CheckBox showCompleteBox = new CheckBox("Show Complete");
        showCompleteBox.setSelected(true);
        todoItemService.setShowComplete(showCompleteBox.isSelected());
        todoItemService.restart();
        
        todoItemService.setOnSucceeded(event -> {
            List<TodoItem> tasks = todoItemService.getValue();
            todoItems.clear();
            todoItems.addAll(tasks);
            todoList.setItems(todoItems);
        });
        
        showCompleteBox.selectedProperty().addListener((obs, old, newVal) -> {
            todoItemService.setShowComplete(newVal);
            todoItemService.restart();
        });

        Label showCompleteLabel = new Label("Show Completed Items");
        showCompleteLabel.setPrefWidth(100);
        showCompleteLabel.setWrapText(true);

        // FILTER UI
        HBox filter = new HBox(showCompleteBox, showCompleteLabel);
        filter.setPadding(new Insets(5));
        HBox.setHgrow(showCompleteLabel, Priority.ALWAYS);
        // Main UI
        VBox root = new VBox(filter, todoList);
        VBox.setVgrow(todoList, Priority.ALWAYS);
        // Scene
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
