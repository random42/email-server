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
    private boolean connected;


    public SocketListener(Socket socket) {
        setName("Socket");
        this.socket = socket;
        connected = true;
        ctrl = EmailCtrl.getInstance();
        server = EmailServer.getInstance();
    }

    public ObjectInputStream getInput() throws IOException {
        return new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
        ctrl.onSocketConnection();
        while (connected) {
            ClientMessage m = listen();
            if (m == null) continue; // socket closed (unless some Exception has been printed)
            switch(m.getType()) {
                case AUTH: {
                    if (isAuthenticated())
                        ctrl.log(m.getUser() + " tried to authenticate more than once.");
                    else { // auth
                        this.user = m.getUser();
                        server.setUser(user, socket);
                        this.setName("Socket-" + user);
                        ctrl.onAuth(user, m.getLastDate());
                    }
                    break;
                }
                case SEND: {
                    if (!isAuthenticated()) break;
                    Email email = m.getEmail();
                    if (!email.getSender().equals(user)) // sender does not match user
                        ctrl.log(user + " tried to fake an email.");
                    else // manage email
                        ctrl.onSend(email);
                    break;
                }
                case DELETE: {
                    if (!isAuthenticated()) break;
                    ctrl.onDelete(user, m.getEmail());
                    break;
                }
            }
        }
        // socket closed
        if (isAuthenticated()) {
            server.removeUser(user);
            ctrl.onUserDisconnected(user);
        } else {
            ctrl.log("Unauthenticated socket disconnected: " + socket);
        }
    }

    private boolean isAuthenticated() {
        return user != null;
    }


    private ClientMessage listen() {
        try {
            return (ClientMessage)getInput().readObject();
        } catch (IOException e) { // socket closed
            connected = false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
