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
            if (m == null) continue; // socket closed (unless some Exception has been printed)
            else if (m.getType() == ClientMessage.Type.AUTH) { // msg type == AUTH
                if (user != null) // auth already done
                    ctrl.log(m.getUser() + " tried to authenticate more than once.");
                else { // auth
                    user = m.getUser();
                    if (server.hasUser(user)) { // user has already a connected client opened that will be substituted by the new one
                        ctrl.log(user + " authenticated on a new client.");
                    }
                    server.setUser(user, socket);
                    ctrl.onAuth(user, m.getLastDate());
                }
            } else { // msg type == EMAIL
                Email email = m.getEmail();
                if (!email.getSender().equals(user)) // sender does not match user
                    ctrl.log(user + " tried to fake an email.");
                else // manage email
                    ctrl.onEmail(email);
            }
        }
        // socket closed
        server.removeUser(user);
        ctrl.onUserDisconnected(user);
    }


    private ClientMessage listen() {
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
