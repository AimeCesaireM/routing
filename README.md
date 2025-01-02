# Netowork simulator project

## Overview

This project simulates a simple network of hosts, where data is transmitted from one host to another using various networking layers: Physical, Data Link, and Network. The simulation allows for the construction of the network, routing of data, and validation of the transmission. The three main Java files:

1. `RandomNetworkLayer.java` - Implements a network layer that performs routing using random link selection.
2. `Simulator.java` - The main entry point of the simulation that coordinates the setup and transmission of data between hosts.
3. `Host.java` (implicitly mentioned) - Represents a network host, with layers for data link and network communication.

### Prerequisites

- Java 11 or higher.
- Command-line access to run the simulation.

### How the Simulation Works

1. **Host Setup:**
   The simulation constructs a network of hosts based on a `links.txt` file, where each line defines a connection between two hosts with a specified weight. The `Simulator.java` file reads this configuration and sets up the necessary network layers for each host.

2. **Network Layers:**
   The network layers are designed to simulate data transmission using different methods:
   - **Physical Layer:** Simulates the medium of communication.
   - **Data Link Layer:** Manages the connection between hosts, including error handling and packet framing.
   - **Network Layer:** Decides how data is routed between hosts. The `RandomNetworkLayer.java` file implements a random routing mechanism.

3. **Data Transmission:**
   The `Simulator.java` file reads a file containing the data to be transmitted, sets up the network, and then sends the data from a source host to a destination host. The simulation verifies whether the received data matches the sent data.

4. **Simulation Execution:**
   - Hosts are started in separate threads.
   - The source host transmits the data to the destination host.
   - The receiver waits for the data, then validates if the transmitted data matches the received data.

---

## Files and Their Functions

### 1. `RandomNetworkLayer.java` and `FloodingNetworkLayer.java`
These classes implement the network layer for routing packets through a random link or a flooding selection processes respectively. They handles the creation and extraction of packets, as well as routing the packets. The main functions of this file are:
- **createPacket():** Builds a packet to be sent, including header and data.
- **route():** Selects a random data link layer to send the packet or route the packet to all connected data link layers.
- **extractPacket():** Extracts a complete packet from a buffer.
- **processPacket():** Processes the received packet and either delivers it to the client or routes it further.

### 2. `Simulator.java`
The `Simulator.java` file is the entry point of the simulation. It reads command-line arguments, constructs the network, transmits data, and verifies successful delivery. The main functions are:
- **main():** The entry point of the simulation, handling command-line arguments, setting up the network, and transmitting data.
- **construct():** Reads a `links.txt` file and creates the hosts and network layers.
- **readFile():** Reads the contents of a file into a byte array.
- **simulate():** Coordinates the simulation by transmitting data from the sender to the receiver and verifying the transmission.

### 3. `Host.java`
- **Attach Network Layers:** Hosts are connected to other hosts via the data link layer.
- **Send and Receive Data:** Handles the sending and receiving of data, working with the data link and network layers.

---

## Command-Line Arguments

To run the simulation, use the following command:

```
java Simulator <medium type> <data link layer type> <network layer type> <links.txt file> <source host> <destination host> <transmission data file>
```

### Parameters:
1. **`<medium type>`** - The type of communication medium (e.g., `low` (for a low noise medium), `perfect` (for a perfect medium)).
2. **`<data link layer type>`** - The data link layer type (e.g., `Dumb`, `PAR` (DataLink layer with Parity Check), `CRC` (DataLink layer with CRC check)).
3. **`<network layer type>`** - The network layer type (e.g., `Random`, `Flood`).
4. **`<links.txt file>`** - The path to the `links.txt` file, which defines the connections between hosts in the network.
5. **`<source host>`** - The hostname of the source host that will transmit the data.
6. **`<destination host>`** - The hostname of the destination host that will receive the data.
7. **`<transmission data file>`** - The path to the file containing the data to be transmitted (e.g: `\kjv.txt`).

### Example Command:

```
java Simulator Wired Ethernet Random links.txt Host1 Host2 data.txt
```

---

## How to Run the Simulation

1. **Prepare the `links.txt` file:**
   This file should define the network topology, with each line containing two hostnames and a weight representing the connection between them. Example format:
   ```
   Host1 Host2 10
   Host2 Host3 5
   ```

2. **Prepare the transmission data file:**
   This file contains the data you want to send between the hosts. Ensure the file exists and is readable.

3. **Run the Simulation:**
   Execute the command with the appropriate arguments, and the simulation will begin. The simulation will run and print whether the data was successfully transmitted and received.

---

## Debugging and Logging

The `debug` flag in the `Simulator.java` file controls the printing of debugging information. Set it to `true` to enable detailed logs during the simulation, which can be helpful for troubleshooting.

```java
public static final boolean debug = true;
```

---

## License

This project is licensed under the MIT License.
