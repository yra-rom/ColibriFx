package gui.chat;

import gui.Controller;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatController implements Controller {
    //private ClientThread thread = ClientThread.getInstance();

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


    @FXML
    public void initialize() {
        lNick.setText("Friend's Nick");
        initTA();
        initImageListeners();

        data = FXCollections.observableArrayList();
        lvChats.setItems(data);
        //thread.setController(this);
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
        //taMessage.setPrefSize(200, 60);
        taMessage.setWrapText(true);
    }

    public void addMessage(Message message){
        data.add(message);
        lvChats.scrollTo(lvChats.getItems().size());
    }


    public void onSendButtonAction(MouseEvent mouseEvent) {
        String text = taMessage.getText();
        if(text.equals("")){
            return;
        }
        String from = "You";
        String time = new SimpleDateFormat("H:mm:ss").format(new Date().getTime());

        Message message = new Message.MessageBuilder().text(text).from(from).time(time).build();

        addMessage(message);
        taMessage.setText("");
    }
}