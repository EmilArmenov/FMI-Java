package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private int port;
    private ServerSocket serverSocket;
    private Map<String, ClientTask> users;

    ChatServer(int port) {
        this.port = port;
        users = new ConcurrentHashMap<>();
    }

    private void listen() {
        while (true) {
            Socket clientConnection = null;
            try {
                clientConnection = serverSocket.accept();
                ClientTask client = new ClientTask(clientConnection, this);
                new Thread(client).start();
            } catch (IOException | ClientTaskException e) {
                System.out.println(clientConnection.getInetAddress() + " tried to connect but error occurred");
                e.printStackTrace();
            }
        }
    }

    void start() {
        try {
            serverSocket = new ServerSocket(port);
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean addUser(String username, ClientTask client) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, client);

        return true;
    }

    void listUsers(String toUser) {
        int connectedUsersCounter = 1;
        PrintWriter receiverOutput = users.get(toUser).getOutput();
        for (Map.Entry<String, ClientTask> entry : users.entrySet()) {
            if (!entry.getKey().equals(toUser)) {
                synchronized (receiverOutput) {
                    receiverOutput.println(entry.getValue().getInfo());
                    receiverOutput.flush();
                    connectedUsersCounter++;
                }
            }
        }

        if (connectedUsersCounter == 1) {
            receiverOutput.println("Nobody is online");
            receiverOutput.flush();
        }
    }

    void sendMessageTo(String fromUser, String toUser, String message) {
        ClientTask client = users.get(toUser);
        if (client == null) {
            ClientTask sender = users.get(fromUser);
            PrintWriter senderOutput = sender.getOutput();
            synchronized (senderOutput) {
                senderOutput.printf("User with name: %s does not exist", toUser);
                senderOutput.flush();
            }
        } else {
            PrintWriter output = client.getOutput();
            synchronized (output) {
                output.printf("[%s]: %s\n", fromUser, message);
                output.flush();
            }
        }
    }

    void broadcast(String fromUser, String message) {
        for (String receiver : users.keySet()) {
            if (!fromUser.equals(receiver)) {
                sendMessageTo(fromUser, receiver, message);
            }
        }
    }

    void disconnect(String username) {
        users.remove(username);
    }

    public static void main(String[] args) {
        final int port = 8080;
        ChatServer server = new ChatServer(port);
        server.start();
    }
}
