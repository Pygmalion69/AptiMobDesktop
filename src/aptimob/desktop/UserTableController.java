/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.models.UserModel;
import aptimob.desktop.entities.User;
import aptimob.desktop.controls.BooleanCell;
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
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

/**
 *
 * @author helfrich
 */
public class UserTableController implements Initializable {

    @FXML
    private AnchorPane rootWindow;
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
    private ResourceBundle bundle;
    RestClient restClient;
    List<User> userList = new ArrayList<>();
    ObservableList<UserModel> userModelList;
    Map<Integer, User> userMap = new HashMap<Integer, User>();
    private UserDetailController userDetailController;
    private AptiMobMainController aptiMobMainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.bundle = rb;

        userTableView.setEditable(false);

//        userTableView.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
//            @Override
//            public void handle(ScrollEvent t) {
//                System.out.println("Scrolling");
//            }
//        });

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

        userList = restClient.getUsers();
        Comparator<User> lastNameComparator = new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getLastName().toUpperCase().compareTo(o2.getLastName().toUpperCase());
            }
        };
        Collections.sort(userList, lastNameComparator);

        //userModelList = FXCollections.observableArrayList();

        userModelList = userTableView.getItems();

        for (User u : userList) {
            userModelList.add(new UserModel(u.getId(), u.getUsername(), u.getDomain(), u.getFirstName(), u.getLastName(), u.isAvailable(), u.getCity()));
            userMap.put(u.getId(), u);
        }

        userTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                userDetailController.checkChanges();
                UserModel selectedUserModel = (UserModel) newValue;
                //System.out.println(selectedUserModel.username.get() + " selected.");
                userDetailController.setViewMode();
                userDetailController.setUser(userMap.get(selectedUserModel.getId()));
            }
        });

        rootWindow.widthProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable,
                    Object oldValue, Object newValue) {
                Double width = (Double) newValue;
                userTableView.setPrefWidth(width);
            }
        });

        rootWindow.heightProperty().addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable,
                            Object oldValue, Object newValue) {
                        Double height = (Double) newValue;
                        userTableView.setPrefHeight(height);
                    }
                });
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (null != aptiMobMainController) {
                    aptiMobMainController.setCursor(Cursor.DEFAULT);
                }
            }
        });
    }

    public void setMainController(AptiMobMainController controller) {
        this.aptiMobMainController = controller;
    }

    public void loadUsers() {
        userList = restClient.getUsers();
        userModelList.clear();
        userMap.clear();
        for (User u : userList) {
            userModelList.add(new UserModel(u.getId(), u.getUsername(), u.getDomain(), u.getFirstName(), u.getLastName(), u.isAvailable(), u.getCity()));
            userMap.put(u.getId(), u);
        }
    }

    public void refresh(User u) {
        if (u.getId() == 0) {
            userList.add(u);
            u.setId(restClient.getUserId(u.getUsername()));
            userModelList.add(new UserModel(u.getId(), u.getUsername(), u.getDomain(), u.getFirstName(), u.getLastName(), u.isAvailable(), u.getCity()));
            userMap.put(u.getId(), u);
        } else {
            for (int i = 0; i < userList.size(); i++) {
                User listedUser = userList.get(i);
                if (listedUser.getId() == u.getId()) {
                    userList.set(i, u);
                }
            }
//            for (User listedUser : userList) {
//                if (listedUser.getId() == u.getId()) {
//                    listedUser.setUsername(u.getUsername());
//                    listedUser.setPassword(u.getPassword());
//                    listedUser.setFirstName(u.getFirstName());
//                    listedUser.setLastName(u.getLastName());
//                    listedUser.setAvailable(u.isAvailable());
//                    listedUser.setAddress(u.getAddress());
//                    listedUser.setPostalCode(u.getPostalCode());
//                    listedUser.setCity(u.getCity());
//                    listedUser.setCountry(u.getCountry());
//                    listedUser.setCellPhone(u.getCellPhone());
//                    listedUser.setPhone(u.getPhone());
//                    listedUser.setTaxCode(u.getTaxCode());
//                }
//            }
            for (UserModel um : userModelList) {
                if (um.getId() == u.getId()) {
                    um.setUsername(u.getUsername());
                    um.setFirstName(u.getFirstName());
                    um.setLastName(u.getLastName());
                    um.setAvailable(u.isAvailable());
                    um.setCity(u.getCity());
                }
            }
        }
    }

    public void setUserDetailController(UserDetailController controller) {
        this.userDetailController = controller;
    }
}
