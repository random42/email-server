import java.io.*;
import java.net.*;
import java.util.*;

public class TestServer {

    public static void main(String[] args) {
        try {
            debug("start");
            ServerSocket server = new ServerSocket(4999);
            debug("server created");
            Socket socket = server.accept();
            debug(socket);
            InputStream is = socket.getInputStream();
            debug("is");
            ObjectInputStream ois = new ObjectInputStream(is);
            debug("ois");
            OutputStream os = socket.getOutputStream();
            debug("os");
            ObjectOutputStream ous = new ObjectOutputStream(os);
            debug("ous");
            String s = (String)ois.readObject();
            debug(s);
            ous.writeObject("albero");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public static void debug(Object... args) {
        for (Object a : args) {
            System.out.print(a);
        }
        System.out.println();
    }
}
