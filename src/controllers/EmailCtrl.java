package controllers;

import db.EmailDb;
import models.*;
import threads.EmailServer;

import java.util.*;

public class EmailCtrl {

    private static final boolean DEBUG = false;

    private static final int SERVER_PORT = 10001;

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
        server = new EmailServer(SERVER_PORT);
    }

    public void onSend(Email e) {
        db.saveEmail(e);
        Set<String> receivers = server.sendEmail(e);
        log(e.getSender() + " sent an email with ID: " + e.getId() + ", delivered to " + receivers.size() + " online receivers");
        debug();
    }

    public void onAuth(String user, Date last) {
        log(user + " authenticated");
        List<Email> emails;
        if (last == null) // il client non ha mail salvate
            emails = db.getEmails(user);
        else
            emails = db.getUserEmailsAfter(user, last);
        if (!emails.isEmpty())
            server.sendToUser(emails, user);
        debug();
    }

    public void onDelete(String user, Email e) {
        db.deleteEmail(user, e);
        log(user + " deleted an email with ID: " + e.getId());
        debug();
    }

    public void log(Object msg) {
        log.add(msg.toString());
    }

    public Log getLog() {
        return log;
    }

    public void startServer() {
        server.start();
        log("Server started at port: " + SERVER_PORT);
        debug();
    }

    public void stopServer() {
        server.stopServer();
        server = new EmailServer(SERVER_PORT);
        log("Server stopped");
        debug();
    }

    public void clearDb() {
        db.clear();
        log("Db cleared");
        debug();
    }

    public void debug() {
        if (DEBUG) {
            log(server.debug());
            log(db.debug());
        }
    }
}
