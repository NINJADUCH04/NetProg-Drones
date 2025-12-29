# Drone Simulation System

## Overview

This is a distributed drone simulation system that coordinates multiple autonomous drones to complete search and rescue tasks across a grid area. The system uses UDP communication for real-time coordination between a central Mission Leader server and multiple drone clients.

## Purpose

The system simulates a disaster response scenario where drones are deployed to search designated areas and report the number of survivors found. It demonstrates:

- Distributed task management
- Fault-tolerant drone coordination
- Real-time heartbeat monitoring
- Automatic task reassignment on drone failure
- Comprehensive event logging

## System Architecture

### Server Side

**MissionLeader** - Central coordination server
- Listens on UDP port 9876
- Manages drone registrations
- Routes messages to appropriate DroneManager threads
- Maintains concurrent drone connections

**DroneManager** - Per-drone thread handler
- Monitors individual drone health via heartbeat timeout (10 seconds)
- Handles task requests and result submissions
- Automatically releases tasks if drone is lost
- One manager thread per registered drone

**TaskManager** - Task coordination
- Manages 6 predefined search tasks covering a 6x8 grid
- Assigns tasks in PENDING → IN_PROGRESS → COMPLETED lifecycle
- Thread-safe task allocation
- Recovers tasks from lost drones back to PENDING state

### Client Side

**Client** - Drone simulation client
- Registers with Mission Leader
- Sends heartbeat every 3 seconds
- Requests tasks, simulates scanning (4-9 seconds), reports results
- Automatically launches new drones every 10 seconds until no tasks remain
- Each drone runs two threads: heartbeat sender and task executor

## Message Protocol

All messages use semicolon-separated format over UDP:

### Client → Server Messages

1. **Registration**
   ```
   DRONE-ID;REGISTER;
   ```

2. **Heartbeat**
   ```
   DRONE-ID;HEARTBEAT;STATE;
   ```

3. **Task Request**
   ```
   DRONE-ID;REQUEST_TASK;
   ```

4. **Submit Result**
   ```
   DRONE-ID;SUBMIT_RESULT;TASK-ID;SURVIVOR_COUNT;
   ```

### Server → Client Messages

1. **Task Assignment**
   ```
   TASK-ID;X1,Y1;X2,Y2
   ```
   Where (X1,Y1) and (X2,Y2) define opposite corners of the search area

2. **No Tasks Available**
   ```
   NO_MORE_TASKS
   ```

## Task Grid

The system divides a 6x8 area into 6 search zones:

```
TASK_4: (0,4)-(2,8)  |  TASK_5: (2,4)-(4,8)  |  TASK_6: (4,4)-(6,8)
------------------------------------------------------------------
TASK_1: (0,0)-(2,4)  |  TASK_2: (2,0)-(4,4)  |  TASK_3: (4,0)-(6,4)
```

Each task requires a drone to scan the rectangular area and report survivor count (0-50, randomly simulated).

## System States

### Drone States
- **ALIVE** - Operational and sending heartbeats
- **FINISHED** - Received NO_MORE_TASKS signal

### Task States (Enum: States)
- **PENDING** - Awaiting assignment
- **IN_PROGRESS** - Assigned to a drone
- **COMPLETED** - Results submitted
- **REGISTER** - Drone registration command
- **HEARTBEAT** - Health check command
- **REQUEST_TASK** - Task request command
- **SUBMIT_RESULT** - Result submission command

## Fault Tolerance

**Drone Timeout Detection**
- DroneManager monitors last heartbeat time
- If no message received for 10+ seconds, drone marked as LOST
- Task automatically released back to PENDING state
- Available for reassignment to another drone

**Automatic Recovery**
- System continues operating despite individual drone failures
- Tasks never lost, only reassigned
- New drones can pick up failed tasks

## Event Logging

   All major events logged to `drone_events.log` with timestamps:

   - Mission Leader initialization and startup
   - Drone registrations
   - Task assignments
   - Task completions with survivor counts
   - Drone timeouts and losses
   - Task recoveries
   - Error conditions

   Log format:
   ```
   yyyy-MM-dd HH:mm:ss - EVENT_MESSAGE
```

## How It Operates

### Startup Sequence

1. **Server Startup**
   - MissionLeader initializes on port 9876
   - TaskManager creates 6 tasks in PENDING state
   - Server begins listening for UDP packets

2. **Client Startup**
   - Client creates drone instance (DRONE-101, DRONE-102, etc.)
   - Sends REGISTER message
   - Starts heartbeat thread (every 3 seconds)
   - Starts task execution thread

