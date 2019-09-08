package socket;

import models.Email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EmailSocket {
    private static final String host = "localhost";
    private static final int port = 4999;
    private static EmailSocket instance;

    public static EmailSocket getInstance() {
        if (instance == null)
            instance = new EmailSocket();
        return instance;
    }

    public static void init() {

    }


    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private EmailSocket() { }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean send(Email email) {
        if (!isConnected())
            throw new Error("Cannot send email on disconnected socket.");
        try {
            out.writeObject(email);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Email listen() {
        try {
            return (Email)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
