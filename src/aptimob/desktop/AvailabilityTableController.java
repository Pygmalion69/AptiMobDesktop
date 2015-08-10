/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.controls.BooleanCell;
import aptimob.desktop.entities.Skill;
import aptimob.desktop.entities.SkillCategory;
import aptimob.desktop.entities.User;
import aptimob.desktop.models.SkillCategoryModel;
import aptimob.desktop.models.SkillModel;
import aptimob.desktop.models.UserModel;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author helfrich
 */
public class AvailabilityTableController implements Initializable {

    @FXML
    private ComboBox skillCategoryComboBox;
    @FXML
    private ComboBox skillComboBox;
    @FXML
    Button setLocationButton;
    @FXML
    private Button openButton;
    @FXML
    private Button messageButton;
    @FXML
    private Button refreshButton;
    @FXML
    ComboBox distanceComboBox;
    @FXML
    private AnchorPane basePane;
    @FXML
    private ScrollPane detail;
    @FXML
    private TableView<UserModel> userTableView;
    @FXML
    private TableColumn userCol;
    @FXML
    private TableColumn firstNameCol;
    @FXML
    private TableColumn lastNameCol;
    @FXML
    private TableColumn availableCol;
    @FXML
    private TableColumn cityCol;
    @FXML
    private TableColumn cellPhoneCol;
    @FXML
    private TableColumn phoneCol;
    @FXML
    private ToolBar availToolBar;
    @FXML
    ScrollPane availabilityScrollPane;
    @FXML
    ChoiceBox allUsersChoice;
    private ResourceBundle bundle;
    RestClient restClient;
    List<User> userList = new ArrayList<>();
    ObservableList<UserModel> userModelList;
    Map<Integer, User> userMap = new HashMap<Integer, User>();
    private MapController mapController;
    private AptiMobMainController aptiMobMainController;
    private Pane availContainer;
    private List<Skill> skills;
    private ObservableList categoryCodeList;
    private List<SkillCategory> skillCategories;
    private SkillCategoryModel selectedCategory = new SkillCategoryModel();
    private SkillModel selectedSkill = new SkillModel();
    private String selectedComboSkill;
    private ObservableList<String> skillsForCategory;
    private ObservableList<String> distances;
    boolean initialSkillselection = true;
    private MapController.LonLat location;
    private int radius;
    private ChangeListener userTableListener;
    private int selectedUserId;
    private ShowUserDetailsController showUserDetailsController;
    private Comparator<User> lastNameComparator;
    private ChangeListener userTableSingleListener;
    private ListChangeListener<UserModel> userTableListChangeListener;
    private ObservableList<UserModel> selectedUsers;
    private List<Integer> selectedUserIds;
    private FXMLLoader newMessageLoader;
    private NewMessageController newMessageController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.bundle = rb;

        userTableView.setEditable(false);

