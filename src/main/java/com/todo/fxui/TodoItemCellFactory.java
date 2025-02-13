package com.todo.fxui;

import com.todo.TodoItem.TodoItem;
import com.todo.TodoItem.TodoItemDAO;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
/**
 * Creates the ListCells for a todoitem.
 */
public class TodoItemCellFactory extends ListCell<TodoItem> {
    private TodoItemDAO dao;

    private TodoItemService todoItemService;

    private ObservableList<TodoItem> todoItems;

    private Label noItems;

    public TodoItemCellFactory(TodoItemDAO dao, TodoItemService todoItemService, ObservableList<TodoItem> todoItems, Label noItemsLabel) {
        this.dao = dao;
        this.todoItemService = todoItemService;
        this.todoItems = todoItems;
        this.noItems = noItemsLabel;
    }

    @Override
    protected void updateItem(TodoItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            if (!todoItems.isEmpty() && item == null) { // Data is loaded, but item is null (Virtualization)
                // add in a loading thing here maybe
                System.out.println("Loading...");
            } else {
                if (this.getListView() != null) {
                    noItems.setVisible(getListView().getItems().isEmpty());
                }
            }
        } else if (!empty && item != null) {
            CheckBox checkbox = new CheckBox();
            Label taskL = new Label();
            taskL.setPrefWidth(200);
            taskL.setWrapText(false);

            taskL.textProperty().bind(item.getTaskProperty());
            // bind bidirectional should update the database as well
            checkbox.selectedProperty().bindBidirectional(item.getCompletedProperty());
            checkbox.selectedProperty().addListener((obs, old, newVal) -> handleUpdateItem(item, newVal));

            Button removeButton = new Button("remove");
            removeButton.setOnAction(event -> handleRemove(item));
            
            taskL.getStyleClass().removeAll("completed-task");
            // System.out.println(item); // here for debugging
            if (item.isCompleted()) {
                taskL.getStyleClass().add("completed-task");
            }

            HBox cellContent = new HBox(checkbox, taskL, removeButton);
            cellContent.setAlignment(Pos.CENTER);
            cellContent.setSpacing(10);
            cellContent.setPrefWidth(300);
            cellContent.setPadding(new Insets(5));
            HBox.setHgrow(taskL, Priority.ALWAYS);
            HBox.setHgrow(cellContent, Priority.ALWAYS);

            setGraphic(cellContent);
            noItems.setVisible(false);
            setText(null); // reset
        }
    }

    private void handleUpdateItem(TodoItem item, boolean val) {
        item.complete(val);
        Task<Void> updateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                dao.updateItemCompleted(item.getId(), val);
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
        this.todoItemService.restart();
        this.getListView().refresh(); // force a refresh of the list view
    }

    /**
     * removes item from database and updates UI. 
     */
    private void handleRemove(TodoItem item) {
        if (item != null) {
            System.out.println("removing item: " + item.getId() + "\n" + item.getTask());
            Task<Void> removeTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    dao.deleteTodoItem(item.getId());
                    return null;
                }

                @Override
                protected void failed() {
                    Throwable e = getException();
                    System.out.println("Error removing " + item.getTask() + " " + e.getMessage());
                    e.printStackTrace();
                }
            };
            removeTask.setOnSucceeded(e -> {
                Platform.runLater(() -> {
                    todoItems.remove(item);
                    this.getListView().refresh();
                    this.todoItemService.restart();
                });
            });
            new Thread(removeTask).start();
        }
    }
}
