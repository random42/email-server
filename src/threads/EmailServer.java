package threads;

import controllers.EmailCtrl;
import models.Email;
import socket.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

public class EmailServer extends Thread {

    private EmailCtrl ctrl;
    private int port;
    private boolean online;
    // synchronized map, i metodi che operano sulla mappa non hanno bisogno di essere sincronizzati
    private Map<String, Socket> users;
    private ServerSocket server;

    public EmailServer(int port) {
        this.port = port;
        // thread name
        setName("Server");
        online = false;
        ctrl = EmailCtrl.getInstance();
        users = Collections.synchronizedMap(new HashMap<>());
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

    private boolean hasUser(String user) {
        return users.containsKey(user);
    }

    public Set<String> getOnlineUsers() {
        return users.keySet();
    }

    public void sendToUser(Email email, String user) {
        List<Email> e = new LinkedList<>();
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
                Socket s = server.accept();
                ctrl.log("Incoming socket: " + s);
                SocketListener l = new SocketListener(s, this);
                l.start();
            } catch (SocketException e) { // server closed
                if (isOnline()) { // expect not to be online
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
            for (Socket s : users.values()) {
                s.close();
            }
            users.clear();
            online = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void startServer() {
        try {
            server = new ServerSocket(port);
            online = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String debug() {
        return ("Online users: " + getOnlineUsers());
    }
}