        try {
            restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
        } catch (MalformedURLException e) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, e);
        }

        Callback<TableColumn<UserModel, Boolean>, TableCell<UserModel, Boolean>> booleanCellFactory =
                new Callback<TableColumn<UserModel, Boolean>, TableCell<UserModel, Boolean>>() {
                    @Override
                    public TableCell<UserModel, Boolean> call(TableColumn<UserModel, Boolean> p) {
                        return new BooleanCell();
                    }
                };

        userCol.setCellValueFactory(
                new PropertyValueFactory<UserModel, String>("username"));
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<UserModel, String>("firstName"));
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<UserModel, String>("lastName"));
        availableCol.setCellValueFactory(
                new PropertyValueFactory<UserModel, Boolean>("available"));
        availableCol.setCellFactory(booleanCellFactory);
        cityCol.setCellValueFactory(new PropertyValueFactory<UserModel, String>("city"));
        cellPhoneCol.setCellValueFactory(new PropertyValueFactory<UserModel, String>("cellPhone"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<UserModel, String>("phone"));

        skillCategories = restClient.getSkillCategories();
        skills = restClient.getSkills();

        Comparator<SkillCategory> categoryComparator = new Comparator<SkillCategory>() {
            @Override
            public int compare(SkillCategory o1, SkillCategory o2) {
                return o1.getCode().toUpperCase().compareTo(o2.getCode().toUpperCase());
            }
        };
        if (null != skillCategories && skillCategories.size() > 0) {
            Collections.sort(skillCategories, categoryComparator);
        }

        Comparator<Skill> skillComparator = new Comparator<Skill>() {
            @Override
            public int compare(Skill o1, Skill o2) {
                return o1.getCode().toUpperCase().compareTo(o2.getCode().toUpperCase());
            }
        };

        if (null != skills && skills.size() > 0) {
            Collections.sort(skills, skillComparator);
        }

        skillComboBox.getItems().removeAll(skillComboBox.getItems());

        categoryCodeList = skillCategoryComboBox.getItems();
        categoryCodeList.clear();

        if (null != skillCategories && skillCategories.size() > 0) {
            for (SkillCategory c : skillCategories) {
                categoryCodeList.add(c.getCode());
            }
        }

        skillCategoryComboBox.setItems(categoryCodeList);
        skillCategoryComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                for (SkillCategory c : skillCategories) {
                    if (c.getCode().equals(t1)) {
                        selectedCategory.setId(c.getId());
                        selectedCategory.setCode(c.getCode());
                        selectedCategory.setDescription(c.getDescription());
                        //userChanged = true;
                        skillsForCategory = FXCollections.observableArrayList();
                        for (Skill s : skills) {
                            if (s.getCategory() == selectedCategory.getId()) {
                                skillsForCategory.add(s.getCode());
                            }
                        }
                        skillComboBox.setDisable(false);
                        setSkillCombo();
                    }
                }
            }
        });

        distances = FXCollections.observableArrayList("5", "10", "15", "20", "25", "30", "40", "50", "60", "70", "80", "90", "100", "150", "200");
        distanceComboBox.setItems(distances);

        distanceComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String oldValue, String newValue) {
                radius = Integer.valueOf(newValue);
                applyCriteria();
            }
        });

        ObservableList choiceItems = FXCollections.observableArrayList(bundle.getString("AvailableUsers"), bundle.getString("AllUsers"));
        allUsersChoice.setItems(choiceItems);
        allUsersChoice.setValue(choiceItems.get(0));
        allUsersChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                //userTableView.getSelectionModel().selectedItemProperty().removeListener(userTableListener);              
                userTableView.getSelectionModel().getSelectedItems().removeListener(userTableListChangeListener);
                switch (t1.intValue()) {
                    case 0:
                        userList = restClient.getAvailableUsers();
                        Collections.sort(userList, lastNameComparator);
                        availableCol.setVisible(false);
                        if (selectedSkill.getId() > 0) {
                            applyCriteria();
                        } else {
                            refreshModel();
                        }
                        break;
                    case 1:
                        availableCol.setVisible(true);
                        userList = restClient.getUsers();
                        Collections.sort(userList, lastNameComparator);
                        if (selectedSkill.getId() > 0) {
                            applyCriteria();
                        } else {
                            refreshModel();
                        }
                }
                //userTableView.getSelectionModel().selectedItemProperty().addListener(userTableListener);
                userTableView.getSelectionModel().getSelectedItems().addListener(userTableListChangeListener);
            }
        });

        userList = restClient.getAvailableUsers();
        lastNameComparator = new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getLastName().toUpperCase().compareTo(o2.getLastName().toUpperCase());
            }
        };

        //userModelList = FXCollections.observableArrayList();

        userModelList = userTableView.getItems();

        if (null != userList && userList.size() > 0) {
            Collections.sort(userList, lastNameComparator);
            for (User u : userList) {
                userModelList.add(new UserModel(u.getId(), u.getUsername(), u.getDomain(), u.getFirstName(), u.getLastName(), u.isAvailable(), u.getCity(), u.getCellPhone(), u.getPhone()));
                userMap.put(u.getId(), u);
            }
        }

        if (mapController != null) {
            mapController.setUsers(userList);
            mapController.setSkills(skills);
        }

        selectedUsers = userTableView.getSelectionModel().getSelectedItems();

