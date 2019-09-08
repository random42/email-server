package threads;

import controllers.EmailCtrl;
import models.Email;
import socket.EmailSocket;

public class EmailListener extends Thread {

    private EmailSocket socket;
    private EmailCtrl ctrl;

    public EmailListener() {
        socket = EmailSocket.getInstance();
        ctrl = EmailCtrl.getInstance();
    }

    @Override
    public void run() {
        while (true) {
            Email e = socket.listen();
            ctrl.onEmailReceived(e);
        }
    }
}
