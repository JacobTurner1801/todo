package com.todo.fxui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.todo.TodoItem.TodoItem;
import com.todo.TodoItem.TodoItemDAO;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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

    private TextField addItemTextField;

    private Button addItemButton;

    @Override
    public void start(Stage primaryStage) {

        File db_path = new File("C:\\todo");
        try {
            Files.createDirectories(Path.of(db_path.toURI()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        dao = new TodoItemDAO("C:\\todo\\todos.db");

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

        // Add item
        addItemTextField = new TextField();
        addItemButton = new Button("Add Item");
        HBox addBox = new HBox(addItemTextField, addItemButton);
        addBox.setSpacing(10);
        addBox.setAlignment(Pos.CENTER_LEFT);
        addBox.setPadding(new Insets(10));

        AddItemHandler addItemHandler = new AddItemHandler(dao, todoItems, todoList,addItemTextField);

        addItemButton.setOnAction(event -> addItemHandler.handleItem());
        
        // top settings bar and add item layout
        HBox topSettings = new HBox(showCompleteBox, showCompleteLabel, addBox);
        topSettings.setPadding(new Insets(5));
        HBox.setHgrow(showCompleteLabel, Priority.ALWAYS);

        // No Items Label, and bind the visibility property
        Label noItemsLabel = new Label("No Items Left");
        noItemsLabel.setStyle("-fx-font-style: italic; -fx-text-fill: white;");
        noItemsLabel.managedProperty().bind(noItemsLabel.visibleProperty());

        // create the list view cells
        todoList.setCellFactory(param -> new TodoItemCellFactory(dao, todoItemService, todoItems, noItemsLabel));
        
        // Main UI
        StackPane listContainer = new StackPane(todoList, noItemsLabel);
        VBox mainLayout = new VBox(topSettings, listContainer);
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
        // create exe: jpackage --name "todo" --input "target" --main-jar "todo-1.0-SNAPSHOT.jar" --main-class "com.todo.fxui.TodoApplication" --dest "dist" --type "exe" 
        // run from todo/todo.
        launch(args);
    }
}
