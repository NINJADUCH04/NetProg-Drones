package com.example.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DroneLogger {
    private static final String LOG_FILE = "drone_events.log";
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void logEvent(String event) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
    
            String timestamp = LocalDateTime.now().format(formatter);
            pw.println(timestamp + " - " + event);
            
        } catch (IOException e) {
            System.err.println("Failed to log event: " + e.getMessage());
        }
    }
}