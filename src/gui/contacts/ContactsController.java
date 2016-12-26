package gui.contacts;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ContactsController {
    //private ClientThread thread = ClientThread.getInstance();
    @FXML
    private TextField tfText;
    private String oldNick;

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

    @FXML
    public void initialize() {
        oldNick = tfText.getText();
    }

    public void onNickClick(){
        tfText.setStyle(textNickEditable);
        tfText.setEditable(true);
    }

    private void sendNewNick(String nick) {
        System.out.println("Sending new nick " + nick);
        //thread.sendNewNick(nick);
    }

    public void onNickKeyPress(KeyEvent keyEvent) {
        if(keyEvent.getEventType().equals(KeyEvent.KEY_PRESSED) && keyEvent.getCode().equals(KeyCode.ENTER)){
            String nick = tfText.getText();
            if(nick.equals("")){
                tfText.setText(oldNick);
                return;
            }
            
            tfText.setStyle(textNickNotEditable);
            tfText.setEditable(false);
            if(!oldNick.equals(nick)){
                sendNewNick(nick);
            }
        }
    }
}