package controllers;

import db.EmailDb;
import models.*;
import threads.EmailServer;

import java.util.*;

public class EmailCtrl {

    private static final int serverPort = 10001;

    private static EmailCtrl instance;
    private Log log;

    public static EmailCtrl getInstance() {
        if (instance == null) {
            instance = new EmailCtrl();
        }
        return instance;
    }

    private EmailServer server;
    private EmailDb db;

    private EmailCtrl() {
        db = EmailDb.getInstance();
        log = new Log();
    }

    public boolean isServerOnline() {
        return server.isOnline();
    }

    public void init() {
        db.init();
        server = new EmailServer(serverPort);
    }

    public void onSocketConnection() {
        //debugThreads();
    }

    public void onSend(Email e) {
        db.saveEmail(e);
        Set<String> receivers = server.sendEmail(e);
        log(e.getSender() + " sent an email with ID: " + e.getId() + ", delivered to " + receivers.size() + " online receivers");
        db.debugDb();
        server.debug();
    }

    public void onAuth(String user, Date last) {
        log(user + " authenticated");
        List<Email> emails;
        // il client non ha mail salvate
        if (last == null)
            emails = db.getEmails(user);
        else
            emails = db.getUserEmailsAfter(user, last);
        if (!emails.isEmpty())
            server.sendToUser(emails, user);
    }

    public void onDelete(String user, Email e) {
        db.deleteEmail(user, e);
        log(user + " deleted an email with ID: " + e.getId());
        db.debugDb();
    }

    public void debugThreads() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        System.out.println(threadSet);
    }

    public void logOnlineUsers() {
        log(server.getOnlineUsers());
    }

    public void log(Object msg) {
        log.add(msg.toString());
    }

    public Log getLog() {
        return log;
    }

    public void debugUserInbox(String user) {
        List<Email> inbox = db.getEmails(user);
        System.out.println(inbox.size());
    }

    public void startServer() {
        server.start();
        log("Server started at port: " + serverPort);
    }

    public void stopServer() {
        server.stopServer();
        server = new EmailServer(serverPort);
        log("Server stopped");
    }

    public void clearDb() {
        db.clear();
        log("Db cleared");
    }
}
