package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private ChatClient client;

    ReadThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            validateUsername();
            System.out.print("[" + client.getUserName() + "]: ");
            System.out.flush();

            String response;
            while (true) {
                response = reader.readLine();
                if (response.equals("disconnect")) {
                    break;
                }
                System.out.println(response);
                System.out.print("[" + client.getUserName() + "]: ");
                System.out.flush();
            }
            System.out.println(String.format("=> disconnected from server on localhost:%d", client.getPort()));
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error reading from server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void validateUsername() throws IOException {
        String username;

        System.out.print("Enter a username: ");
        System.out.flush();
        while (true) {
            username = reader.readLine();
            if (!username.equals("")) {
                break;
            }
            System.out.print("Username Taken. Please try another one: ");
            System.out.flush();
        }
        client.setUserName(username);

        System.out.println(String.format("Successfully logged in as User: %s", username));
        System.out.flush();
    }
}
