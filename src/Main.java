import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controllers.EmailCtrl;
import models.Email;

public class Main extends Application {

    EmailCtrl ctrl;

    public void start(Stage primaryStage) throws Exception {
        ctrl = EmailCtrl.getInstance();
        ctrl.init();
        Parent root = FXMLLoader.load(getClass().getResource("views/main.fxml"));
        primaryStage.setTitle("Email server");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
