package gui.contacts;

import client.Client;
import client.ClientThread;
import constants.Activity;
import gui.Controller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ContactsController implements Controller {
    private String textNickNotEditable =
            "    -fx-font-size: 14px;\n" +
            "    -fx-font-style: normal;\n" +
            "    -fx-display-caret: true;\n" +
            "    -fx-border-width: 0;\n" +
            "    -fx-alignment: center;\n" +
            "    -fx-background-color: inherit;\n" +
            "    -fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);";

    private String textNickEditable =
            "    -fx-font-size: 14px;\n" +
            "    -fx-font-style: normal;\n" +
            "    -fx-display-caret: true;\n" +
            "    -fx-border-width: 0.7;\n" +
            "    -fx-alignment: center;\n" +
            "    -fx-border-color: #a6adae;\n" +
            "    -fx-background-color: #ecf4ff;\n" +
            "    -fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);" +
            "    -fx-border-insets: 0.5;" +
            "   -fx-border-radius: 20 20 20 20;\n" +
            "   -fx-background-radius: 20 20 20 20;";

    private ClientThread thread = ClientThread.getInstance();

    private HashMap<String, Stage> stages = new HashMap<>();

    @FXML private TextField tfNick;

    private String oldNick;

    @FXML private ListView<Client> listView;
    private ObservableList<Client> data;


    @FXML public void initialize() {
        thread.setController(this);
        String nick = ClientThread.getInstance().getClient().getNick();
        tfNick.setText(nick);
        oldNick = nick;
        tfNick.setStyle(textNickNotEditable);
        deselectTf();

        data = FXCollections.observableArrayList();
        listView.setItems(data);
        addListListeners();

        thread.askForFriends();
    }

    public void addFriends(ArrayList<Client> clients){
        Platform.runLater(() ->{
            data.clear();
            data.setAll(clients);
            listView.setItems(data);
        });
    }

    private void deselectTf() {
        tfNick.requestFocus();
        tfNick.focusedProperty().addListener((ov, t, t1) -> Platform.runLater(() -> {
            if (tfNick.isFocused() && !tfNick.getText().isEmpty()) {
                tfNick.deselect();
            }
        }));
    }

    private void addListListeners() {
        listView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                openChat(listView.getSelectionModel().getSelectedItem());
            }
        });

        listView.setOnKeyPressed(key -> {
            if (key.getCode().equals(KeyCode.ENTER)) {
                openChat(listView.getSelectionModel().getSelectedItem());
            }
        });
    }

    public void onNickClick(){
        tfNick.setStyle(textNickEditable);
        tfNick.setEditable(true);
    }

    private void sendNewNick(String nick) {
        System.out.println("Sending new nick " + nick);
        thread.sendNewNick(nick);
    }

    public void onNickKeyPress(KeyEvent keyEvent) {
        if(keyEvent.getEventType().equals(KeyEvent.KEY_PRESSED) && keyEvent.getCode().equals(KeyCode.ENTER)){
            String nick = tfNick.getText();
            tfNick.setStyle(textNickNotEditable);
            tfNick.setEditable(false);
            if(nick.equals("")){
                tfNick.setText(oldNick);
                return;
            }

            if(!oldNick.equals(nick)){
                sendNewNick(nick);
            }
        }
    }

    private void openChat(Client client) {
        if(stages.containsKey(client.getEmail())){
            Stage stage = stages.get(client.getEmail());
            stage.requestFocus();
            return;
        }

        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/layout/chat.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setWidth(Activity.WIDTH);
        stage.setHeight(Activity.HEIGHT);
        stage.setTitle(client.getNick() + " - Colibri");
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("resources/images/icon.png")));
        Scene scene = new Scene(root, Activity.WIDTH, Activity.HEIGHT);
        scene.getStylesheets().add(0, "resources/css/chat.css");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            //Stop Messenger thread???
            stage.close();
        });
        stage.show();
        stages.put(client.getEmail(), stage);
    }
}