package com.example;

import java.io.IOException;
import java.net.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

// Extending Thread allows you to use super("name") and the run() method
public class server {

    private DatagramSocket skt;
    private int size;
    // Changed key to Integer, Integer to match your .put() calls
    private HashMap<SimpleEntry<Integer, Integer>, Boolean> tasksCoordinates = new HashMap<>();

    // Constructor
    public server(int port, int size) throws SocketException {
        super("Receiver-Thread");
        this.skt = new DatagramSocket(port);
        this.size = size;
        initializeCoordinates();
    }
    
    // Logic must be inside a method
    private void initializeCoordinates() {
        tasksCoordinates.put(new SimpleEntry<>(1, 1), false);
        tasksCoordinates.put(new SimpleEntry<>(2, 1), true);
        tasksCoordinates.put(new SimpleEntry<>(1, -1), false);
        tasksCoordinates.put(new SimpleEntry<>(5, 3), true);
        tasksCoordinates.put(new SimpleEntry<>(2, 2), false);
    }

    @Override
    public void run() {
        System.out.println("Server started on port: " + skt.getLocalPort());
        try {
            while (true) {
                byte[] buffer = new byte[size];
                DatagramPacket pktIn = new DatagramPacket(buffer, buffer.length);

                // This blocks until a packet arrives
                skt.receive(pktIn);

                String line = new String(pktIn.getData(), pktIn.getOffset(), pktIn.getLength());
                System.out.println("Received: " + line);
                
                // Optional: yield to other threads
                Thread.yield();
            }
        } catch (IOException e) {
            System.out.println("Error in receiverThread: " + e.getMessage());
        } finally {
            if (skt != null && !skt.isClosed()) {
                skt.close();
            }
        }
    }

    public static void main(String[] args) {
        try {
            server serverThread = new server(9876, 1024);
            serverThread.start(); // Start the thread
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}