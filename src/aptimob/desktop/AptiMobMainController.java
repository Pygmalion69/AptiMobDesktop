/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.entities.Country;
import aptimob.desktop.entities.User;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.xml.stream.EventFilter;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author helfrich
 */
public class AptiMobMainController implements Initializable {

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuUsersGroups;
    @FXML
    private Menu menuDomains;
    @FXML
    Menu menuSkills;
    @FXML
    private MenuItem menuItemAddUser;
    @FXML
    private SplitPane horizontalSplit;
    @FXML
    private BorderPane basePane;
    @FXML
    private AnchorPane leftPane;
    @FXML
    private Accordion leftAccordion;
    @FXML
    private TitledPane userTiteledPane;
    @FXML
    private ScrollPane overview;
    @FXML
    private Pane detail;
    @FXML
    private TreeView userTreeView;
    @FXML
    private TreeView availTreeView;
    @FXML
    private TreeView skillNavigationTreeView;
    @FXML
    private TreeView messageTreeView;
    @FXML
    private TreeView resourceNavigationTreeView;
    @FXML
    private AnchorPane resourceNavigationTreeAnchorPane;
    @FXML
    private AnchorPane userTreeAnchorPane;
    @FXML
    private AnchorPane availTreeAnchorPane;
    @FXML
    private AnchorPane skillNavigationTreeAnchorPane;
    @FXML
    private AnchorPane messageTreeAnchorPane;
    @FXML
    private AnchorPane rootWindow;
//    @FXML
//    Menu menuMap;
//    @FXML
//    CheckMenuItem osmCheck;
//    @FXML
//    CheckMenuItem wmsOsmCheck;
    private UserTableController userTableController;
    private UserDetailController userDetailController;
    private ResourceBundle rb;
    private MapController mapController;
    TreeItem<String> userRootItem;
    TreeItem<String> availRootItem;
    private AvailabilityTableController availabilityTableController;
    private RestClient restClient;
    private Preferences prefs;
    private int userId;
    private int domain;
    private List<String> currentEndUserGroups;
    private boolean admin;
    private boolean root;
    private List<Country> countries;
    private HashMap<Integer, String> countryMap;
    private TreeItem<String> skillNavigationRootItem;
    private SkillsController skillsController;
    private SkillDetailController skillDetailController;
    private TreeItem<String> messageRootItem;
    private User user;
    private AnchorPane messagesPane;
    private MessageTableController messageTableController;
    private AnchorPane messageDetailPane;
    private MessageDetailController messageDetailController;
    private TreeItem resourceNavigationTreeItem;
    private TreeItem resourceNavigationRootItem;

