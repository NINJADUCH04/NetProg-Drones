package com.example.Server;

import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.net.*;
import java.util.*;
import com.example.Model.*;

public class MissionLeader extends Thread {

    private DatagramSocket skt;
    private final int BUFFER_SIZE = 1024;

    
    private ConcurrentHashMap<String, DroneManager> droneThreads = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Task> globalTasks = new ConcurrentHashMap<>();


    public MissionLeader(int port) throws SocketException {
        this.skt = new DatagramSocket(port);
        initializeTasks();
        System.out.println("Mission Leader initialized on port " + port);
    }

    private void initializeTasks() {
        
        globalTasks.put("TASK_1", new Task("TASK_1", List.of(new Coordinate(0,0), new Coordinate(3,4))));
        globalTasks.put("TASK_2", new Task("TASK_2", List.of(new Coordinate(3,0), new Coordinate(6,4))));
        globalTasks.put("TASK_3", new Task("TASK_3", List.of(new Coordinate(0,4), new Coordinate(3,8))));
        globalTasks.put("TASK_4", new Task("TASK_4", List.of(new Coordinate(3,4), new Coordinate(6,8))));
        globalTasks.put("TASK_5", new Task("TASK_5", List.of(new Coordinate(1,1), new Coordinate(2,2)))); 
        globalTasks.put("TASK_6", new Task("TASK_6", List.of(new Coordinate(4,5), new Coordinate(5,6))));
        
        System.out.println("Initialized " + globalTasks.size() + " tasks covering the 6x8 grid.");
    }
    @Override
    public void run() {
        System.out.println("Mission Leader Receiver Loop Started...");
        try {
            while (true) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket pktIn = new DatagramPacket(buffer, buffer.length);

                
                skt.receive(pktIn);

                String message = new String(pktIn.getData(), pktIn.getOffset(), pktIn.getLength());
                
                String[] parts = message.split(",");
                if (parts.length < 2) continue;

                String command = parts[1].toUpperCase();
                String droneID = parts[0];

                handleRouting(command, droneID, message, pktIn);
            }
        } 
        catch (IOException e) {
            System.err.println("Server Socket Error: " + e.getMessage());
        } finally {
            if (skt != null && !skt.isClosed()) skt.close();
        }
    }

    private void handleRouting(String command, String droneID, String message, DatagramPacket packet) {
        
        if (command.equals("REGISTER")) {
            if (!droneThreads.containsKey(droneID)) {
                
                System.out.println("New Registration: " + droneID);
                
                DroneManager manager = new DroneManager(droneID, packet.getSocketAddress(), skt, tasksCoordinates);
                droneThreads.put(droneID, manager);
                manager.start();
            }
        } 
        
        DroneManager manager = droneThreads.get(droneID);
        if (manager != null) {
            manager.addMessageToQueue(message);
        } else {
            System.out.println("Unknown drone attempted to communicate: " + droneID);
        }
    }

    public static void main(String[] args) {
        try {
            MissionLeader leader = new MissionLeader(9876);
            leader.start();
        } 
        catch (SocketException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}