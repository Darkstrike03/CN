import java.io.*;
import java.net.*;
import java.util.Scanner;

public class EchoTCPClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12347;

        try (Socket socket = new Socket(hostname, port)) {
            System.out.println("Connected to Echo TCP Server on port " + port);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            String userInput;
            while (true) {
                System.out.print("Enter message (type 'bye' to exit): ");
                userInput = scanner.nextLine();
                out.println(userInput);
                String response = in.readLine();
                System.out.println("Server: " + response);
                if (userInput.equalsIgnoreCase("bye")) {
                    break;
                }
            }
            scanner.close();

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
}
