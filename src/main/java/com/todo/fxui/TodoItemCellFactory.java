package com.todo.fxui;

import com.todo.TodoItem.TodoItem;
import com.todo.TodoItem.TodoItemDAO;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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
                // TODO: add in a loading thing here
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

            setGraphic(cellContent);
            noItems.setVisible(false);
            setText(null); // reset
        }
    }

}