    public AptiMobMainController() {
        //testButton.setText("Test");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle rb) {

        this.rb = rb;

//        overview.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
//
//            @Override
//            public void handle(ScrollEvent t) {
//                overview.setVvalue(overview.getVvalue() - t.getDeltaY());
//                t.consume();
//            }      
//        });

        try {
            restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
        } catch (MalformedURLException e) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, e);
        }
        prefs = Preferences.userNodeForPackage(getClass());
        userId = prefs.getInt("user_id", 0);

        user = restClient.getUserById(userId);


        domain = user.getDomain();
        prefs.putInt("domain", domain);

        countries = restClient.getCountries();
        countryMap = new HashMap<Integer, String>();
        for (Country c : countries) {
            countryMap.put(c.getId(), c.getName());
        }

        currentEndUserGroups = restClient.getUserGroups(userId);

        for (String group : currentEndUserGroups) {
            if (group.equals("admin")) {
                admin = true;
            }
            if (group.equals("root")) {
                root = true;
            }
        }

        if (root) {
            menuDomains.setVisible(true);
            menuDomains.setDisable(false);
        }

        populateTrees();

        //AnchorPane overviewTable;

        //overviewTable = FXMLLoader.load(getClass().getResource("TableView.fxml"));
        //userTiteledPane.setDisable(true);
        menuBar.prefWidthProperty().bind(rootWindow.widthProperty());
        userTreeView.prefHeightProperty().bind(userTreeAnchorPane.heightProperty());
        userTreeView.prefWidthProperty().bind(userTreeAnchorPane.widthProperty());
        availTreeView.prefHeightProperty().bind(availTreeAnchorPane.heightProperty());
        availTreeView.prefWidthProperty().bind(availTreeAnchorPane.widthProperty());
        skillNavigationTreeView.prefHeightProperty().bind(skillNavigationTreeAnchorPane.heightProperty());
        skillNavigationTreeView.prefWidthProperty().bind(skillNavigationTreeAnchorPane.widthProperty());
        resourceNavigationTreeView.prefHeightProperty().bind(resourceNavigationTreeAnchorPane.heightProperty());
        resourceNavigationTreeView.prefWidthProperty().bind(resourceNavigationTreeAnchorPane.widthProperty());
        messageTreeView.prefHeightProperty().bind(messageTreeAnchorPane.heightProperty());
        messageTreeView.prefWidthProperty().bind(messageTreeAnchorPane.widthProperty());
        //userTreeView.setStyle("-fx-background-color: transparent;");
        leftAccordion.prefWidthProperty().bind(leftPane.widthProperty());
        leftAccordion.prefHeightProperty().bind(basePane.heightProperty());

        //overview.setContent(overviewTable);

        // Startup Action
        try {
            // Startup Action
            handleMap(null);
        } catch (IOException ex) {
            Logger.getLogger(AptiMobMainController.class.getName()).log(Level.SEVERE, null, ex);
        }


