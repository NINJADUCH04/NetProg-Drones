package com.example.Model;

import java.util.List;
import com.example.Utils.States;

public class Task {
    private String taskID;
    private List<Coordinate> coordiantes;
    private int result;
    private String status;
    private String assignedDroneID;

    public Task(String taskID, List<Coordinate> coordiantes) {
        this.taskID = taskID;
        this.coordiantes = coordiantes;
        this.status = States.PENDING.name();
        this.result = 0;
        this.assignedDroneID = "NONE";
    }

    public String getTaskID() {
        return taskID;
    }

    public List<Coordinate> getCoordiantes() {
        return coordiantes;
    }

    public int getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    public String getAssignedDroneID() {
        return assignedDroneID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedDroneID(String assignedDroneID) {
        this.assignedDroneID = assignedDroneID;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void assignResult(int result) {
        this.result = result;
        this.status = States.COMPLETED.name();
    }
}