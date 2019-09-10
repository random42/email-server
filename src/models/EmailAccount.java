package models;

import java.util.*;

public class EmailAccount {
    String name;
    List<Email> inbox;

    public EmailAccount(String name) {
        this.name = name;
        inbox = new ArrayList<>();
    }

    public EmailAccount(String name, List<Email> inbox) {
        this.name = name;
        this.inbox = inbox;
    }

    public void addEmail(Email email) {
        inbox.add(email);
    }

    public void removeEmail(Email email) {
        inbox.remove(email);
    }

}
