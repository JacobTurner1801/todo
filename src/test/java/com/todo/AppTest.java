package com.todo;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void createDatabaseAndAddTwoTasks() {
        TodoItemDAO dao = new TodoItemDAO("sample.db");

        dao.addTodoItem("Grocery Shopping");
        dao.addTodoItem("Pay Bills");
        
        List<TodoItem> items = dao.getAllTodoItems();
        for (TodoItem item : items) {
            System.out.println(item);
        }
        dao.close();
        try {
            assertTrue(dao.deleteFile("sample.db"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println(items.size());
        assertTrue(items.size() == 2);
    }

    @Test
    public void testClearDatabase() {
        TodoItemDAO dao = new TodoItemDAO("sample.db");

        dao.addTodoItem("Grocery Shopping");
        dao.addTodoItem("Pay Bills");

        dao.clearAndReCreateTable();

        assertTrue(dao.getAllTodoItems().size() == 0);

        dao.close();
    }
}
