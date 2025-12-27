package com.example.Model;
    

public class Drone{
    private String droneID;
    private String state;
    private Task currentTask;
    
    public Drone(String droneID){
        this.droneID = droneID;
        this.state = "ALIVE";
        this.currentTask = null;
    }
    
    public String getDroneID() {
        return droneID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }
}