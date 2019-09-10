package models;

import java.io.Serializable;
import java.util.*;

public class Email implements Serializable {

    private int id;
    private String sender;
    private Set<String> receivers;
    private String subject;
    private String body;
    private Date date;

    public Email(int i, String s1, Set<String> r, String s2, String b, Date d) {
        id = i;
        sender = s1;
        receivers = r;
        subject = s2;
        body = b;
        date = d;
    }

    public String toString() {
        String a = "'";
        String s = "','";
        return a + id + s + sender + s + receivers + s + subject + s + body + s + date.toString() + a;
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public Set<String> getReceivers() {
        return receivers;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    public boolean isReceiver(String user) {
        return receivers.contains(user);
    }

}