//        userTableSingleListener = new ChangeListener() {
//            @Override
//            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
//                UserModel selectedUserModel = (UserModel) newValue;
//                //System.out.println(selectedUserModel.username.get() + " selected.");
//                selectedUserId = selectedUserModel.getId();
//                mapController.setSelectedUserId(selectedUserId);
//                // System.out.println("selectedUserId = " + Integer.toString(selectedUserModel.getId()));
//                mapController.drawUsers();
//                openButton.setDisable(false);
//            }
//        };

        userTableListChangeListener = new ListChangeListener<UserModel>() {
            @Override
            public void onChanged(Change<? extends UserModel> change) {
                System.out.println(change.getList().size());
                if (change.getList().size() > 0) {
                    selectedUserIds = new ArrayList<Integer>();
                    for (UserModel um : change.getList()) {
                        selectedUserIds.add(um.getId());
                    }
                    mapController.setSelectedUserIds(selectedUserIds);
                    mapController.drawUsers();
                    openButton.setDisable(false);
                    messageButton.setDisable(false);
                } else {
                    openButton.setDisable(true);
                    messageButton.setDisable(true);
                }
            }
        };
        userTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //userTableView.getSelectionModel().selectedItemProperty().addListener(userTableListener);
        userTableView.getSelectionModel().getSelectedItems().addListener(userTableListChangeListener);
    }

    private void refreshModel() {
        userModelList.clear();
        userMap.clear();
        for (User u : userList) {
            userModelList.add(new UserModel(u.getId(), u.getUsername(), u.getDomain(), u.getFirstName(), u.getLastName(), u.isAvailable(), u.getCity(), u.getCellPhone(), u.getPhone()));
            userMap.put(u.getId(), u);
        }
        mapController.setUsers(userList);
    }

    private void setSkillCombo() {

//        userGridPane.getChildren().remove(skillComboBox);
//        skillComboBox = new ComboBox();
//        userGridPane.add(skillComboBox, 3, 2);

        skillComboBox.getItems().removeAll(skillComboBox.getItems());
        skillComboBox.setItems(skillsForCategory);

        if (!initialSkillselection) {
            if (skillComboBox.getItems().size() > 0) {
                skillComboBox.setValue(skillsForCategory.get(0));
            }
        }
        initialSkillselection = false;

        skillComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                selectedComboSkill = t1;
                if (selectedComboSkill != null && !selectedComboSkill.equals("")) {
                    selectedSkill.setCode(selectedComboSkill);
                    for (Skill s : skills) {
                        if (s.getCode().equals(selectedComboSkill)) {
                            selectedSkill.setId(s.getId());
                            break;
                        }
                    }
                    applyCriteria();
                }
            }
        });
    }

    private void applyCriteria() {
        if (null != selectedSkill) {
            //userTableView.getSelectionModel().selectedItemProperty().removeListener(userTableListener);
            userTableView.getSelectionModel().getSelectedItems().removeListener(userTableListChangeListener);
            List<User> filteredUserList = new ArrayList<>();
            for (User u : userList) {
                if (null != u.getSkills()) {
                    for (int skillId : u.getSkills()) {
                        if (skillId == selectedSkill.getId()) {
                            if (location != null && radius > 0) {
                                System.out.println("radius: " + Integer.toString(radius));
                                if (gps2km(location.lon, location.lat, u.getLon(), u.getLat()) <= radius) {
                                    filteredUserList.add(u);
                                }
                            } else {
                                // no loc nor radius selected, add what we've got
                                filteredUserList.add(u);
                            }
                            break;
                        }
                    }
                }
            }
            userModelList.clear();
            userMap.clear();
            for (User u : filteredUserList) {
                userModelList.add(new UserModel(u.getId(), u.getUsername(), u.getDomain(), u.getFirstName(), u.getLastName(), u.isAvailable(), u.getCity(), u.getCellPhone(), u.getPhone()));
                userMap.put(u.getId(), u);
            }
            mapController.setUsers(filteredUserList);
            setLocationButton.setDisable(false);
            //userTableView.getSelectionModel().selectedItemProperty().addListener(userTableListener);
            userTableView.getSelectionModel().getSelectedItems().addListener(userTableListChangeListener);
        }
    }

    public void setMapController(MapController controller) {
        this.mapController = controller;
    }

    public void setMainController(AptiMobMainController controller) {
        this.aptiMobMainController = controller;
    }

    public void setContainer(Pane container) {
        this.availContainer = container;
        availToolBar.
                prefWidthProperty().
                bind(availContainer.widthProperty());
        availabilityScrollPane.prefWidthProperty().
                bind(availContainer.widthProperty());
        availabilityScrollPane.prefHeightProperty().
                bind(availContainer.heightProperty().subtract(30d));
    }

    @FXML
    private void handleSetLocationButton(ActionEvent event) {
        mapController.setText(bundle.getString("SelectLocation"));
        setLocationButton.setDisable(true);
        mapController.myBrowser.setLocation();
    }

    public void setLocation(MapController.LonLat location) {
        this.location = location;
        applyCriteria();
    }

    private double gps2km(double lng_a, double lat_a, double lng_b, double lat_b) {
        double pk = (double) (180 / 3.14169);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366 * tt;
    }
