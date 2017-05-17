package gui.login;

import client.Client;
import client.ClientThread;
import constants.Activity;
import constants.Authentication;
import gui.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController implements Controller {
    private ClientThread thread = ClientThread.getInstance();

    private Tooltip tipEmail;
    private Tooltip tipPass;
    private String tipStyle =
        "   -fx-background-color: rgba(255, 3, 0, 0.5);" +
        "   -fx-text-fill: black;" +
        "   -fx-opacity: 0.5;";

    @FXML private TextField tfEmailLogin;
    @FXML private TextField pfPass;

    @FXML public void initialize() {
        thread.setController(this);
    }

    public void loginAction(ActionEvent actionEvent) throws IOException {
        String email = tfEmailLogin.getText();
        String pass = pfPass.getText();
        Client client = new Client.ClientBuilder().email(email).pass(pass).build();
        String status = thread.authentication(client);

        switch (status) {
            case Authentication.WRONG_EMAIL:{
                tipEmail = new Tooltip();
                tipEmail.setText("Wrong Email");
                tipEmail.setStyle(tipStyle);
                Tooltip.install(tfEmailLogin, tipEmail);
                tfEmailLogin.setStyle("-fx-text-fill: red");
                break;
            }
            case Authentication.WRONG_PASSWORD: {
                tipPass = new Tooltip();
                tipPass.setText("Wrong Password");
                tipPass.setStyle(tipStyle);
                Tooltip.install(pfPass, tipPass);
                pfPass.setStyle("-fx-text-fill: red");
                break;
            }
            case Authentication.FAILED: {

                break;
            }
            case Authentication.SUCCESS:
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/layout/contacts.fxml"));
                
                Parent root = loader.load();
                Scene scene = new Scene(root, Activity.WIDTH, Activity.HEIGHT);
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(scene);
                break;
        }
    }

    public void registrationAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("/layout/registration.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, Activity.WIDTH, Activity.HEIGHT);
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    public void emailTextAction(KeyEvent keyEvent) {
        tfEmailLogin.setStyle("-fx-text-fill: black");
        if(tipEmail != null){
            Tooltip.uninstall(tfEmailLogin, tipEmail);
        }
    }

    public void passTextAction(KeyEvent keyEvent) {
        pfPass.setStyle("-fx-text-fill: black");
        if(tipPass != null){
            Tooltip.uninstall(pfPass, tipPass);
        }
    }
}
