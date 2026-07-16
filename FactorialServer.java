import java.io.*;
import java.net.*;

public class FactorialServer {
    public static void main(String[] args) {
        int port = 12354;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Factorial Server listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new FactorialClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class FactorialClientHandler extends Thread {
    private Socket clientSocket;

    public FactorialClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    private long calculateFactorial(int n) {
        if (n < 0) return -1; // Indicate error for negative input
        if (n == 0 || n == 1) return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client " + clientSocket.getInetAddress() + ": " + inputLine);
                try {
                    int number = Integer.parseInt(inputLine);
                    if (number < 0) {
                        out.println("Error: Please enter a non-negative number.");
                    } else if (number > 20) { // Factorials grow very fast, limit for long type
                        out.println("Error: Number too large for factorial calculation (max 20).");
                    } else {
                        long result = calculateFactorial(number);
                        out.println("Factorial of " + number + " is: " + result);
                    }
                } catch (NumberFormatException e) {
                    out.println("Error: Invalid input. Please enter an integer.");
                }
            }
        } catch (IOException e) {
            System.err.println("I/O error in factorial client handler: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
