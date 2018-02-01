package com.colibri.client.gui.contacts;

import com.colibri.client.ClientThread;
import com.colibri.client.constants.Activity;
import com.colibri.client.gui.Controller;
import com.colibri.client.gui.chat.ChatController;
import com.colibri.common.client.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ContactsController implements Controller {
    private ClientThread thread = ClientThread.getInstance();
    public static HashMap<String, Stage> stages = new HashMap<>();
    public static HashMap<String, ChatController> chats = new HashMap<>();
    public static Stack<Client> stack = new Stack<>();

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

    private String oldNick;
    @FXML
    private TextField tfNick;

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
        Platform.runLater(() -> {
            boolean stageIsOpened = stages.containsKey(client.getEmail());
            if(stageIsOpened){
                stages.get(client.getEmail()).requestFocus();
            }else{
                openChatStage(client);
            }
        });
    }

    public void openChat(String email){
        for (Client client : data){
            if(client.getEmail().equals(email)){
                openChat(client);
                break;
            }
        }
    }

    private void openChatStage(Client client){
        try {
            Stage stage = new Stage();
            stages.put(client.getEmail(), stage);
            stack.push(client);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/layout/chat.fxml"));
            Parent root = loader.load();
            stage.setWidth(Activity.WIDTH);
            stage.setHeight(Activity.HEIGHT);
            stage.setTitle(client.getNick() + " - " + Activity.AppName);
            stage.setResizable(false);
//            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("resources/images/MainIcon.png")));
            Scene scene = new Scene(root, Activity.WIDTH, Activity.HEIGHT);
            stage.setScene(scene);
            stage.setOnCloseRequest(e -> {
                chats.remove(client.getEmail());
                stages.remove(client.getEmail());
                stage.close();
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean makeIncomeNotification(String from){

        final FutureTask query = new FutureTask(new Callable() {
            @Override
            public Object call() throws Exception {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("User " + from + " is trying to get in touch with you.");
                alert.setContentText("Do you want to chat with him/her or receive files ?");

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("resources/images/MainIcon.png")));

                Optional<ButtonType> result = alert.showAndWait();

                return result.get() == ButtonType.OK;
            }
        });
        Platform.runLater(query);

        try {
            return (boolean) query.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

}