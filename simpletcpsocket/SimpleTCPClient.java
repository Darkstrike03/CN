import java.io.*;
import java.net.*;

public class SimpleTCPClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(hostname, port)) {
            System.out.println("Connected to server on port " + port);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String messageToSend = "Hello from TCP Client!";
            out.println(messageToSend);
            System.out.println("Sent to server: " + messageToSend);

            String response = in.readLine();
            System.out.println("Received from server: " + response);

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
}
