package gui.chat;

import gui.Controller;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ChatController implements Controller {
    //private ClientThread thread = ClientThread.getInstance();

//    @FXML
//    private TextArea taMessage;

    @FXML
    private ImageView ivCall;
    @FXML
    private ImageView ivVideo;
    @FXML
    private ImageView ivFile;

    @FXML
    public void initialize() {
        initTA();
        initImageListeners();
        //thread.setController(this);
    }

    private void initImageListeners() {
        ivCall.setOnMouseEntered(t -> ivCall.setStyle("-fx-effect: dropshadow(gaussian, #9ba6a8, 15, 0.0001, 0.05, 0.05);"));
        ivCall.setOnMouseExited(t -> ivCall.setStyle(""));

        ivVideo.setOnMouseEntered(t -> ivVideo.setStyle("-fx-effect: dropshadow(gaussian, #9ba6a8, 15, 0.0001, 0.05, 0.05);"));
        ivVideo.setOnMouseExited(t -> ivVideo.setStyle(""));

        ivFile.setOnMouseEntered(t -> ivFile.setStyle("-fx-effect: dropshadow(gaussian, #9ba6a8, 15, 0.0001, 0.05, 0.05);"));
        ivFile.setOnMouseExited(t -> ivFile.setStyle(""));
    }

    private void initTA() {
        //taMessage.setPrefSize(200, 40);
        //taMessage.setWrapText(true);
    }

    public void anchorClick(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.getX());
    }
}