package com.example.Server;

import java.net.*;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.example.Utils.States;
import com.example.Model.Task;

public class DroneManager extends Thread {
    Task [] globalTasks;
    private String droneID;
    private SocketAddress clientAddress;
    private DatagramSocket skt;
    
    public DroneManager(String droneID, SocketAddress clientAddress, DatagramSocket skt, Task [] globalTasks) {
        this.droneID = droneID;
        this.clientAddress = clientAddress;
        this.skt = skt;
        this.globalTasks = globalTasks;
    }

    public void addMessageToQueue(String [] messageParts) {
        //String Command = messageParts[1];
        States Command = States.valueOf(messageParts[1]);

        switch (Command) {

            case States.REQUEST_TASK:
                for (int i = 0; i < globalTasks.length; i++) {
                    if (globalTasks[i].getStatus().equals(States.PENDING.name())) {
                        globalTasks[i].setStatus(States.IN_PROGRESS.name());
                        globalTasks[i].setAssignedDroneID(droneID);
                        break; 
                    }
                }
                break;
        
            case States.SUBMIT_RESULT:
            for (int i = 0; i < globalTasks.length; i++) {
                if (globalTasks[i].getDroneID().equals(droneID)) {
                    globalTasks[i].setStatus(States.COMPLETED.name());
                    String result = messageParts[3] ;
                    globalTasks[i].setResult(result);                    
                    break; 
                }
            }
                break;
        
            default:
                System.out.println("Unknown command");
        }
        


    public void run() {
        while (true) {    
    }
    }

}
