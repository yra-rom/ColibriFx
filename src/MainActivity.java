import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
public class MainActivity extends Application {

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/layout/login.fxml"));
        stage.setTitle("Colibri");
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("resources/images/icon.png")));
        Scene scene = new Scene(root, 300, 475);
        scene.getStylesheets().add(0, "resources/css/login.css");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
