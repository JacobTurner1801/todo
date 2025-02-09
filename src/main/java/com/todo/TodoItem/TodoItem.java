package com.todo.TodoItem;

public class TodoItem {
    private int id;
    private String task;
    private boolean completed;

    public TodoItem(int id, String task, boolean completed) {
        this.id = id;
        this.task = task;
        this.completed = completed;
    }

    public int getId() {return this.id;}
    public String getTask() {return this.task;}
    public boolean isCompleted() {return this.completed;}
    public void SetTask(String t) {this.task = t;}
    public void complete(boolean c) {this.completed = c;}

    @Override
    public String toString() {
        return "TodoItem{" +
                "id=" + id +
                ", task='" + task + '\'' +
                ", completed=" + completed +
                '}';
    }
}
