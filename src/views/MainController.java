package views;

import controllers.EmailCtrl;
import java.util.*;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import models.Log;

public class MainController implements Observer {

    EmailCtrl ctrl;

    @FXML
    private ToggleButton button;
    @FXML
    private TextArea logs;

    @FXML
    public void initialize() {
        ctrl = EmailCtrl.getInstance();
        Log l = ctrl.getLog();
        logs.setText(String.join("\n",l.getAll()));
        l.addObserver(this);
        logs.setEditable(false);
    }

    @FXML
    private void toggleServer() {
        if (ctrl.isServerOnline()) {
            ctrl.stopServer();
            button.setText("Start");
        }
        else {
            ctrl.startServer();
            button.setText("Stop");
        }
    }

    @FXML
    private void clearDb() {
        ctrl.clearDb();
    }

    @Override
    public void update(Observable o, Object arg) {
        Log l = (Log)o;
        String pre = "\n";
        if (logs.getText().length() == 0)
            pre = "";
        logs.appendText(pre + l.getLast());
        logs.setScrollLeft(0);
    }
}
