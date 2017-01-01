package gui.chat;

import gui.Controller;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class ChatController implements Controller {
    public static final String STYLE_ON_HOVER = "-fx-effect: dropshadow(gaussian, #9ba6a8, 15, 0.0001, 0.05, 0.05);";
    //private ClientThread thread = ClientThread.getInstance();

//    @FXML
//    private TextArea taMessage;

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
    public void initialize() {
        lNick.setText("Friend's Nick");
        initTA();
        initImageListeners();
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


//        ivCall.setOnMouseEntered(t -> ivCall.setStyle(STYLE_ON_HOVER));
//        ivCall.setOnMouseExited(t -> ivCall.setStyle(""));
//
//        ivVideo.setOnMouseEntered(t -> ivVideo.setStyle(STYLE_ON_HOVER));
//        ivVideo.setOnMouseExited(t -> ivVideo.setStyle(""));
//
//        ivFile.setOnMouseEntered(t -> ivFile.setStyle(STYLE_ON_HOVER));
//        ivFile.setOnMouseExited(t -> ivFile.setStyle(""));
//
//        ivSend.setOnMouseEntered(t -> ivSend.setStyle(STYLE_ON_HOVER));
//        ivSend.setOnMouseExited(t -> ivSend.setStyle(""));
    }

    private void initTA() {
        //taMessage.setPrefSize(200, 40);
        //taMessage.setWrapText(true);
    }

    public void anchorClick(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.getX());
    }
}