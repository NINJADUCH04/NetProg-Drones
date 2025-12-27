package com.example.Model;

import java.util.List;

class Coordinate{
    private double x;
    private double y;
    
    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }
}

public class Task{
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


