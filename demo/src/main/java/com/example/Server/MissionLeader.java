package com.example.Server;

import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.net.*;
import java.util.*;
import com.example.Model.*;
import com.example.Utils.States;
public class MissionLeader extends Thread {

    private DatagramSocket skt;
    private final int BUFFER_SIZE = 1024;

    
    private ConcurrentHashMap<String, DroneManager> droneThreads = new ConcurrentHashMap<>();

    private Task[] globalTasks;


    public MissionLeader(int port) throws SocketException {
        this.skt = new DatagramSocket(port);
        initializeTasks();
        System.out.println("Mission Leader initialized on port " + port);
    }

    private void initializeTasks() {
        
        globalTasks = new Task[6];
        globalTasks[0] = new Task("TASK_1", List.of(new Coordinate(0,0), new Coordinate(3,4)));
        globalTasks[1] = new Task("TASK_2", List.of(new Coordinate(3,0), new Coordinate(6,4)));
        globalTasks[2] = new Task("TASK_3", List.of(new Coordinate(0,4), new Coordinate(3,8)));
        globalTasks[3] = new Task("TASK_4", List.of(new Coordinate(3,4), new Coordinate(6,8)));
        globalTasks[4] = new Task("TASK_5", List.of(new Coordinate(1,1), new Coordinate(2,2))); 
        globalTasks[5] = new Task("TASK_6", List.of(new Coordinate(4,5), new Coordinate(5,6)));
        
        System.out.println("Initialized " + globalTasks.length + " tasks covering the 6x8 grid.");
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

                handleRouting(command, droneID, parts, pktIn);
            }
        } 
        catch (IOException e) {
            System.err.println("Server Socket Error: " + e.getMessage());
        } finally {
            if (skt != null && !skt.isClosed()) skt.close();
        }
    }

    private void handleRouting(String command, String droneID, String [] parts, DatagramPacket packet) {
        
        if (command.equals(States.REGISTER.name())) {
            if (!droneThreads.containsKey(droneID)) {
                
                System.out.println("New Registration: " + droneID);
                
                DroneManager manager = new DroneManager(droneID, packet.getSocketAddress(), skt, globalTasks);
                droneThreads.put(droneID, manager);
                manager.start();
            }//part of error control
        } 
        
        DroneManager manager = droneThreads.get(droneID);
        if (manager != null) {
            manager.addMessageToQueue(parts);
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