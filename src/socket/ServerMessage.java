package socket;

import models.*;
import java.io.Serializable;
import java.util.*;

public class ServerMessage implements Serializable {

    enum Type {
        EMAIL
    }

    private Type type;
    private List<Email> emails;

    public ServerMessage(Email email) {
        type = Type.EMAIL;
        emails = new ArrayList<>();
        emails.add(email);
    }

    public ServerMessage(List<Email> emails) {
        this.type = Type.EMAIL;
        this.emails = emails;
    }

    public Type getType() {return type;}

    public List<Email> getEmail() {return emails;}


}
