import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BidirectionalChatServer {
    public static void main(String[] args) {
        int port = 12346;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            String clientMessage, serverMessage;

            while (true) {
                // Read from client
                if (in.ready()) {
                    clientMessage = in.readLine();
                    if (clientMessage == null || clientMessage.equalsIgnoreCase("bye")) {
                        System.out.println("Client disconnected.");
                        break;
                    }
                    System.out.println("Client: " + clientMessage);
                }

                // Write to client
                System.out.print("Server: ");
                serverMessage = scanner.nextLine();
                out.println(serverMessage);
                if (serverMessage.equalsIgnoreCase("bye")) {
                    break;
                }
            }

            clientSocket.close();
            scanner.close();

        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
