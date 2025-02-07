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

public class TodoItemDAO {
    private Connection connection; // db connect

    public TodoItemDAO(String dbPath) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTableIfNeeded();
        } catch (SQLException se) {
            System.err.println("ERROR: CREATING CONNECTION");
            System.err.println(se.getSQLState() + " " + se.getErrorCode());
            System.err.println(se.getMessage());
            se.printStackTrace();
        }
    }

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
            System.err.println("ERROR: CREATING TABLE");
            System.err.println(se.getSQLState() + " " + se.getErrorCode());
            System.err.println(se.getMessage());
            se.printStackTrace();
        }
    }

    public void addTodoItem(String task) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO todos (task, completed) VALUES (?, ?)");
            ps.setString(1, task);
            ps.setInt(2, 0);
            ps.executeUpdate();
        } catch (SQLException se) {
            System.err.println("ERROR: ADDING TODO ITEM");
            System.err.println(se.getSQLState() + " " + se.getErrorCode());
            System.err.println(se.getMessage());
            se.printStackTrace();
        }
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
            System.err.println("ERROR: GETTING ALL TODO ITEMS");
            System.err.println(se.getSQLState() + " " + se.getErrorCode());
            System.err.println(se.getMessage());
            se.printStackTrace();
        }
        return todoItems;
    }

    public void clearAndReCreateTable() {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE todos");
            createTableIfNeeded();
        } catch (SQLException se) {
            System.err.println("ERROR: CLEARING TABLE");
            System.err.println(se.getSQLState() + " " + se.getErrorCode());
            System.err.println(se.getMessage());
            se.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException se) {
            System.err.println("ERROR: CLOSING CONNECTION!!");
            System.err.println(se.getSQLState() + " " + se.getErrorCode());
            System.err.println(se.getMessage());
            se.printStackTrace();
        }
    }

}
