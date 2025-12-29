package com.example.Model;
    
public class Drone{
    private String droneID;
    private String state;
    private Task currentTask;
    private long lastSeenTime;
    
    public Drone(String droneID){
        this.droneID = droneID;
        this.state = "ALIVE";
        this.currentTask = null;
        this.lastSeenTime = System.currentTimeMillis();
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

    public long getLastSeenTime() { 
        return lastSeenTime;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

    public void setLastSeenTime(long lastSeenTime) {
        this.lastSeenTime = lastSeenTime; 
    }
}