package threads;

import models.Email;
import socket.ClientMessage;
import controllers.EmailCtrl;

import java.io.*;
import java.net.*;

public class SocketListener extends Thread {

    private EmailCtrl ctrl;
    private EmailServer server;
    private String user;
    private Socket socket;
    private ObjectInputStream in;

    public SocketListener(Socket socket) {
        this.socket = socket;
        ctrl = EmailCtrl.getInstance();
        server = EmailServer.getInstance();
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!socket.isClosed()) {
            ClientMessage m = listen();
            if (m.getType() == ClientMessage.Type.AUTH) { // msg type == AUTH
                if (user != null) // auth already done
                    ctrl.log(m.getUser() + " tried to authenticate again.");
                else { // auth
                    user = m.getUser();
                    server.setUser(user, socket);
                    ctrl.onAuth(user);
                }
            } else { // msg type == EMAIL
                Email email = m.getEmail();
                if (!email.getSender().equals(user)) // sender does not match user
                    ctrl.log(user + " tried to fake an email.");
                else // manage email
                    ctrl.onEmail(email);
            }
        }
        server.removeUser(user);
    }


    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ClientMessage listen() {
        try {
            return (ClientMessage)in.readObject();
        } catch (IOException e) { // socket closed
            if (!socket.isClosed()) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
