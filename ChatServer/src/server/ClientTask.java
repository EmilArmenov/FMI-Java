package server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientTask implements Runnable {

    private Socket client;
    private ChatServer server;
    private String username;
    private BufferedReader input;
    private PrintWriter output;
    private String connectionTimeStamp;

    ClientTask(Socket client, ChatServer server) throws ClientTaskException {
        this.client = client;
        this.server = server;
        username = null;
        connectionTimeStamp = null;
        try {
            this.input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.output = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            throw new ClientTaskException("Error occurred when initializing client", e);
        }
    }

    @Override
    public void run() {
        try {
            login();
            String line;

            while (true) {
                line = input.readLine();
                if (line.equals("disconnect")) {
                    break;
                }
                line += " ";
                String command = line.substring(0, line.indexOf(' '));

                if (command.equals("send")) {
                    String subCommand = line.substring(line.indexOf(' ')).trim();
                    String receiver = subCommand.substring(0, subCommand.indexOf(' '));
                    String msg = subCommand.substring(subCommand.indexOf(' '));
                    server.sendMessageTo(username, receiver, msg);
                    output.print("");
                    output.flush();
                }
                if (command.equals("send-all")) {
                    String msg = line.substring(line.indexOf(' '));
                    server.broadcast(username, msg);
                }
                if (command.equals("list-users")) {
                    server.listUsers(username);
                } 
                  
                output.println("Unknown Command");
                output.flush();    
            }
            output.println("disconnect");
            output.flush();

            server.disconnect(username);
            client.close();

            String disconnectMessage = " has disconnected from the server";
            server.broadcast(username, disconnectMessage);
        } catch (IOException e) {
            System.out.println("Error in ClientTask: " + e.getMessage());
            e.printStackTrace();
        }
    }

    PrintWriter getOutput() {
        return output;
    }

    String getInfo() {
        return String.format("%s, connected at %s", username, connectionTimeStamp);
    }

    private void login() throws IOException {
        String line;

        while ((line = input.readLine()) != null) {
            username = line.trim();
            if (server.addUser(username, this)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm");
                connectionTimeStamp = LocalDateTime.now().format(formatter);
                output.println(username);
                output.flush();
                return;
            }

            output.println("");
            output.flush();
        }
    }
}
