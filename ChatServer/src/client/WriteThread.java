package client;

import java.io.*;
import java.net.Socket;

public class WriteThread extends Thread {

    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String text;

        try {
            do {
                text = console.readLine();
                writer.println(text);
            } while (!text.equals("disconnect"));

            socket.close();
        } catch (IOException ex) {
            System.out.println("Error writing to server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
