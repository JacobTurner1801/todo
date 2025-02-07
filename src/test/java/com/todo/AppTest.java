package com.todo;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppTest {
    private static TodoItemDAO dao;

    @BeforeClass
    public static void setUp() {
        UUID uid = UUID.randomUUID();
        String path = "sample_" + uid.toString() + ".db";
        dao = new TodoItemDAO(path);
    }

    @AfterClass
    public static void tearDown() {
        try {
            dao.close();
            dao.deleteFile(dao.getDbPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * caution, sometimes this test has weird results,
     * make sure to run independently if there is an error
     */
    @Test
    public void createDatabaseAndAddTwoTasks() {
        dao.addTodoItem("Grocery Shopping");
        dao.addTodoItem("Pay Bills");

        List<TodoItem> items = dao.getAllTodoItems();
        // System.out.println(items.size());
        // for (TodoItem item : items) {
        // System.out.println(item);
        // }
        // System.out.println(items.size());
        assertTrue(items.size() == 2);

    }

    @Test
    public void testClearDatabase() {

        dao.addTodoItem("Grocery Shopping");
        dao.addTodoItem("Pay Bills");

        dao.clearAndReCreateTable();

        assertTrue(dao.getAllTodoItems().size() == 0);

    }

    @Test
    public void testDeletingItem() {

        dao.addTodoItem("Grocery Shopping");
        dao.addTodoItem("Pay Bills");

        dao.deleteTodoItem(1); // autoincrement starts at 1

        assertTrue(dao.getAllTodoItems().size() == 1);
        // NOTE: since we have a static dao thing, I don't think each item 
        // gets deleted when it should because we might have 
        // two tasks running at the same time together
        // so this is my current workaround, because this is ran before testGettingOneItem
        dao.clearAndReCreateTable();
    }

    @Test
    public void testGettingOneItem() {
        dao.addTodoItem("Test Task"); // Use the dao object
        Optional<TodoItem> ti = dao.getTodoItemById(1);
        ti.ifPresent(todoItem -> System.out.println("Found task: " + todoItem));
        assertTrue(ti.get().getTask().equals("Test Task"));
        // fail();      
    }
}
