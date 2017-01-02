import client.ClientThread;
import constants.Activity;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainActivity extends Application {
    //293 496
    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/layout/login.fxml"));
//        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/layout/chat.fxml"));
        stage.setWidth(Activity.WIDTH);
        stage.setHeight(Activity.HEIGHT);
        stage.setTitle("Colibri");
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("resources/images/MainIcon.png")));
        Scene scene = new Scene(root, Activity.WIDTH, Activity.HEIGHT);
        scene.getStylesheets().add(0, "resources/css/login.css");
//        scene.getStylesheets().add(0, "resources/css/chat.css");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            ClientThread.getInstance().close();

            stage.close();
            System.exit(0);
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
