package socket;

import models.*;
import java.io.Serializable;
import java.util.Date;

public class ClientMessage implements Serializable {

    public enum Type {
        EMAIL,
        AUTH
    }

    private Type type;
    private String user;
    private Date lastDate; // date of last email that the client received
    private Email email;

    public Type getType() {return type;}

    public String getUser() {return user;}

    public Email getEmail() {return email;}

    public Date getLastDate() {return lastDate;}

}