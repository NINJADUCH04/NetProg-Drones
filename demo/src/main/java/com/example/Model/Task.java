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
    private String taskID;
    private List<Coordinate> coordiantes;
    private int result;
    private String status;
    
    public Task(String taskID, List<Coordinate> coordiantes){
        this.taskID = taskID;
        this.coordiantes = coordiantes;
        this.result = result;
        this.status = "PENDING";
    } 
}


