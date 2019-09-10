import controllers.EmailCtrl;
import db.EmailDb;
import models.Email;

import java.util.*;

import java.util.Date;

public class Test {

    public static void init() {
        EmailDb.init();
    }

    public static void main(String[] args) {
        init();
        EmailCtrl ctrl = EmailCtrl.getInstance();
        String user = "Giancarlo";
        Email test = new Email(0,user, new ArrayList(Arrays.asList("Mario")), "oggetto", "ciao", new Date());
        ctrl.send(test);
    }

}
