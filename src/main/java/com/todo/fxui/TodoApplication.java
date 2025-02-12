package com.todo.fxui;

import java.util.List;

import com.todo.TodoItem.TodoItem;
import com.todo.TodoItem.TodoItemDAO;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
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
        
        // filtering the items shown according to the checkbox
        CheckBox showCompleteBox = new CheckBox("Show Complete");
        showCompleteBox.setSelected(true); // default true
        // Initially getting the todoItems
        todoItemService.setShowComplete(showCompleteBox.isSelected());
        todoItemService.restart();

        // when the todoItemService is used
        todoItemService.setOnSucceeded(event -> {
            List<TodoItem> tasks = todoItemService.getValue();
            todoItems.clear();
            todoItems.addAll(tasks);
            todoList.setItems(todoItems);
        });
        
        // when the showComplete checkbox is changed
        showCompleteBox.selectedProperty().addListener((obs, old, newVal) -> {
            todoItemService.setShowComplete(newVal);
            todoItemService.restart();
        });

        // show complete text
        Label showCompleteLabel = new Label("Show Completed Items");
        showCompleteLabel.setPrefWidth(100);
        showCompleteLabel.setWrapText(true);

        // showComplete checkbox layout
        HBox filter = new HBox(showCompleteBox, showCompleteLabel);
        filter.setPadding(new Insets(5));
        HBox.setHgrow(showCompleteLabel, Priority.ALWAYS);

        // No Items Label, and bind the visibility property
        Label noItemsLabel = new Label("No Items Left");
        noItemsLabel.setStyle("-fx-font-style: italic; -fx-text-fill: white;");
        noItemsLabel.managedProperty().bind(noItemsLabel.visibleProperty());

        // create the list view cells
        todoList.setCellFactory(param -> new TodoItemCellFactory(dao, todoItemService, todoItems, noItemsLabel));
        
        // Main UI
        StackPane listContainer = new StackPane(todoList, noItemsLabel);
        VBox mainLayout = new VBox(filter, listContainer);
        VBox.setVgrow(todoList, Priority.ALWAYS);

        // Scene
        Scene scene = new Scene(mainLayout, 1920, 1080);
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
