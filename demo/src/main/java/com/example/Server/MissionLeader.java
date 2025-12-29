package com.example.Server;

//import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.net.*;
//import java.util.*;
//import com.example.Model.*;
import com.example.Utils.States;

public class MissionLeader extends Thread {

    private DatagramSocket skt;
    private final int BUFFER_SIZE = 1024;

    private ConcurrentHashMap<String, DroneManager> droneThreads = new ConcurrentHashMap<>();
    private TaskManager taskManager;

    public MissionLeader(int port) throws SocketException {
        this.skt = new DatagramSocket(port);
        this.taskManager = new TaskManager();
        System.out.println("Mission Leader initialized on port " + port);
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
                String[] parts = message.split(";");
                if (parts.length < 2)
                    continue;

                String command = parts[1].toUpperCase();
                String droneID = parts[0];

                handleRouting(command, droneID, parts, pktIn);
            }
        } catch (IOException e) {
            System.err.println("Server Socket Error: " + e.getMessage());
        } finally {
            if (skt != null && !skt.isClosed())
                skt.close();
        }
    }

    private void handleRouting(String command, String droneID, String[] parts, DatagramPacket packet) {

        if (command.equals(States.REGISTER.name())) {
            if (!droneThreads.containsKey(droneID)) {

                System.out.println("New Registration: " + droneID);

                DroneManager manager = new DroneManager(droneID, packet.getSocketAddress(), skt, taskManager);
                droneThreads.put(droneID, manager);
                manager.start();
            } // part of error control
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
        } catch (SocketException e) {
            System.err.println("Could not start server: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}