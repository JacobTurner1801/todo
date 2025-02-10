package com.todo.TodoItem;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TodoItem {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty task = new SimpleStringProperty();
    private BooleanProperty completed = new SimpleBooleanProperty();

    public TodoItem() {}

    public TodoItem(int uid, String desc, boolean c) {
        this.id.set(uid);
        this.task.set(desc);
        this.completed.set(c);
    }

    public int getId() {return this.id.get();}
    public String getTask() {return this.task.get();}
    public StringProperty getTaskProperty() {return this.task;}
    public IntegerProperty getIdProperty() {return this.id;}
    public BooleanProperty getCompletedProperty() {return this.completed;}
    public boolean isCompleted() {return this.completed.get();}
    public void SetTask(String t) {this.task.set(t);}
    public void complete(boolean c) {this.completed.set(c);}

    @Override
    public String toString() {
        return "TodoItem{" +
                "id=" + id.get() +
                ", task='" + task.get() + '\'' +
                ", completed=" + completed.get() +
                '}';
    }
}
