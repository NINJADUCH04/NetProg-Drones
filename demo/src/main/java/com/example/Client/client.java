
package com.example.Client;

import java.util.List;

class Coordinate{
    private double x;
    private double y;
    
    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }
}

class Task{
    private int taskID;
    private List<Coordinate> coordiantes;
    private String result;
    private boolean completed;
    
    public Task(int taskID, List<Coordinate> coordiantes){
        this.taskID = taskID;
        this.coordiantes = coordiantes;
        this.result = result;
        this.completed = false;
    } 
}

class Drone{
    private int droneID;
    private String state;
    private Task currentTask;
    
    public Drone(int droneID){
        this.droneID = droneID;
        this.state = "ALIVE";
        this.currentTask = null;
    }
    
}

public class client {
    try{
        
    }catch(Exception e){
        System.out.println(e.getMessage());
    }
}
