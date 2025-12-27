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

    private ConcurrentHashMap <Coordinate, Boolean> tasksCoordinates = new ConcurrentHashMap<>();
    
    private ConcurrentHashMap<String, DroneManager> droneThreads = new ConcurrentHashMap<>();

    public MissionLeader(int port) throws SocketException {
        this.skt = new DatagramSocket(port);
        initializeCoordinates();
        System.out.println("Mission Leader initialized on port " + port);
    }

    private void initializeCoordinates() {
        tasksCoordinates.put(new Coordinate(1, 1), false);
        tasksCoordinates.put(new Coordinate(2, 1), false); 
        tasksCoordinates.put(new Coordinate(1, -1), false);
        tasksCoordinates.put(new Coordinate(5, 3), false);
        tasksCoordinates.put(new Coordinate(2, 2), false);
    }

    @Override
    public void run() {
        System.out.println("Mission Leader Receiver Loop Started...");
        try {
            while (!Thread.currentThread().isInterrupted()) {
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

    private void handleRouting(String command, String droneID, String rawMessage, DatagramPacket packet) {
        // 1. If REGISTER, check if we need to spawn a new manager thread
        if (command.equals("REGISTER")) {
            if (!droneThreads.containsKey(droneID)) {
                System.out.println("New Registration: " + droneID);
                
                // Create the manager thread (Pass the task map and drone info)
                DroneManager manager = new DroneManager(droneID, packet.getSocketAddress(), skt, tasksCoordinates);
                droneThreads.put(droneID, manager);
                manager.start();
            }
        } 
        
        DroneManager manager = droneThreads.get(droneID);
        if (manager != null) {
            manager.addMessageToQueue(rawMessage);
        } else {
            System.out.println("Unknown drone attempted to communicate: " + droneID);
        }
    }

    public static void main(String[] args) {
        try {
            MissionLeader leader = new MissionLeader(9876);
            leader.start();
        } catch (SocketException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }
}