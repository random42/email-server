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

    public boolean isUserOnline(String user) {
        return server.hasUser(user);
    }

    public void send(Email e) {
        //server.send(e);
        db.saveEmail(e);
    }

    public synchronized void onEmail(Email e) {
        db.saveEmail(e);
        log(e.getSender() + " sent an email.");
        server.sendEmail(e);
    }

    public synchronized void onAuth(String user, Date last) {
        List<Email> emails = db.getUserEmailsAfter(user, last);
        server.sendToUser(emails, user);
    }

    public void debugThreads() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        System.out.println(threadSet);
    }

    public void log(String msg) {
        System.out.println(msg);
        log.add(msg);
    }

    public List<String> getLogs() {
        return log.get();
    }
}