### Task Execution Flow

   1. Drone sends REQUEST_TASK
   2. TaskManager assigns first PENDING task
   3. Server sends task coordinates
   4. Drone calculates 4 corners of search area
   5. Drone simulates scanning (4-9 seconds)
   6. Drone generates random survivor count (0-50)
   7. Drone sends SUBMIT_RESULT with count
   8. TaskManager marks task COMPLETED
   9. Drone requests next task (loop continues)

### Termination

   1. When no PENDING tasks remain, server sends NO_MORE_TASKS
   2. Drone receives signal, sets state to FINISHED
   3. Heartbeat thread stops
   4. System sets `availableTasks = false`
   5. No new drones launched
   6. All active drones complete their tasks
   7. System gracefully terminates

## Project Structure

```
   src/
   ├── com/example/
   │   ├── Server/
   │   │   ├── MissionLeader.java    - Main server coordinator
   │   │   ├── DroneManager.java     - Per-drone handler
   │   │   └── TaskManager.java      - Task allocation manager
   │   ├── Client/
   │   │   └── Client.java            - Drone client simulator
   │   ├── Model/
   │   │   ├── Drone.java             - Drone data model
   │   │   ├── Task.java              - Task data model
   │   │   └── Coordinate.java        - 2D coordinate
   │   └── Utils/
   │       ├── States.java            - System state enums
   │       └── DroneLogger.java       - Event logging utility
   ```

## Running the System

### Start Server
```bash
mvn exec:java -Dexec.mainClass="com.example.Server.MissionLeader"
```

### Start Client (in separate terminal)
```bash
mvn exec:java -Dexec.mainClass="com.example.Client.Client"
```

   The client will automatically:
   - Launch DRONE-101 immediately
   - Launch additional drones every 10 seconds
   - Continue until all 6 tasks are completed
   - Terminate when NO_MORE_TASKS received

## Key Features

   **Concurrent Processing** - Multiple drones work simultaneously  
   **Fault Tolerance** - Automatic task recovery on drone failure  
   **Real-time Monitoring** - Heartbeat-based health checking  
   **Thread Safety** - Synchronized task management  
   **Comprehensive Logging** - All events timestamped and recorded  
   **UDP Communication** - Lightweight, connectionless messaging  
   **Automatic Scaling** - Dynamic drone deployment  

## Configuration

Key parameters (modifiable in code):

   - **Server Port**: 9876 (MissionLeader)
   - **Heartbeat Interval**: 3 seconds (Client)
   - **Timeout Threshold**: 10 seconds (DroneManager)
   - **Scan Duration**: 4-9 seconds random (Client)
   - **Survivor Range**: 0-50 random (Client)
   - **New Drone Interval**: 10 seconds (Client.main)
   - **Grid Size**: 6x8 units (TaskManager)
   - **Task Count**: 6 tasks (TaskManager)

## Example Output

**Server Console:**
   ```
   Mission Leader initialized on port 9876
   Mission Leader Receiver Loop Started...
   TaskManager: 6 Tasks initialized with 2 points each.
   New Registration: DRONE-101
   Sent task TASK_1 with 2 points to DRONE-101
   Drone DRONE-101 COMPLETED TASK_1 with 23 survivors.
   TaskManager: TASK_1 is COMPLETED.
```

**Client Console:**
```
   Registration sent for Drone DRONE-101
   Successfully Launched: DRONE-101
   Drone [DRONE-101] Target Area: [0.0,0.0], [2.0,0.0], [0.0,4.0], [2.0,4.0]
   Drone DRONE-101 is scanning... (Estimated time: 6s)
   Drone DRONE-101 COMPLETED scanning and found 23 survivors.
   Drone DRONE-101 sent survivors count (23) for task TASK_1
```
## Message Protocol

   All messages use semicolon-separated format over UDP. Fields are delimited by semicolons (`;`) and coordinates within fields are delimited by commas (`,`).

### Client → Server Messages

   1. **Registration**
   ```
      Format: DRONE-ID;REGISTER;
      Example: DRONE-101;REGISTER;
   ```

   2. **Heartbeat**
   ```
      Format: DRONE-ID;HEARTBEAT;STATE;
      Example: DRONE-101;HEARTBEAT;ALIVE;
   ```

   3. **Task Request**
   ```
      Format: DRONE-ID;REQUEST_TASK;
      Example: DRONE-101;REQUEST_TASK;
   ```

   4. **Submit Result**
   ```
      Format: DRONE-ID;SUBMIT_RESULT;TASK-ID;SURVIVOR_COUNT;
      Example: DRONE-101;SUBMIT_RESULT;TASK_1;23;
      