//
//    @FXML
//    private void handleOpenButton(ActionEvent event) {
//        if (selectedUserId > 0) {
//            try {
//                Stage userStage = new Stage();
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("ShowUserDetails.fxml"), ResourceBundle.getBundle("bundles.strings"));
//                userStage.setScene(new Scene((Parent) loader.load()));
//                userStage.show();
//                User user = null;
//                for (User u : userList) {
//                    if (u.getId() == selectedUserId) {
//                        user = u;
//                        break;
//                    }
//                }
//                showUserDetailsController = loader.getController();
//                showUserDetailsController.setCountryMap(mapController.getCountryMap());
//                showUserDetailsController.setUser(user, skills, skillCategories);
//            } catch (IOException ex) {
//                Logger.getLogger(AvailabilityTableController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }

    @FXML
    private void handleOpenButton(ActionEvent event) {
        for (int selectedUserId : selectedUserIds) {
            try {
                Stage userStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ShowUserDetails.fxml"), ResourceBundle.getBundle("bundles.strings"));
                userStage.setScene(new Scene((Parent) loader.load()));
                userStage.show();
                User user = null;
                for (User u : userList) {
                    if (u.getId() == selectedUserId) {
                        user = u;
                        break;
                    }
                }
                showUserDetailsController = loader.getController();
                showUserDetailsController.setCountryMap(mapController.getCountryMap());
                showUserDetailsController.setUser(user, skills, skillCategories);
            } catch (IOException ex) {
                Logger.getLogger(AvailabilityTableController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void handleMessageButton(ActionEvent event) throws IOException {
        Stage newMessageStage = new Stage();
        newMessageLoader = new FXMLLoader(getClass().getResource("NewMessage.fxml"), ResourceBundle.getBundle("bundles.strings"));
        newMessageStage.setScene(new Scene((Parent) newMessageLoader.load()));
        newMessageStage.setTitle(bundle.getString("ApplicationName") + " - " + bundle.getString("Message"));
        newMessageStage.show();
        newMessageController = newMessageLoader.getController();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setNewMessageRecipients();
            }
        });
    }

    private void setNewMessageRecipients() {
        List<User> users = new ArrayList();
        for (int id : selectedUserIds) {
            for (User u : userList) {
                if (u.getId() == id) {
                    users.add(u);
                }
            }
        }
        newMessageController.setUsers(users);
    }

    @FXML
    private void handleRefreshButton(ActionEvent event) {
        try {
            aptiMobMainController.reloadMap();
        } catch (IOException ex) {
            Logger.getLogger(AvailabilityTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
