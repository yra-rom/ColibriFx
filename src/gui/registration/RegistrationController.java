package gui.registration;

import client.Client;
import client.ClientThread;
import constants.Activity;
import constants.Registration;
import gui.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;


public class RegistrationController implements Controller {
    private ClientThread thread = ClientThread.getInstance();

    private Tooltip tipEmail;
    private Tooltip tipPass;
    private String tipStyle =
            "   -fx-background-color: rgba(255, 3, 0, 0.5);" +
            "   -fx-text-fill: black;" +
            "   -fx-opacity: 0.5;";

    @FXML private TextField tfEmail;
    @FXML private TextField tfNick;
    @FXML private PasswordField pfPass;
    @FXML private PasswordField pfPassRepeat;

    @FXML public void initialize() {
        thread.setController(this);
    }

    public void registerMeAction(ActionEvent actionEvent) {
        String nick = tfNick.getText();
        String email = tfEmail.getText();
        String pass = pfPass.getText();
        String passRepeat = pfPassRepeat.getText();

        if (!pass.equals(passRepeat)) {
            passNotEqualsTip();
            return;
        }

        Client client = new Client.ClientBuilder().nick(nick).email(email).pass(pass).build();
        String status = thread.registration(client);
        System.out.println("Registration status: " + status);

        switch (status) {
            case Registration.FAILED:
                break;
            case Registration.EMAIL_ISNT_FREE:
                emailNotFreeTip();
                break;
            case Registration.SUCCESS:
                try {
                    openLogin((Stage) ((Node) actionEvent.getSource()).getScene().getWindow());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("Unrecognizable registration status: " + status);
                break;
        }
    }

    public void backToLogin(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        openLogin(stage);
    }

    public void emailTextAction(KeyEvent keyEvent) {
        if (tipEmail != null) {
            Tooltip.uninstall(tfEmail, tipEmail);
        }
        tfEmail.setStyle("-fx-text-fill: black");
    }

    public void passAction(KeyEvent keyEvent) {
        if (tipPass != null) {
            Tooltip.uninstall(pfPass, tipPass);
            Tooltip.uninstall(pfPassRepeat, tipPass);
        }
        pfPass.setStyle("-fx-text-fill: black");
        pfPassRepeat.setStyle("-fx-text-fill: black");
    }

    public void passRepeatAction(KeyEvent keyEvent) {
        if (tipPass != null) {
            Tooltip.uninstall(pfPass, tipPass);
            Tooltip.uninstall(pfPassRepeat, tipPass);
        }
        pfPass.setStyle("-fx-text-fill: black");
        pfPassRepeat.setStyle("-fx-text-fill: black");
    }

    private void openLogin(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/layout/login.fxml"));
        Scene scene = new Scene(root, Activity.WIDTH, Activity.HEIGHT);
        scene.getStylesheets().add("resources/css/login.css");
        stage.setScene(scene);
    }

    private void passNotEqualsTip() {
        pfPass.setStyle("-fx-text-fill: red");
        pfPassRepeat.setStyle("-fx-text-fill: red");
        tipPass = new Tooltip();
        tipPass.setText("Password must be the same");
        tipPass.setStyle(tipStyle);
        Tooltip.install(pfPass, tipPass);
        Tooltip.install(pfPassRepeat, tipPass);

    }

    private void emailNotFreeTip() {
        tfEmail.setStyle("-fx-text-fill: red");
        tipEmail = new Tooltip();
        tipEmail.setText("Email is not free");
        tipEmail.setStyle(tipStyle);
        Tooltip.install(tfEmail, tipEmail);
    }
}