//        osmCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue ov,
//                    Boolean old_val, Boolean new_val) {
//                if (new_val) {
//                    wmsOsmCheck.selectedProperty().setValue(false);
//                    if (null != mapController) {
//                        mapController.setMapSource("osm");
//                    }
//                }
//            }
//        });
//        
//        wmsOsmCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue ov,
//                    Boolean old_val, Boolean new_val) {
//                if (new_val) {
//                    osmCheck.selectedProperty().setValue(false);
//                     if (null != mapController) {
//                        mapController.setMapSource("wmsosm");
//                    }
//                }
//            }
//        });

    }

    private void populateTrees() {
        userRootItem = new TreeItem<String>(rb.getString("Maintain"));
        userRootItem.setExpanded(true);
        //TreeItem<String> usersItem = new TreeItem<String>(rb.getString("Users"));
        TreeItem<String> maintainUsersItem = new TreeItem<String>(rb.getString("MaintainUsers"));
        TreeItem<String> addUserItem = new TreeItem<String>(rb.getString("AddUser"));
        TreeItem<String> groupsItem = new TreeItem<String>(rb.getString("Groups"));
        //usersItem.getChildren().addAll(maintainUsersItem);
        userRootItem.getChildren().addAll(maintainUsersItem, addUserItem);
        userTreeView.setRoot(userRootItem);

        userTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> t, TreeItem<String> t1) {
                //System.out.println(t1.getValue());
                if (t1 != null) {
                    if (t1.getValue().equals(rb.getString("MaintainUsers"))) {
                        try {
                            handleMaintainUsers(null);
                        } catch (IOException ex) {
                            Logger.getLogger(AptiMobMainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (t1.getValue().equals(rb.getString("AddUser"))) {
                        try {
                            handleAddUser(null);
                        } catch (IOException ex) {
                            Logger.getLogger(AptiMobMainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

        availRootItem = new TreeItem<String>(rb.getString("Overview"));
        availRootItem.setExpanded(true);
        TreeItem<String> mapItem = new TreeItem<String>(rb.getString("Map"));
        availRootItem.getChildren().addAll(mapItem);
        availTreeView.setRoot(availRootItem);

        availTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> t, TreeItem<String> t1) {
                //System.out.println(t1.getValue());
                if (t1 != null && t1.getValue().equals(rb.getString("Map"))) {
                    try {
                        handleMap(null);
                    } catch (IOException ex) {
                        Logger.getLogger(AptiMobMainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        skillNavigationRootItem = new TreeItem<String>(rb.getString("Overview"));
        skillNavigationRootItem.setExpanded(true);
        TreeItem<String> skillNavigationItem = new TreeItem<String>(rb.getString("Skills"));
        skillNavigationRootItem.getChildren().addAll(skillNavigationItem);
        skillNavigationTreeView.setRoot(skillNavigationRootItem);

        skillNavigationTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> t, TreeItem<String> t1) {
                //System.out.println(t1.getValue());
                if (t1 != null) {
                    if (t1.getValue().equals(rb.getString("Skills"))) {
                        try {
                            handleSkills(null);
                        } catch (IOException ex) {
                            Logger.getLogger(AptiMobMainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

        resourceNavigationRootItem = new TreeItem(rb.getString("Maintain"));
        resourceNavigationRootItem.setExpanded(true);
        TreeItem<String> resourceNavigationItem = new TreeItem<String>(rb.getString("Resources"));
        resourceNavigationRootItem.getChildren().addAll(resourceNavigationItem);
        resourceNavigationTreeView.setRoot(resourceNavigationRootItem);

        resourceNavigationTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> t, TreeItem<String> t1) {
                //System.out.println(t1.getValue());
                if (t1 != null) {
                    if (t1.getValue().equals(rb.getString("Resources"))) {
                        try {
                            handleResources(null);
                        } catch (IOException ex) {
                            Logger.getLogger(AptiMobMainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });


        messageRootItem = new TreeItem<String>(user.getUsername());
        messageRootItem.setExpanded(true);
        TreeItem<String> inboxItem = new TreeItem<String>(rb.getString("Inbox"));
        TreeItem<String> sentItem = new TreeItem<String>(rb.getString("Sent"));
        messageRootItem.getChildren().addAll(inboxItem, sentItem);
        messageTreeView.setRoot(messageRootItem);

        messageTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> t, TreeItem<String> t1) {
                //System.out.println(t1.getValue());
                if (t1 != null) {
                    if (t1.getValue().equals(rb.getString("Inbox"))) {
                        try {
                            handleInbox(null);
                        } catch (IOException ex) {
                            Logger.getLogger(AptiMobMainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (t1.getValue().equals(rb.getString("Sent"))) {
                        try {
                            handleSent(null);
                        } catch (IOException ex) {
                            Logger.getLogger(AptiMobMainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
    }

    @FXML
    private void resetTreeSelection() {
        // System.out.println("Resetting tree selection.");
        userTreeView.getSelectionModel().clearSelection();
        userTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        availTreeView.getSelectionModel().clearSelection();
        availTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        skillNavigationTreeView.getSelectionModel().clearSelection();
        skillNavigationTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        messageTreeView.getSelectionModel().clearSelection();
        messageTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void setCursor(Cursor cursor) {
        basePane.getScene().setCursor(cursor);
    }

    @FXML
    private void handleInbox(ActionEvent event) throws IOException {
        AnchorPane detailPane;

        if (userDetailController != null) {
            userDetailController.checkChanges();
        }

        FXMLLoader fxmlMessagesLoader = new FXMLLoader(getClass().getResource("MessageTable.fxml"), ResourceBundle.getBundle("bundles.strings"));
        messagesPane = (AnchorPane) fxmlMessagesLoader.load();
        messageTableController = fxmlMessagesLoader.getController();
        overview.setContent(messagesPane);

        FXMLLoader fxmlMessageDetailLoader = new FXMLLoader(getClass().getResource("MessageDetail.fxml"), ResourceBundle.getBundle("bundles.strings"));
        detailPane = (AnchorPane) fxmlMessageDetailLoader.load();
        messageDetailController = fxmlMessageDetailLoader.getController();
        detail.getChildren().clear();
        detail.getChildren().add(detailPane);
        messageDetailController.setContainer(detail);
        messageDetailController.setMessageTableController(messageTableController);

        messageTableController.setMessageDetailController(messageDetailController);
        messageTableController.setMainController(this);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (messageTableController != null) {
                    messageTableController.setMessages(MessageViewMode.IN);
                }
            }
        });
    }

    @FXML
    private void handleSent(ActionEvent event) throws IOException {
        AnchorPane detailPane;

        if (userDetailController != null) {
            userDetailController.checkChanges();
        }

        FXMLLoader fxmlMessagesLoader = new FXMLLoader(getClass().getResource("MessageTable.fxml"), ResourceBundle.getBundle("bundles.strings"));
        messagesPane = (AnchorPane) fxmlMessagesLoader.load();
        messageTableController = fxmlMessagesLoader.getController();
        overview.setContent(messagesPane);

        FXMLLoader fxmlMessageDetailLoader = new FXMLLoader(getClass().getResource("MessageDetail.fxml"), ResourceBundle.getBundle("bundles.strings"));
        detailPane = (AnchorPane) fxmlMessageDetailLoader.load();
        messageDetailController = fxmlMessageDetailLoader.getController();
        detail.getChildren().clear();
        detail.getChildren().add(detailPane);
        messageDetailController.setContainer(detail);
        messageDetailController.setMessageTableController(messageTableController);

        messageTableController.setMessageDetailController(messageDetailController);
        messageTableController.setMainController(this);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (messageTableController != null) {
                    messageTableController.setMessages(MessageViewMode.SENT);
                }
            }
        });
    }

    @FXML
    private void handleSkills(ActionEvent event) throws IOException {
        AnchorPane skillsPane, detailPane;

        if (userDetailController != null) {
            userDetailController.checkChanges();
        }

        menuSkills.setDisable(false);
//        menuMap.setDisable(true);

        FXMLLoader fxmlSkillsLoader = new FXMLLoader(getClass().getResource("Skills.fxml"), ResourceBundle.getBundle("bundles.strings"));
        skillsPane = (AnchorPane) fxmlSkillsLoader.load();
        skillsController = fxmlSkillsLoader.getController();
        overview.setContent(skillsPane);
        skillsController.setContainer(overview);

        FXMLLoader fxmlDetailLoader = new FXMLLoader(getClass().getResource("SkillDetail.fxml"), ResourceBundle.getBundle("bundles.strings"));
        detailPane = (AnchorPane) fxmlDetailLoader.load();
        skillDetailController = fxmlDetailLoader.getController();
        // detail.setContent(detailPane);
        detail.getChildren().clear();
        detail.getChildren().add(detailPane);
        skillDetailController.setContainer(detail);

        skillDetailController.setSkillsController(skillsController);
        skillsController.setSkillDetailController(skillDetailController);
        skillDetailController.setMainController(this);
    }

    @FXML
    private void handleResources(ActionEvent event) throws IOException {
        AnchorPane resourcesPane, detailPane;
        
         if (userDetailController != null) {
            userDetailController.checkChanges();
        }
        
    }
    public void reloadInbox() throws IOException {
        this.handleInbox(null);
    }

    public void reloadSent() throws IOException {
        this.handleSent(null);
    }

    public void reloadSkills() throws IOException {
        this.handleSkills(null);
    }

    public void reloadMap() throws IOException {
        this.handleMap(null);
    }

    public void reloadUsers() throws IOException {
        this.handleMaintainUsers(null);
    }

    @FXML
    private void handleMap(ActionEvent event) throws IOException {
        StackPane mapPane;
        AnchorPane tablePane;

        if (userDetailController != null) {
            userDetailController.checkChanges();
        }
        if (skillDetailController != null) {
            skillDetailController.checkChanges();
        }

        menuSkills.setDisable(true);
//        menuMap.setDisable(false);

        FXMLLoader fxmlMapLoader = new FXMLLoader(getClass().getResource("Map.fxml"), ResourceBundle.getBundle("bundles.strings"));
        mapPane = (StackPane) fxmlMapLoader.load();
        mapController = fxmlMapLoader.getController();
        overview.setContent(mapPane);
        mapController.setContainer(overview);
        mapController.setCountryMap(countryMap);

        FXMLLoader fxmlTableLoader = new FXMLLoader(getClass().getResource("AvailabilityTable.fxml"), ResourceBundle.getBundle("bundles.strings"));
        tablePane = (AnchorPane) fxmlTableLoader.load();
        //detail.setContent(tablePane);
        detail.getChildren().clear();
        detail.getChildren().add(tablePane);
        availabilityTableController = fxmlTableLoader.getController();
        //detail.setContent(tablePane);
        availabilityTableController.setContainer(detail);
        availabilityTableController.setMainController(this);
        availabilityTableController.setMapController(mapController);
        mapController.setAvailabilityTableController(availabilityTableController);
    }

    @FXML
    private void handleAddUser(ActionEvent event) throws IOException {
//        Stage userStage = new Stage();
//        userStage.setScene(new Scene((Parent) FXMLLoader.load(getClass().getResource("User.fxml"), ResourceBundle.getBundle("bundles.strings"))));
//        userStage.show();
        resetTreeSelection();
        handleMaintainUsers(event);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (userDetailController != null) {
                    userDetailController.handleNewUser();
                }
            }
        });
    }

    public void reloadMaintainUsers() throws IOException {
        AnchorPane usersPane, detailPane;
        this.handleMaintainUsers(null);
    }

    @FXML
    private void handleMaintainUsers(ActionEvent event) throws IOException {

        final AnchorPane usersPane, detailPane;

        setCursor(Cursor.WAIT);

        if (userDetailController != null) {
            userDetailController.checkChanges();
        }
        if (skillDetailController != null) {
            skillDetailController.checkChanges();
        }

        menuSkills.setDisable(true);
//        menuMap.setDisable(true);

        FXMLLoader fxmlTableLoader = new FXMLLoader(getClass().getResource("UserTable.fxml"), ResourceBundle.getBundle("bundles.strings"));
        usersPane = (AnchorPane) fxmlTableLoader.load();
        userTableController = fxmlTableLoader.getController();
        overview.setContent(usersPane);
        userTableController.setMainController(this);

        // TODO:
//        overview.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {    
//         @Override
//            public void handle(ScrollEvent event) {
//               Event.fireEvent(usersPane.getScene(), event);
//            }
//        });

        FXMLLoader fxmlDetailLoader = new FXMLLoader(getClass().getResource("UserDetail.fxml"), ResourceBundle.getBundle("bundles.strings"));
        detailPane = (AnchorPane) fxmlDetailLoader.load();
        //detail.setContent(detailPane);
        detail.getChildren().clear();
        detail.getChildren().add(detailPane);
        userDetailController = (UserDetailController) fxmlDetailLoader.getController();
        //userDetailController.setUserField("test");
        userDetailController.setContainer(detail);
        userTableController.setUserDetailController(userDetailController);
        userDetailController.setUserTableController(userTableController);
        userDetailController.setMainController(this);

//        Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                    userDetailController.setUserField("test");
//                }
//            });
    }

    @FXML
    private void handleMaintainDomains(ActionEvent event) throws IOException {
        Stage domainStage = new Stage();
        domainStage.setScene(new Scene((Parent) FXMLLoader.load(getClass().getResource("Domains.fxml"), ResourceBundle.getBundle("bundles.strings"))));
        domainStage.setTitle(rb.getString("ApplicationName") + " - " + rb.getString("MaintainDomains"));
        domainStage.show();
    }

    @FXML
    private void handleMaintainCategories(ActionEvent event) throws IOException {
        Stage categoryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SkillCategories.fxml"), ResourceBundle.getBundle("bundles.strings"));
        //domainStage.setScene(new Scene((Parent) FXMLLoader.load(getClass().getResource("SkillCategories.fxml"), ResourceBundle.getBundle("bundles.strings"))));
        categoryStage.setScene(new Scene((Parent) loader.load()));
        ((SkillCategoriesController) loader.getController()).setMainController(this);
        categoryStage.setTitle(rb.getString("ApplicationName") + " - " + rb.getString("MaintainCategories"));
        categoryStage.show();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        if (userDetailController != null) {
            userDetailController.checkChanges();
        }
        if (skillDetailController != null) {
            skillDetailController.checkChanges();
        }
        rootWindow.getScene().getWindow().hide();
    }
}
