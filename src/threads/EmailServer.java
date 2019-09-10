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
        online = false;
        users = Collections.synchronizedMap(new HashMap<String,Socket>());
    }

    private synchronized Socket getSocket(String user) {
        return users.get(user);
    }

    protected synchronized void setUser(String user, Socket socket) {
        users.put(user, socket);
    }

    protected synchronized void removeUser(String user) {
        users.remove(user);
    }

    public synchronized boolean hasUser(String user) {
        return users.containsKey(user);
    }

    public void sendToUser(Email email, String user) {
        ArrayList<Email> e = new ArrayList<>();
        e.add(email);
        sendToUser(e, user);
    }

    public void sendToUser(List<Email> emails, String user) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(getSocket(user).getOutputStream());
            ServerMessage msg = new ServerMessage(emails);
            out.writeObject(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // returns list of online receivers
    public List<String> sendEmail(Email e) {
        List<String> receivers = e.getReceivers();
        List<String> onlineReceivers = new ArrayList<>();
        for (String r : receivers) {
            if (hasUser(r)) {
                sendToUser(e, r);
                onlineReceivers.add(r);
            }
        }
        return onlineReceivers;
    }

    public void run() {
        while (true) {
            while (!online) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Socket s = server.accept();
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
            notifyAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void startServer() {
        try {
            server = new ServerSocket(port);
            online = true;
            notifyAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
