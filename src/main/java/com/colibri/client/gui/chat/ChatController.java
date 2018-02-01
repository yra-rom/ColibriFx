package com.colibri.client.gui.chat;

import com.colibri.client.ClientThread;
import com.colibri.client.constants.Activity;
import com.colibri.client.gui.Controller;
import com.colibri.client.gui.contacts.ContactsController;
import com.colibri.common.client.Client;
import com.colibri.common.constants.SendKeys;
import com.colibri.common.dto.Message;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ChatController implements Controller {
    private ClientThread thread = ClientThread.getInstance();

    @FXML
    private TextArea taMessage;

    @FXML
    private Label lNick;

    @FXML
    private ImageView ivCall;
    @FXML
    private ImageView ivVideo;
    @FXML
    private ImageView ivFile;
    @FXML
    private ImageView ivSend;

    @FXML
    private ListView<Message> lvChats;
    private ObservableList<Message> data;

    private Client friend;

    @FXML
    public void initialize() {
        initImageListeners();

        if (!ContactsController.stack.isEmpty()) {
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

        for (ImageView iv : ivs) {
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

    public void onSendButtonAction(MouseEvent mouseEvent) {
        sendMessage();
    }

    public void taMessageAction(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
            sendMessage();
        }
    }

    public void receiveMessage(Message message) {
        Platform.runLater(() -> {
            data.add(message);
            lvChats.scrollTo(lvChats.getItems().size());
        });
    }

    private void sendMessage() {
        String text = taMessage.getText();
        if (text.equals("")) {
            return;
        }
        String time = new SimpleDateFormat("H:mm:ss").format(new Date().getTime());

        Message message = Message.builder()
                .text(text)
                .from(ClientThread.getInstance().getClient().getEmail())
                .time(time)
                .to(friend.getEmail()).build();

        addYourMessage(message);
        taMessage.setText("");
        thread.sendMessage(message);
    }

    private void addYourMessage(Message message) {
        Message messageMy = Message.builder()
                .from("You")
                .to(message.getTo())
                .time(message.getTime())
                .text(message.getText())
                .build();
        receiveMessage(messageMy);
    }

    private void registerChat(String email) {
        ContactsController.chats.put(email, this);
    }

    public void ivFileClickAction(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                thread.sendFile(file, friend);
                Message message = Message.builder()
                        .from("You")
                        .text("Sent file " + file.getName())
                        .time(getCurrentTime())
                        .build();

                addYourMessage(message);
            }
        }
    }

    public File getConfirmation(HashMap<String, Object> map) {

        if (SendKeys.FILE_START.equals(map.get(SendKeys.TITLE))) {
            String email = (String) map.get(SendKeys.FROM);
            String fileName = (String) map.get(SendKeys.FILE_NAME);

            final FutureTask query = new FutureTask(new Callable() {
                @Override
                public Object call() throws Exception {
                    File file = null;

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Do you want to save file " + fileName + " from " + email + "?");


                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("resources/images/MainIcon.png")));

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        FileChooser chooser = new FileChooser();
                        chooser.setTitle("Save File - " + Activity.AppName);

                        String[] splited = fileName.split("\\.");
                        String format = "." + splited[splited.length - 1];

                        chooser.setInitialFileName(fileName + format);

                        chooser.getExtensionFilters().addAll(
                                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                                new FileChooser.ExtensionFilter("All Files", "*.*"));


                        file = chooser.showSaveDialog(ContactsController.stages.get(email));
                    }
                    return file;
                }
            });

            Platform.runLater(query);

            File file = null;
            try {
                file = (File) query.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            Message message = Message.builder()
                    .from((String) map.get(SendKeys.FROM))
                    .text((file == null ? "Rejected " : "Confirmed ") + " file " + (String) map.get(SendKeys.FILE_NAME))
                    .time(new SimpleDateFormat("H:mm:ss").format(new Date().getTime()))
                    .build();

            receiveMessage(message);

            return file;
        }


        return null;
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("H:mm:ss").format(new Date().getTime());
    }
}