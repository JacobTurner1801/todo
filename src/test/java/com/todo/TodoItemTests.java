package com.todo;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.todo.TodoItem.TodoItem;
import com.todo.TodoItem.TodoItemDAO;

// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TodoItemTests {
    private static TodoItemDAO dao;

    @BeforeClass
    public static void setUp() {
        String path = "sample_.db";
        dao = new TodoItemDAO(path);
    }

    @Before
    public void initialise() {
        dao.clearAndReCreateTable();
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
    }

    @Test
    public void testGettingOneItem() {
        dao.addTodoItem("Test Task");
        Optional<TodoItem> ti = dao.getTodoItemById(1);
        assertTrue(ti.get().getTask().equals("Test Task"));
    }

    @Test
    public void testGettingAllCompleted() {
        dao.addTodoItem("Test Completed");
        dao.addTodoItem("Not completed task");
        // update the database
        dao.updateItemCompleted(1, true);
        // get all completed
        List<TodoItem> items = dao.getAllCompletedItems(true);
        assertTrue(items.size() == 1);
    }

    @Test
    public void testGettingAllNotCompleted() {
        dao.addTodoItem("Test Completed");
        dao.addTodoItem("Not completed task");
        dao.addTodoItem("Another not completed task");

        dao.updateItemCompleted(1, true);
        
        List<TodoItem> items = dao.getAllCompletedItems(false);
        assertTrue(items.size() == 2);
    }
}
