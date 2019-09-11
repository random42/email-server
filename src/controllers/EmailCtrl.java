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

    public void init() {
        db.init();
    }

    public void onSocketConnection() {
        //debugThreads();
    }

    public void onSend(Email e) {
        db.saveEmail(e);
        log(e.getSender() + " sent an email with ID: " + e.getId());
        server.sendEmail(e);
    }

    public void onAuth(String user, Date last) {
        log(user + " authenticated");
        List<Email> emails;
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
    }

    public void onUserDisconnected(String user) {
        log(user + " disconnected");
    }

    public void debugThreads() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        System.out.println(threadSet);
    }

    public void logOnlineUsers() {
        log(server.getOnlineUsers());
    }

    public void log(Object msg) {
        System.out.println(msg);
        log.add(msg.toString());
    }

    public Log getLog() {
        return log;
    }

    public void debugUserInbox(String user) {
        List<Email> inbox = db.getEmails(user);
        System.out.println(inbox);
    }

    public void startServer() {
        server.start();
    }
}
