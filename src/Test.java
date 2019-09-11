import controllers.EmailCtrl;
import db.EmailDb;
import models.Email;

import java.util.*;

import java.util.Date;

public class Test {

    public static void main(String[] args) {
        Thread.currentThread().setName("Main");
        EmailCtrl ctrl = EmailCtrl.getInstance();
        ctrl.init();
        ctrl.startServer();
    }

}
