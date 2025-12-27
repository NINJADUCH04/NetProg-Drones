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
        if( messageParts[ 1 ].equals(States.REQUEST_TASK)) {
            for(int i = 0; i < globalTasks.length; i++) {
                if(globalTasks[i].getName().equals(messageParts[2]))
            }
        }
        
    }


    public void run() {
        while (true) {    
    }
    }

}
