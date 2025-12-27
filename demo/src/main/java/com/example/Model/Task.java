package com.example.Model;

import java.util.List;
import com.example.Utils.States;
public class Task{
    private String taskID;
    private List<Coordinate> coordiantes;
    private int result;
    private String status;
    
    public Task(String taskID, List<Coordinate> coordiantes){
        this.taskID = taskID;
        this.coordiantes = coordiantes;
        this.result = result;
        this.status = States.PENDING.name();
    } 
    
    public void assignResult(int result) {
        this.result = result;
        this.status = States.COMPLETED.name();
    }
}


