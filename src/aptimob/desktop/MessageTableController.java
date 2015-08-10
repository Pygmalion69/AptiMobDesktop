/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.entities.Message;
import aptimob.desktop.entities.User;
import aptimob.desktop.models.MessageModel;
import aptimob.desktop.models.UserModel;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author helfrich
 */
public class MessageTableController implements Initializable {

    @FXML
    AnchorPane basePane;
    @FXML
    TableView messageTable;
    @FXML
    TableColumn fromCol;
    @FXML
    TableColumn toCol;
    @FXML
    TableColumn subjectCol;
    @FXML
    TableColumn dateCol;
    private ResourceBundle bundle;
    private ObservableList<MessageModel> messageModelList;
    private RestClient restClient;
    private Preferences prefs;
    private int currentEndUserId;
    private List<Message> messages;
    private MessageDetailController messageDetailController;
    private MessageViewMode viewMode;
    private AptiMobMainController aptiMobMainController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.bundle = rb;

        prefs = Preferences.userNodeForPackage(getClass());
        currentEndUserId = prefs.getInt("user_id", 0);

        try {
            restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
        } catch (MalformedURLException e) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, e);
        }

        messageTable.setEditable(false);

        fromCol.setCellValueFactory(
                new PropertyValueFactory<UserModel, String>("from"));
        toCol.setCellValueFactory(
                new PropertyValueFactory<UserModel, String>("to"));
        subjectCol.setCellValueFactory(
                new PropertyValueFactory<UserModel, String>("subject"));
        dateCol.setCellValueFactory(
                new PropertyValueFactory<UserModel, String>("dateTime"));

        messageModelList = messageTable.getItems();

        messageTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                MessageModel selectedMessageModel = (MessageModel) newValue;
                //System.out.println(selectedUserModel.username.get() + " selected.");
                for (Message message : messages) {
                    if (message.getId() == selectedMessageModel.getId()) {
                        if (viewMode == MessageViewMode.IN) {
                           messageDetailController.replyDisabled(false); 
                        }
                        messageDetailController.setMessage(message);
                        break;
                    }
                }
            }
        });

        basePane.widthProperty()
                .addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable,
                    Object oldValue, Object newValue) {
                Double width = (Double) newValue;
                messageTable.setPrefWidth(width);
            }
        });

        basePane.heightProperty()
                .addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable,
                            Object oldValue, Object newValue) {
                        Double height = (Double) newValue;
                        messageTable.setPrefHeight(height);
                    }
                });

    }

    public void setMessages(MessageViewMode viewMode) {
        this.viewMode = viewMode;
        switch (viewMode) {
            case IN:
                //messageDetailController.replyDisabled(false);
                messages = restClient.getMessagesIn(currentEndUserId);
                break;
            case SENT:
                //messageDetailController.replyDisabled(true);
                messages = restClient.getMessagesSent(currentEndUserId);
        }

        Comparator<Message> messageDateComparator = new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return (int) o2.getTimestamp() - (int) o1.getTimestamp();
            }
        };

        if (null != messages && messages.size() > 0) {
            Collections.sort(messages, messageDateComparator);
            messageModelList.clear();

            for (Message message : messages) {
                MessageModel messageModel = new MessageModel();
                messageModel.setId(message.getId());
                messageModel.setFrom(message.getFrom());
                messageModel.setTo(message.getTo());
                messageModel.setSubject(message.getSubject());
                messageModel.setBody(message.getFrom());
                messageModel.setDateTime((new Date(message.getTimestamp() * 1000)).toString());
                messageModelList.add(messageModel);
            }
        }
    }

    @FXML
    public void handleRefreshButton(ActionEvent event) {
        try {
            switch (viewMode) {
                case SENT:
                    aptiMobMainController.reloadSent();
                    break;
                case IN:
                    aptiMobMainController.reloadInbox();
            }
        } catch (IOException ex) {
            Logger.getLogger(AvailabilityTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setMainController(AptiMobMainController controller) {
        this.aptiMobMainController = controller;
    }

    public void setMessageDetailController(MessageDetailController controller) {
        this.messageDetailController = controller;
    }
}
