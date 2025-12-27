package com.example.Model;
    

public class Drone{
    private int droneID;
    private String state;
    private Task currentTask;
    
    public Drone(int droneID){
        this.droneID = droneID;
        this.state = "ALIVE";
        this.currentTask = null;
    }  
}