package com.todo.fxui;

import com.todo.TodoItem.TodoItem;
import com.todo.TodoItem.TodoItemDAO;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class AddItemHandler {
    private TodoItemDAO dao;

    private ObservableList<TodoItem> todoItems;

    private ListView<TodoItem> listView;

    private TextField newItemField;

    public AddItemHandler(TodoItemDAO dao, ObservableList<TodoItem> items, ListView<TodoItem> listView,
            TextField field) {
        this.dao = dao;
        this.todoItems = items;
        this.listView = listView;
        this.newItemField = field;
    }

    public void handleItem() {
        String text = newItemField.getText();
        if (!text.isEmpty()) {
            System.out.println("adding item: " + text);
            Task<TodoItem> addTask = new Task<>() {
                @Override
                protected TodoItem call() throws Exception {
                    return dao.addTodoItem(text); // Add to the database and return the updated item
                }

                @Override
                protected void failed() {
                    System.out.println("Failed to add item into database");
                }
            };

            addTask.setOnSucceeded(e -> {
                TodoItem createdItem = addTask.getValue();
                if (createdItem != null) {
                    Platform.runLater(() -> {
                        todoItems.add(createdItem);
                        listView.refresh();
                        newItemField.clear();
                    });
                }
            });
            new Thread(addTask).start();
        }
    }
}
