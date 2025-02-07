package com.todo;

import java.sql.Statement;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TodoItemDAO {
    private Connection connection; // db connect
    private String dbPath;

    public TodoItemDAO(String dbPath) {
        this.dbPath = dbPath;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTableIfNeeded();
        } catch (SQLException se) {
            new TodoItemDAOSQLError("CREATING CONNECTION", se);
        }
    }

    public String getDbPath() {return this.dbPath;}

    
    public boolean deleteFile(String databaseName) throws Exception {
        File db = new File(databaseName);
        boolean del = db.delete();
        if (!del) {
            throw new Exception("Could not delete file: " + databaseName);
        }
        return true;
    }
    
    private void createTableIfNeeded() throws SQLException {
        // completed is 0 if false, 1 if true
        try {
            Statement st = connection.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS todos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," + 
            "task TEXT NOT NULL," +
            "completed INTEGER NOT NULL DEFAULT 0)"
            );
        } catch (SQLException se) {
            new TodoItemDAOSQLError("CREATING TABLE", se);
        }
    }

    public void deleteTodoItem(int id) {
        try (Statement st = connection.createStatement()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM todos WHERE id = (?)");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException se) {
            new TodoItemDAOSQLError("DELETING TASK", se);
        }
    }
    
    public void addTodoItem(String task) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO todos (task) VALUES (?)");
            ps.setString(1, task);
            ps.executeUpdate();
        } catch (SQLException se) {
            new TodoItemDAOSQLError("ADDING ITEM", se);
        }
    }

    public Optional<TodoItem> getTodoItemById(int id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id, task, completed FROM todos WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("here");
                    int itemId = resultSet.getInt("id");
                    String task = resultSet.getString("task");
                    boolean completed = resultSet.getInt("completed") == 1;
                    return Optional.of(new TodoItem(itemId, task, completed));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException se) {
            new TodoItemDAOSQLError("GETTING ITEM", se);
        }
        return Optional.empty();
    }

    public List<TodoItem> getAllTodoItems() {
        List<TodoItem> todoItems = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, task, completed FROM todos")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String task = resultSet.getString("task");
                boolean completed = resultSet.getInt("completed") == 1;
                todoItems.add(new TodoItem(id, task, completed));
            }
        } catch (SQLException se) {
            new TodoItemDAOSQLError("GETTING ALL TODO ITEMS", se);
        }
        return todoItems;
    }

    public void clearAndReCreateTable() {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE todos");
            createTableIfNeeded();
        } catch (SQLException se) {
            new TodoItemDAOSQLError("CLEARING TABLE", se);
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException se) {
            new TodoItemDAOSQLError("CLOSING CONNECTION!!!!", se);
        }
    }

}
