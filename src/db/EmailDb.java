package db;

import models.Email;

import java.io.*;
import java.util.*;

public class EmailDb {
    private static final String root = "data/";
    private static EmailDb instance;

    public static EmailDb getInstance() {
        if (instance == null)
            instance = new EmailDb();
        return instance;
    }

    private EmailDb() {
    }

    private File createFileIfNotExists(String user) throws IOException {
        File f = new File(root + user);
        if (!f.exists()) {
            f.createNewFile();
        }
        return f;
    }

    private ObjectOutputStream getOutputStream(String user) throws IOException {
        return new ObjectOutputStream(new FileOutputStream(createFileIfNotExists(user)));
    }

    private ObjectInputStream getInputStream(String user) throws IOException {
        return new ObjectInputStream(new FileInputStream(createFileIfNotExists(user)));
    }

    public void init() {
        File dir = new File(root);
        if (!dir.exists())
            dir.mkdir();
        else if (!dir.isDirectory()) {
            dir.delete();
            dir.mkdir();
        }
    }

    public synchronized void writeEmails(String user, List<Email> emails, boolean append) {
        try {
            List<Email> toWrite;
            if (append) {
                toWrite = getEmails(user);
                toWrite.addAll(emails);
            }  else
                toWrite = emails;
            ObjectOutputStream out = getOutputStream(user);
            out.writeObject(toWrite);
            out.flush();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized List<Email> getEmails(String user) {
        List<Email> inbox = new LinkedList<>();
        try {
            ObjectInputStream in = getInputStream(user);
            inbox = (List<Email>)in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            if (!(e instanceof EOFException))
                e.printStackTrace();
        }
        return inbox;
    }

    public void deleteEmail(String user, Email email) {
        List<Email> emails = getEmails(user);
        emails.remove(email);
        writeEmails(user, emails, false);
    }

    public void saveEmail(Email email) {
        List<Email> l = new LinkedList<>();
        l.add(email);
        for (String r : email.getReceivers()) {
            writeEmails(r, l, true);
        }
    }

    public List<Email> getUserEmailsAfter(String user, Date last) {
        List<Email> emails = getEmails(user);
        List<Email> req = new LinkedList<>();
        for (Email e : emails) {
            if (e.getDate().compareTo(last) > 0) {
                req.add(e);
            }
        }
        return req;
    }

    public synchronized String debug() {
        File dir = new File(root);
        List<String> print = new LinkedList<>();
        for (File f : dir.listFiles()) {
            String user = f.getName();
            int size = getEmails(user).size();
            print.add(user + ": " + size);
        }
        return "DB: " + print;
    }

    public void clear() {
        File dir = new File(root);
        for (File f : dir.listFiles()) {
            f.delete();
        }
    }
}
