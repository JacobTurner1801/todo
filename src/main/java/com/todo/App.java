package com.todo;

import java.util.List;

public class App {
    public static void main( String[] args ) {
        // To run:
        // mvn clean install ; mvn exec:java
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("OUTPUT FOR CODE");
        System.out.println(" ");
        System.out.println(" ");
        
        TodoItemDAO dao = new TodoItemDAO();
        dao.addTodoItem("Grocery Shopping");
        dao.addTodoItem("Pay Bills");
        
        List<TodoItem> items = dao.getAllTodoItems();
        for (TodoItem item : items) {
            System.out.println(item);
        }        
        dao.close();
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");
    }
}
