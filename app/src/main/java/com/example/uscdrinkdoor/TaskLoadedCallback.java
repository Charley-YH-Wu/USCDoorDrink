package com.example.uscdrinkdoor;


public interface TaskLoadedCallback {
    void onTaskDone(Object... values);
    void onSecondTaskDone(Object... values);
}
