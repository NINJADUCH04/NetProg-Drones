package com.example.Client;

import com.example.Model.Drone;
import com.example.Model.Task;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import com.example.Utils.States;

public class Client extends Thread {
    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;
    private final int severPort = 9876;
    private Drone droneData;

    public Client(DatagramSocket datagramSocket, InetAddress inetAddress, String droneID) {
        this.datagramSocket = datagramSocket;
        this.inetAddress = inetAddress;
        this.droneData = new Drone(droneID);
    }

    public void Register() {
        try {
            String registerMessage = droneData.getDroneID() + ";REGISTER;";
            byte[] registerBuffer = registerMessage.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(registerBuffer, registerBuffer.length, inetAddress,
                    severPort);
            datagramSocket.send(datagramPacket);
            System.out.println("Registration sent for Drone " + droneData.getDroneID());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (droneData.getState().equals("ALIVE")) {
                String heartbeatMessage = droneData.getDroneID() + ";HEARTBEAT;" + droneData.getState() + ";";
                byte[] hbBuffer = heartbeatMessage.getBytes();

                DatagramPacket hpPacket = new DatagramPacket(hbBuffer, hbBuffer.length, inetAddress, severPort);
                datagramSocket.send(hpPacket);

                Thread.sleep(3000);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleTaskCycle() {
        try {
            String requestMessage = droneData.getDroneID() + ";REQUEST_TASK;";
            byte[] requestBuffer = requestMessage.getBytes();
            DatagramPacket rtPacket = new DatagramPacket(requestBuffer, requestBuffer.length, inetAddress, severPort);
            datagramSocket.send(rtPacket);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            datagramSocket.receive(receivePacket);

            String receivedTask = new String(receivePacket.getData(), 0, receivePacket.getLength());
            String[] parts = receivedTask.split(";");

            String receivedTaskID = parts[0];
            String point1 = parts[1];
            String point2 = parts[2];
            System.out.println("Drone " + droneData.getDroneID() + " working on area from " + point1 + " to " + point2);

            int survivors = new Random().nextInt(51);
            String resultMessage = droneData.getDroneID() + ";" + States.SUBMIT_RESULT.name() + ";" + receivedTaskID + ";"
                    + survivors + ";";

            byte[] sendingTaskResultBuffer = resultMessage.getBytes();
            DatagramPacket completedTaskPacket = new DatagramPacket(sendingTaskResultBuffer,
                    sendingTaskResultBuffer.length, inetAddress, severPort);
            datagramSocket.send(completedTaskPacket);
            System.out.println("Drone " + droneData.getDroneID() + " sent survivors count (" + survivors + ") for task " + receivedTaskID);

        } catch (IOException e) {
            System.out.println("Task Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress inetAddress = InetAddress.getByName("localhost");

        int i = 100;
        while (true) {
            i++;
            String DroneID = "DRONE-" + i;
            Client client = new Client(datagramSocket, inetAddress, DroneID);
            client.Register();
            client.start();

            new Thread(() -> {
                client.handleTaskCycle();
            }).start();
            System.out.println("Successfully Launched: " + DroneID);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
