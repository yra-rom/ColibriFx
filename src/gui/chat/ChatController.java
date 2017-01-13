package gui.chat;

import client.Client;
import client.ClientThread;
import gui.Controller;
import gui.contacts.ContactsController;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import lib.Message;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatController implements Controller {
    private ClientThread thread = ClientThread.getInstance();

    @FXML private TextArea taMessage;

    @FXML private Label lNick;

    @FXML private ImageView ivCall;
    @FXML private ImageView ivVideo;
    @FXML private ImageView ivFile;
    @FXML private ImageView ivSend;

    @FXML private ListView<Message> lvChats;
    private ObservableList<Message> data;

    private Client friend;

    @FXML public void initialize() {
        initImageListeners();

        if(! ContactsController.stack.isEmpty()){
            friend = ContactsController.stack.pop();
            lNick.setText(friend.getNick());
            registerChat(friend.getEmail());
        }

        data = FXCollections.observableArrayList();
        lvChats.setItems(data);
        // thread.setController(this);
        initTA();
    }

    private void initImageListeners() {
        ImageView ivs[] = {ivCall, ivVideo, ivFile, ivSend};

        for(ImageView iv : ivs) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0.0);

            iv.setEffect(colorAdjust);
            iv.setOnMouseEntered(e -> {

                Timeline fadeInTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0),
                                new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(0.1), new KeyValue(colorAdjust.brightnessProperty(), -0.35, Interpolator.LINEAR)
                        ));
                fadeInTimeline.setCycleCount(1);
                fadeInTimeline.setAutoReverse(false);
                fadeInTimeline.play();

            });

            iv.setOnMouseExited(e -> {

                Timeline fadeOutTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0),
                                new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(0.7), new KeyValue(colorAdjust.brightnessProperty(), 0, Interpolator.LINEAR)
                        ));
                fadeOutTimeline.setCycleCount(1);
                fadeOutTimeline.setAutoReverse(false);
                fadeOutTimeline.play();

            });
        }
    }

    private void initTA() {
        taMessage.setWrapText(true);
    }

    public void receiveMessage(Message message){
        Platform.runLater(() -> {
            data.add(message);
            lvChats.scrollTo(lvChats.getItems().size());
        });
    }

    public void onSendButtonAction(MouseEvent mouseEvent) {
        sendMessage();
    }

    public void taMessageAction(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
            sendMessage();
        }
    }

    private void sendMessage() {
        String text = taMessage.getText();
        if(text.equals("")){
            return;
        }
        String time = new SimpleDateFormat("H:mm:ss").format(new Date().getTime());

        Message message = new Message.MessageBuilder().
                text(text).
                from(ClientThread.getInstance().getClient().getEmail()).
                time(time).
                to(friend.getEmail()).build();

        addYourMessage(message);
        taMessage.setText("");
        thread.sendMessage(message);
    }

    private void registerChat(String email){
        ContactsController.chats.put(email, this);
    }

    private void addYourMessage(Message message){
        Message messageMy = new Message.MessageBuilder().
                from("You").
                to(message.getTo()).
                time(message.getTime()).
                text(message.getText()).build();
        receiveMessage(messageMy);
    }

    public void ivFileClickAction(MouseEvent mouseEvent) {
        if(mouseEvent.getButton() == MouseButton.PRIMARY) {
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(null);
            thread.sendFile(file);
        }
    }
}