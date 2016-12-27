package gui.contacts;

import client.ClientThread;
import gui.Controller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ContactsController implements Controller {
    private String textNickNotEditable = "-fx-font-size: 14px;\n" +
            "    -fx-font-style: normal;\n" +
            "    -fx-display-caret: true;\n" +
            "    -fx-border-width: 0;\n" +
            "    -fx-alignment: center;\n" +
            "    -fx-background-color: inherit;\n" +
            "    -fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);";

    private String textNickEditable = "-fx-font-size: 14px;\n" +
            "    -fx-font-style: normal;\n" +
            "    -fx-display-caret: true;\n" +
            "    -fx-border-width: 2;\n" +
            "    -fx-alignment: center;\n" +
            "    -fx-border-color: #a6adae;\n" +
            "    -fx-background-color: #ecf4ff;\n" +
            "    -fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);";

    private ClientThread thread = ClientThread.getInstance();

    @FXML
    private TextField tfNick;

    private String oldNick;

    @FXML
    private ListView<String> listView;
    private ObservableList<String> data;

    @FXML
    public void initialize() {
        thread.setController(this);
        oldNick = tfNick.getText();
        //tfNick.setText(ClientThread.getInstance().getClient().getNick());
        tfNick.setText("SomeNick");
        tfNick.setStyle(textNickNotEditable);
        deselectTf();

        data = FXCollections.observableArrayList();
        listView.setItems(data);
        addListListeners();

        thread.askForFriends();
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
        //thread.sendNewNick(nick);
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

    private void openChat(String s) {
        System.out.println("Opening chat " + s);
    }

    private static short count = 0;

    public void onButtonAction(ActionEvent actionEvent) {
        data.add("New item " + count++);
    }
}