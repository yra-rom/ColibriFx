package login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LoginAct extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("login/login.fxml"));
        stage.setTitle("Colibri");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("login/icon.png")));
        Scene scene = new Scene(root, 300, 475);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
