package controllers;

import db.EmailDb;
import models.*;
import threads.EmailServer;

import java.util.*;

public class EmailCtrl {

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
        server = EmailServer.getInstance();
        db = EmailDb.getInstance();
        log = new Log();
    }

//    public boolean isUserOnline(String user) {
//        return server.hasUser(user);
//    }

    public void startServer() {
        server.startServer();
    }

    public void onEmail(Email e) {
        db.saveEmail(e);
        log(e.getSender() + " sent an email with ID: " + e.getId());
        server.sendEmail(e);
    }

    public void onAuth(String user, Date last) {
        log(user + "authenticated.");
        List<Email> emails = db.getUserEmailsAfter(user, last);
        server.sendToUser(emails, user);
    }

    public void onUserDisconnected(String user) {
        if (user != null)
            log(user + " closed the connection.");
    }

    public void debugThreads() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        System.out.println(threadSet);
    }

    public void log(String msg) {
        System.out.println(msg);
        log.add(msg);
    }

    public Log getLog() {
        return log;
    }
}
