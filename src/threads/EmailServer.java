package threads;

import models.Email;
import socket.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

public class EmailServer extends Thread {
    private static EmailServer instance;
    private static final int port = 4999;

    private boolean online;
    private Map<String, Socket> users;
    private ServerSocket server;

    public static EmailServer getInstance() {
        if (instance == null)
            instance = new EmailServer();
        return instance;
    }

    private EmailServer() {
        this.setName("Server");
        online = false;
        users = Collections.synchronizedMap(new HashMap<String,Socket>());
    }

    private Socket getSocket(String user) {
        return users.get(user);
    }

    protected void setUser(String user, Socket socket) {
        users.put(user, socket);
    }

    protected void removeUser(String user) {
        users.remove(user);
    }

    public boolean hasUser(String user) {
        return users.containsKey(user);
    }

    public Set<String> getOnlineUsers() {
        return users.keySet();
    }

    public void sendToUser(Email email, String user) {
        ArrayList<Email> e = new ArrayList<>();
        e.add(email);
        sendToUser(e, user);
    }

    public synchronized void sendToUser(List<Email> emails, String user) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(getSocket(user).getOutputStream());
            ServerMessage msg = new ServerMessage(emails);
            out.writeObject(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // returns set of online receivers
    public Set<String> sendEmail(Email e) {
        Set<String> receivers = e.getReceivers();
        Set<String> onlineReceivers = new HashSet<>();
        for (String r : receivers) {
            if (hasUser(r)) {
                sendToUser(e, r);
                onlineReceivers.add(r);
            }
        }
        return onlineReceivers;
    }

    public void run() {
        startServer();
        while (this.isOnline()) {
            try {
                System.out.println("Waiting for connections...");
                Socket s = server.accept();
                System.out.println("Incoming socket: " + s);
                SocketListener l = new SocketListener(s);
                l.start();
            } catch (SocketException e) { // server closed
                if (online) { // expect not to be online
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean isOnline() {
        return online;
    }

    public synchronized void stopServer() {
        try {
            server.close();
            users.clear();
            online = false;
            System.out.println("Server stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void startServer() {
        try {
            server = new ServerSocket(port);
            online = true;
            System.out.println("Server started at port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
