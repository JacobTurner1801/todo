package com.todo.fxui;

import java.util.List;

import com.todo.TodoItem.TodoItem;
import com.todo.TodoItem.TodoItemDAO;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

/**
 * TodoItemService is for loading the correct todo item data 
 * depending on if showComplete is true or false.
 */
public class TodoItemService extends Service<List<TodoItem>> {

    private final TodoItemDAO dao;
    private boolean showComplete = true; // Flag for incomplete items

    public TodoItemService(TodoItemDAO dao) {
        this.dao = dao;
    }

    public void setShowComplete(boolean showComplete) {
        this.showComplete = showComplete;
    }

    @Override 
    public Task<List<TodoItem>> createTask() {
        return new Task<>() {
            @Override
            protected List<TodoItem> call() throws Exception {
                if (showComplete) {
                    return dao.getAllTodoItems();
                } else {
                    return dao.getAllCompletedItems(false);
                }
            }

            @Override
            protected void failed() {
                Throwable e = getException();
                System.err.println("Database operation failed: " + e.getMessage());
                // Handle error on JavaFX thread (e.g., show an alert)
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation failed: " + e.getMessage());
                    alert.showAndWait();
                });
            }
        };
    }
}