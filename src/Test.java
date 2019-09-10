import controllers.EmailCtrl;
import db.EmailDb;
import models.Email;

import java.util.*;

import java.util.Date;

public class Test {

    public static void main(String[] args) {
        EmailCtrl ctrl = EmailCtrl.getInstance();
        EmailDb db = EmailDb.getInstance();
        db.init();
        String user = "Giancarlo";
        Email test = new Email(0,user, new ArrayList<String>(Arrays.asList("Mario")), "oggetto", "ciao", new Date());
    }

}
