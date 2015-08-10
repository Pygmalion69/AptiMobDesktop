/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.entities.Message;
import aptimob.desktop.entities.User;
import aptimob.desktop.models.MessageModel;
import aptimob.desktop.models.UserModel;
import com.sun.deploy.util.StringUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author helfrich
 */
public class NewMessageController implements Initializable {

    @FXML
    AnchorPane basePane;
    @FXML
    Button sendButton;
    @FXML
    ListView recipientsListView;
    @FXML
    TextField subjectField;
    @FXML
    TextArea messageField;
    private List<User> users;
    private ResourceBundle bundle;
    private ObservableList userModelList;
    private RestClient restClient;
    private Preferences prefs;
    private int userId;
    private User currentEndUser;
    private Message message;
    private MessageModel messageModel;
    private int refId;
    private Stage newMessageStage;
    private int domain;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.bundle = rb;

        try {
            restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
        } catch (MalformedURLException ex) {
            Logger.getLogger(UserDetailController.class.getName()).log(Level.SEVERE, null, ex);
        }
        prefs = Preferences.userNodeForPackage(getClass());
        userId = prefs.getInt("user_id", 0);
        domain = prefs.getInt("domain", 0);
        currentEndUser = restClient.getUserById(userId);

        recipientsListView.setCellFactory(new Callback<ListView<UserModel>, ListCell<UserModel>>() {
            @Override
            public ListCell<UserModel> call(ListView<UserModel> lv) {

                return new recipientCell();
            }
        });
        userModelList = recipientsListView.getItems();

        message = new Message();
        messageModel = new MessageModel();

        Bindings.bindBidirectional(subjectField.textProperty(), messageModel.subjectProperty());
        Bindings.bindBidirectional(messageField.textProperty(), messageModel.bodyProperty());
    }

    public void setUsers(List<User> users) {
        this.users = users;
        for (User u : users) {
            userModelList.add(new UserModel(u.getId(), u.getUsername(), u.getDomain(), u.getFirstName(), u.getLastName(), u.isAvailable(), u.getCity(), u.getCellPhone(), u.getPhone()));
        }
        recipientsListView.setItems(userModelList);
    }
    
    public void setSubject(String subject) {
        messageModel.setSubject(subject);
    }
    
    public void setRefId(int refId) {
        this.refId = refId;
    }

    @FXML
    private void handleSendButton(ActionEvent event) {
        newMessageStage = (Stage) basePane.getScene().getWindow();
        List<Integer> userIds = new ArrayList<>();
        final List<String> usernames = new ArrayList<>();
        for (User u : users) {
            userIds.add(u.getId());
            usernames.add(u.getUsername());
        }
        final String subject = null != messageModel.getSubject() ? messageModel.getSubject() : "";
        String body = null != messageModel.getBody() ? messageModel.getBody() : "";
        restClient.sendMessage(userIds, refId, currentEndUser.getId(), domain, StringUtils.join(usernames, ", "), currentEndUser.getUsername(), subject, body);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                restClient.notify(usernames, subject);
            }
        });
        newMessageStage.hide();
    }

    static class recipientCell extends ListCell<UserModel> {

        @Override
        public void updateItem(UserModel item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getUsername());
            }
        }
    }
}
