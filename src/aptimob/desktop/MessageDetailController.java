package aptimob.desktop;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import aptimob.desktop.entities.Message;
import aptimob.desktop.entities.User;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author helfrich
 */
public class MessageDetailController implements Initializable {

    @FXML
    AnchorPane basePane;
    @FXML
    ToolBar detailToolBar;
    @FXML
    ScrollPane scrollPane;
    @FXML
    TextField fromField;
    @FXML
    TextField toField;
    @FXML
    TextField subjectField;
    @FXML
    TextArea bodyField;
    @FXML
    private Button replyButton;
    private Pane detailContainer;
    private MessageTableController messageTableControllerController;
    private AptiMobMainController aptiMobMainController;
    private ResourceBundle bundle;
    private Message message;
    private FXMLLoader newMessageLoader;
    private NewMessageController newMessageController;
    private RestClient restClient;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.bundle = rb;
    }

    public void setMessage(Message message) {
        this.message = message;
        fromField.setText(message.getFrom());
        toField.setText(message.getTo());
        subjectField.setText(message.getSubject());
        bodyField.setText(message.getBody());
    }

    public void replyDisabled(boolean b) {
        replyButton.setDisable(b);
    }
    
    @FXML
    private void handleReplyButton(ActionEvent event) throws IOException {
        Stage newMessageStage = new Stage();
        newMessageLoader = new FXMLLoader(getClass().getResource("NewMessage.fxml"), ResourceBundle.getBundle("bundles.strings"));
        newMessageStage.setScene(new Scene((Parent) newMessageLoader.load()));
        newMessageStage.setTitle(bundle.getString("ApplicationName") + " - " + bundle.getString("Message"));
        newMessageStage.show();
        newMessageController = newMessageLoader.getController();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setNewMessageRecipientsAndRef();
            }
        });
    }
    
    private void setNewMessageRecipientsAndRef() {
        try {
            restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
        } catch (MalformedURLException e) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, e);
        }
        List<User> users = new ArrayList();
        User user = restClient.getUserById(message.getSenderId());
        users.add(user);
        
        newMessageController.setUsers(users);
        newMessageController.setRefId(message.getId());
        newMessageController.setSubject(message.getSubject());
    }

    @FXML
    private void handleRefreshButton(ActionEvent event) {
        messageTableControllerController.handleRefreshButton(event);
    }

    public void setContainer(Pane container) {
        this.detailContainer = container;
        detailToolBar.
                prefWidthProperty().
                bind(detailContainer.widthProperty());
        basePane.
                prefHeightProperty().
                bind(detailContainer.heightProperty());
//        scrollableDetailPane.prefWidthProperty().
//                bind(detailContainer.widthProperty());
        scrollPane.prefWidthProperty().
                bind(detailContainer.widthProperty());
        scrollPane.prefHeightProperty().
                bind(detailContainer.heightProperty().subtract(30d));
//        Toolbar!:
//        bind(detailContainer.heightProperty().subtract(30d));
    }

    public void setMessageTableController(MessageTableController controller) {
        this.messageTableControllerController = controller;
    }

    public void setMainController(AptiMobMainController controller) {
        this.aptiMobMainController = controller;
    }
}
