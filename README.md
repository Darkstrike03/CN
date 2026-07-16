# How to Run Networking Programs on Windows and Ubuntu

This guide provides instructions for compiling and running the C and Java networking programs.

## 1. Prerequisites

### Ubuntu (Linux)
- GCC Compiler: `sudo apt update && sudo apt install build-essential`
- Java Development Kit (JDK): `sudo apt install default-jdk`

### Windows
- C Compiler: Install MinGW-w64 or use Visual Studio (MSVC).
- Java Development Kit (JDK): Download and install from Oracle or use OpenJDK. Ensure `javac` and `java` are in your PATH.

---

## 2. Running C Programs

### Ubuntu (Terminal)
To compile and run any C program (e.g., `stop_and_wait.c`):
1. Compile: `gcc stop_and_wait.c -o stop_and_wait`
2. Run: `./stop_and_wait`

### Windows (Command Prompt / PowerShell)
If using MinGW:
1. Compile: `gcc stop_and_wait.c -o stop_and_wait.exe`
2. Run: `stop_and_wait.exe`

---

## 3. Running Java Programs

For socket programs, you must run the **Server** first in one terminal window, and then the **Client** in another.

### Ubuntu (Terminal)
1. Compile: `javac SimpleTCPServer.java SimpleTCPClient.java`
2. Run Server: `java SimpleTCPServer`
3. Run Client (in a new terminal): `java SimpleTCPClient`

### Windows (Command Prompt / PowerShell)
1. Compile: `javac SimpleTCPServer.java SimpleTCPClient.java`
2. Run Server: `java SimpleTCPServer`
3. Run Client (in a new terminal): `java SimpleTCPClient`

---

## 4. Program Specifics

### Bidirectional Chat Programs
- Both TCP and UDP versions require two terminal windows.
- Type your message and press Enter. Type 'bye' to exit the chat.

### Echo Servers
- The server will stay running and echo back any message the client sends.
- Multiple clients can connect to the Multithreaded servers simultaneously.

### Factorial Server
- Send an integer (e.g., `5`) from the client.
- The server will return the factorial (e.g., `120`).
- Limit: Numbers up to 20 (due to `long` type limits).

---

## Troubleshooting
- **Port Conflicts**: If you get a "Port already in use" error, change the port number in both the Server and Client source files and recompile.
- **Firewall**: On Windows, you might see a firewall prompt when running servers. Allow access for the program to communicate over the network.
- **Localhost**: All clients are configured to connect to `localhost`. If running on different machines, replace `localhost` with the server's IP address.
