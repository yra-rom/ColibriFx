import client.ClientThread;
import constants.Activity;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainActivity extends Application {
    //496x293 size
    @Override
    public void start(Stage stage) throws Exception{
        startLoginScene(stage);
    }

    private void startLoginScene(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("/layout/login.fxml"));
        Parent root = loader.load();


        stage.setWidth(Activity.WIDTH);
        stage.setHeight(Activity.HEIGHT);
        stage.setTitle(Activity.AppName);
        stage.setResizable(false);

        stage.getIcons().add( new Image( MainActivity.class.getResourceAsStream( "images/MainIcon.png" )));

        Scene scene = new Scene(root, Activity.WIDTH, Activity.HEIGHT);
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
