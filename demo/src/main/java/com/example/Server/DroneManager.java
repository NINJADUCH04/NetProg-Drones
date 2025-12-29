package com.example.Server;

import java.io.IOException;
import java.net.*;
import com.example.Utils.States;
import com.example.Utils.DroneLogger;
import com.example.Model.Coordinate;
import com.example.Model.Task;

public class DroneManager extends Thread {
    private TaskManager taskManager;
    private String droneID;
    private SocketAddress clientAddress;
    private DatagramSocket skt;
    private long lastSeenTime;
    private boolean isRunning = true;

    public DroneManager(String droneID, SocketAddress clientAddress, DatagramSocket skt, TaskManager taskManager) {
        this.droneID = droneID;
        this.clientAddress = clientAddress;
        this.skt = skt;
        this.taskManager = taskManager;
        this.lastSeenTime = System.currentTimeMillis();
    }

    public void addMessageToQueue(String[] messageParts) {
        try {
            this.lastSeenTime = System.currentTimeMillis();
            
            if (messageParts.length < 2) return;

            String commandStr = messageParts[1].trim().toUpperCase();
            States Command;

            try{
                Command = States.valueOf(commandStr);
            }catch(Exception e){
                DroneLogger.logEvent("Invalid command from " + droneID + ": " + commandStr);
                return;
            }

            switch (Command) {
                case REQUEST_TASK:
                    handleTaskRequest();
                    break;

                case SUBMIT_RESULT:
                    if (messageParts.length >= 4) {
                    String taskID = messageParts[2];
                    String result = messageParts[3];
                    taskManager.submitTaskResult(droneID, result);
                    System.out.println("Drone " + droneID + " COMPLETED " + taskID + " with " + result + " survivors.");
                    DroneLogger.logEvent("Drone " + droneID + " COMPLETED " + taskID + " with " + result + " survivors.");
                }
                break;
                
                case HEARTBEAT:
                    break;

                default:
                    System.out.println("Unknown command from " + droneID);
                    DroneLogger.logEvent("Unknown command from " + droneID);
            }
        } catch (Exception e) {
            System.err.println("Error processing message for " + droneID + ": " + e.getMessage());
            DroneLogger.logEvent("Error processing message for " + droneID + ": " + e.getMessage());
        }
    }

    private void handleTaskRequest() {
        Task assigedTask = taskManager.assignNextTask(droneID);
        if (assigedTask != null) {
            try {
                StringBuilder response = new StringBuilder(assigedTask.getTaskID());
                for (Coordinate c : assigedTask.getCoordiantes()) {
                    response.append(";").append(c.getX()).append(",").append(c.getY());
                }

                byte[] buffer = response.toString().getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress);
                skt.send(packet);
                System.out.println("Sent task " + assigedTask.getTaskID() + " with 2 points to " + droneID);
                DroneLogger.logEvent("Sent task " + assigedTask.getTaskID() + " with 2 points to " + droneID);
            } catch (IOException e) {
                System.err.println("Failed to send task to " + droneID);
                DroneLogger.logEvent("Failed to send task to " + droneID);
            }
        }
        else {
            try{
                String noTaskMessage = "NO_MORE_TASKS";
                byte[]  noTaskBuffer = noTaskMessage.getBytes();
                DatagramPacket noTaskPacket = new DatagramPacket(noTaskBuffer, noTaskBuffer.length,clientAddress);
                skt.send(noTaskPacket);
                System.out.println("No more tasks available. Sent termination signal to " + droneID);
                DroneLogger.logEvent("No more tasks available. Sent termination signal to " + droneID);

            }catch(IOException e){
                System.err.println("Failed to send termination signal to " + droneID);
                DroneLogger.logEvent("Failed to send termination signal to " + droneID);
            }
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            if (System.currentTimeMillis() - lastSeenTime > 10000) {
                System.out.println("Drone " + droneID + " timed out (LOST) !");
                DroneLogger.logEvent("Drone " + droneID + " timed out (LOST) !");
                taskManager.releaseTaskIfLost(droneID);
                isRunning = false;
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
    }

}