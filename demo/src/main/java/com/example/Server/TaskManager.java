package com.example.Server;

import com.example.Model.Task;
import com.example.Model.Coordinate;
import com.example.Utils.States;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> globalTasks;

    public TaskManager() {
        this.globalTasks = new ArrayList<>();
        initializeTasks();
    }

    private void initializeTasks() {
        globalTasks.add(new Task("TASK_1", List.of(new Coordinate(0, 0), new Coordinate(2, 4))));
        globalTasks.add(new Task("TASK_2", List.of(new Coordinate(2, 0), new Coordinate(4, 4))));
        globalTasks.add(new Task("TASK_3", List.of(new Coordinate(4, 0), new Coordinate(6, 4))));
        globalTasks.add(new Task("TASK_4", List.of(new Coordinate(0, 4), new Coordinate(2, 8))));
        globalTasks.add(new Task("TASK_5", List.of(new Coordinate(2, 4), new Coordinate(4, 8))));
        globalTasks.add(new Task("TASK_6", List.of(new Coordinate(4, 4), new Coordinate(6, 8))));

        System.out.println("TaskManager: 6 Tasks initialized with 2 points each.");
    }

    public synchronized Task assignNextTask(String droneID) {
        for (Task task : globalTasks) {
            if (task.getStatus().equals(States.PENDING.name())) {
                task.setStatus(States.IN_PROGRESS.name());
                task.setAssignedDroneID(droneID);
                return task;
            }
        }
        return null;
    }

    public synchronized void submitTaskResult(String droneID, String result) {
        for (Task task : globalTasks) {
            if (droneID.equals(task.getAssignedDroneID()) && 
            task.getStatus().equals(States.IN_PROGRESS.name())) {
            
            task.assignResult(result);
            task.setStatus(States.COMPLETED.name());
            System.out.println("TaskManager: " + task.getTaskID() + " is COMPLETED.");
            break;
        }
        }
    }

    public synchronized void releaseTaskIfLost(String droneID) {
        for (Task task : globalTasks) {
            if (droneID.equals(task.getAssignedDroneID()) && 
            !task.getStatus().equals(States.COMPLETED.name())) {
            
            task.setStatus(States.PENDING.name()); 
            task.setAssignedDroneID("NONE");
            System.out.println("TaskManager: " + task.getTaskID() + " is RECOVERED and PENDING.");
            break; 
        }
        }
    }
}
