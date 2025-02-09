package com.todo.TodoItem;

import java.sql.SQLException;

public class TodoItemDAOSQLError {
    public TodoItemDAOSQLError(String message, SQLException se) {
        System.err.println("ERROR: " + message);
        System.err.println(se.getSQLState() + " " + se.getErrorCode());
        System.err.println(se.getMessage());
        se.printStackTrace();
    }
}
