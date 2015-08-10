/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.entities.Country;
import aptimob.desktop.entities.Domain;
import aptimob.desktop.entities.Group;
import aptimob.desktop.entities.Skill;
import aptimob.desktop.entities.SkillCategory;
import aptimob.desktop.entities.User;
import aptimob.desktop.models.CountryModel;
import aptimob.desktop.models.DomainModel;
import aptimob.desktop.models.GroupModel;
import aptimob.desktop.models.SkillCategoryModel;
import aptimob.desktop.models.UserModel;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfx.messagebox.MessageBox;

/**
 *
 * @author helfrich
 */
public class UserDetailController implements Initializable {

    @FXML
    AnchorPane userDetailBasePane;
    @FXML
    ScrollPane userDetailScrollPane;
    @FXML
    ToolBar detailToolBar;
    @FXML
    Button editUserButton;
    @FXML
    Button saveUserButton;
    @FXML
    Button deleteUserButton;
    @FXML
    Button newUserButton;
    @FXML
    GridPane userGridPane;
    @FXML
    TextField userField;
    @FXML
    TextField firstNameField;
    @FXML
    TextField lastNameField;
    @FXML
    TextField addressField;
    @FXML
    TextField postalCodeField;
    @FXML
    TextField cityField;
    @FXML
    ComboBox countryComboBox;
    @FXML
    TextField cellPhoneField;
    @FXML
    TextField phoneField;
    @FXML
    TextField taxCodeField;
    @FXML
    CheckBox availCheckBox;
    @FXML
    ComboBox skillCategoryComboBox;
    @FXML
    ComboBox skillComboBox;
    @FXML
    ListView<String> groupListView;
    @FXML
    ListView<String> skillListView;
    @FXML
    ComboBox groupComboBox;
    @FXML
    Button addGroupButton;
    @FXML
    Button addSkillButton;
    @FXML
    Button removeGroupButton;
    @FXML
    Button removeSkillButton;
    @FXML
    PasswordField passwordField;
    @FXML
    ComboBox domainComboBox;
    private ResourceBundle rb;
    private RestClient restClient;
    private UserModel userModel = new UserModel();
    private List<Country> countries;
    private ObservableList<String> countryNameList;
    private ObservableList<String> domainNameList;
    private CountryModel selectedCountry = new CountryModel();
    private SkillCategoryModel selectedCategory = new SkillCategoryModel();
    private DomainModel selectedDomain = new DomainModel();
    Pane detailContainer;
    private List<Group> groups;
    private Preferences prefs;
    private int userId;
    private List<Integer> currentEndUserGroupIds;
    private List<String> currentEndUserGroups;
    private boolean userChanged;
    private boolean admin;
    private boolean root;
    private User user;
    private UserTableController userTableController;
    private AptiMobMainController aptiMobMainController;
    private ChangeListener<String> stringChangeListener;
    private ChangeListener<Boolean> booleanChangeListener;
    private List<Group> groupsAll;
    private List<Group> groupsAssigned;
    private List<Group> groupsAvailable;
    private ObservableList<String> groupNamesAvailable;
    private ObservableList<String> groupNamesAssigned;
    private String selectedComboGroup;
    private String selectedListGroup;
    private List<Domain> domains;
    private ChangeListener<String> countryListener;
    private ChangeListener<String> domainListener;
    private User currentEndUser;
    private int currentEndUserDomain;
    private List<Skill> skillsAll;
    private List<Skill> skillsForCategory;
    private List<SkillCategory> skillCategories;
    private ObservableList categoryCodeList;
    private ChangeListener<String> categoryListener;
    private List<Skill> skillsAssigned;
    private List<Skill> skillsAvailable;
    private ObservableList<String> skillCodesAvailable;
    private ObservableList<String> skillCodesAssigned;
    private String selectedComboSkill;
    private String selectedListSkill;
    private boolean newUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        try {
            restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
        } catch (MalformedURLException ex) {
            Logger.getLogger(UserDetailController.class.getName()).log(Level.SEVERE, null, ex);
        }
        prefs = Preferences.userNodeForPackage(getClass());
        userId = prefs.getInt("user_id", 0);
        currentEndUser = restClient.getUserById(userId);
        currentEndUserDomain = currentEndUser.getDomain();
        currentEndUserGroups = restClient.getUserGroups(userId);

        for (String group : currentEndUserGroups) {
            if (group.equals("admin")) {
                admin = true;
            }
            if (group.equals("root")) {
                root = true;
            }
        }
        //groups = restClient.getGroups();
        if (root) {
            domains = restClient.getDomains();
        }

        groupsAll = restClient.getGroups();


        skillCategories = restClient.getSkillCategories();
        skillsAll = restClient.getSkills();

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

        if (null != skillsAll && skillsAll.size() > 0) {
            Collections.sort(skillsAll, skillComparator);
        }



        categoryCodeList = skillCategoryComboBox.getItems();
        categoryCodeList.clear();

        if (null != skillCategories && skillCategories.size() > 0) {
            for (SkillCategory c : skillCategories) {
                categoryCodeList.add(c.getCode());
            }
        }

        skillCategoryComboBox.setItems(categoryCodeList);

        countries = restClient.getCountries();
        countryNameList = countryComboBox.getItems();
        countryNameList.clear();

        if (null != countries && countries.size() > 0) {
            for (Country c : countries) {
                countryNameList.add(c.getName());
            }
        }

        countryComboBox.setItems(countryNameList);

        userGridPane.setDisable(true);

        if (root || admin) {
            Bindings.bindBidirectional(userField.textProperty(), userModel.usernameProperty());
            Bindings.bindBidirectional(passwordField.textProperty(), userModel.passwordProperty());
            Bindings.bindBidirectional(firstNameField.textProperty(), userModel.firstNameProperty());
            Bindings.bindBidirectional(lastNameField.textProperty(), userModel.lastNameProperty());
            Bindings.bindBidirectional(addressField.textProperty(), userModel.addressProperty());
            Bindings.bindBidirectional(postalCodeField.textProperty(), userModel.postalCodeProperty());
            Bindings.bindBidirectional(cityField.textProperty(), userModel.cityProperty());
            Bindings.bindBidirectional(cellPhoneField.textProperty(), userModel.cellPhoneProperty());
            Bindings.bindBidirectional(phoneField.textProperty(), userModel.phoneProperty());
            Bindings.bindBidirectional(taxCodeField.textProperty(), userModel.taxCodeProperty());
            Bindings.bindBidirectional(availCheckBox.selectedProperty(), userModel.availableProperty());

            Bindings.bindBidirectional(selectedCountry.idProperty(), userModel.countryProperty());

            if (root) {
                Bindings.bindBidirectional(selectedDomain.idProperty(), userModel.domainProperty());
            }

            //editUserButton.setDisable(false);
            //deleteUserButton.setDisable(false);
            newUserButton.setDisable(false);

            stringChangeListener = new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    userChanged = true;
                }
            };
            booleanChangeListener = new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                    userChanged = true;
                }
            };
            countryListener =
                    new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue ov, String t, String t1) {
                            for (Country c : countries) {
                                if (c.getName().equals(t1)) {
                                    selectedCountry.setId(c.getId());
                                    selectedCountry.setCode(c.getCode());
                                    selectedCountry.setName(c.getName());
                                    userChanged = true;
                                }
                            }
                        }
                    };
            categoryListener =
                    new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue ov, String t, String t1) {
                            for (SkillCategory c : skillCategories) {
                                if (c.getCode().equals(t1)) {
                                    selectedCategory.setId(c.getId());
                                    selectedCategory.setCode(c.getCode());
                                    selectedCategory.setDescription(c.getDescription());
                                    //userChanged = true;
                                    skillsForCategory = new ArrayList<>();
                                    for (Skill s : skillsAll) {
                                        if (s.getCategory() == selectedCategory.getId()) {
                                            skillsForCategory.add(s);
                                        }
                                    }
                                    setSkillsForUser();
                                    setSkillCombo();
                                }
                            }
                        }
                    };
            if (root) {
                domainListener = new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                        for (Domain d : domains) {
                            if (d.getName().equals(t1)) {
                                selectedDomain.setId(d.getId());
                                selectedDomain.setName(d.getName());
                                userChanged = true;
                            }
                        }
                    }
                };
            }
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                userField.requestFocus();
            }
        });
    }

    public void setContainer(Pane container) {
        this.detailContainer = container;
        detailToolBar.
                prefWidthProperty().
                bind(detailContainer.widthProperty());
        userDetailBasePane.
                prefHeightProperty().
                bind(detailContainer.heightProperty());
//        scrollableDetailPane.prefWidthProperty().
//                bind(detailContainer.widthProperty());
        userDetailScrollPane.prefWidthProperty().
                bind(detailContainer.widthProperty());
        userDetailScrollPane.prefHeightProperty().
                bind(detailContainer.heightProperty().subtract(30d));
    }

    public void setUser(User user) {
        //userModel = new UserModel(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.isAvailable(), user.getAddress(), user.getPostalCode(), user.getCity(), user.getCountry(), user.getCellPhone(), user.getPhone(), user.getTaxCode());
        this.user = user;
        userChanged = false;
        userModel.setId(user.getId());
        userModel.setUsername(user.getUsername());
        if (user.getDomain() > 0) {
            userModel.setDomain(user.getDomain());
        } else {
            user.setDomain(currentEndUserDomain);
            userModel.setDomain(currentEndUserDomain);
        }
        userModel.setFirstName(user.getFirstName());
        userModel.setLastName(user.getLastName());
        userModel.setAvailable(user.isAvailable());
        userModel.setAddress(user.getAddress());
        userModel.setPostalCode(user.getPostalCode());
        userModel.setCity(user.getCity());
        userModel.setCountry(user.getCountry());
        userModel.setCellPhone(user.getCellPhone());
        userModel.setPhone(user.getPhone());
        userModel.setTaxCode(user.getTaxCode());

        for (Country c : countries) {
            if (c.getId() == user.getCountry()) {
                countryComboBox.setValue(c.getName());
                selectedCountry.setId(c.getId());
                selectedCountry.setCode(c.getCode());
                selectedCountry.setName(c.getName());
            }
        }

        groupsAssigned = new ArrayList<Group>();
        groupsAvailable = new ArrayList<Group>();
        groupNamesAvailable = groupComboBox.getItems();
        groupNamesAvailable.clear();
        groupNamesAssigned = groupListView.getItems();
        groupNamesAssigned.clear();
        if (user.getId() != 0) {
            //List<Integer> idsAssigned = restClient.getUserGroupIds(user.getId());
            List<Integer> idsAssigned = new ArrayList<>();
            int[] userGroupIds = user.getGroups();
            if (userGroupIds != null && userGroupIds.length > 0) {
                for (int id : userGroupIds) {
                    idsAssigned.add(id);
                }
            }
            if (idsAssigned != null) {
                for (int groupId : idsAssigned) {
                    for (Group group : groupsAll) {
                        if (groupId == group.getId()) {
                            groupsAssigned.add(group);
                            groupNamesAssigned.add(group.getGroup());
                        }
                    }
                }
            }
        }
        for (Group group : groupsAll) {
            boolean assigned = false;
            for (Group aGroup : groupsAssigned) {
                if (aGroup.getId() == group.getId()) {
                    assigned = true;
                }
            }
            if (!assigned) {
                groupsAvailable.add(group);
                groupNamesAvailable.add(group.getGroup());
            }
        }

        setGroupCombo();

        groupListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                selectedListGroup = t1;
                if (selectedListGroup != null && !selectedListGroup.equals("")) {
                    removeGroupButton.setDisable(false);
                }
            }
        });

        setSkillsForUser();
        if (null != skillCodesAssigned) {
            skillListView.setItems(skillCodesAssigned);
        }
        if (null != skillsForCategory) {

            setSkillCombo();
        }

        skillListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                selectedListSkill = t1;
                if (selectedListSkill != null && !selectedListSkill.equals("")) {
                    removeSkillButton.setDisable(false);
                }
            }
        });

        if (root) {
            domainComboBox.setVisible(true);

            ObservableList<Domain> domainList = domainComboBox.getItems();
            for (Iterator<Domain> it = domains.iterator(); it.hasNext();) {
                Domain d = it.next();
                domainList.add(new Domain(d.getId(), d.getName()));
            }

            domainNameList = domainComboBox.getItems();
            domainNameList.clear();

            for (Domain d : domains) {
                domainNameList.add(d.getName());
                if (d.getId() == user.getDomain()) {
                    domainComboBox.setValue(d.getName());
                    selectedDomain.setId(d.getId());
                    selectedDomain.setName(d.getName());
                }
            }
        }
        if (admin || root) {
            editUserButton.setDisable(false);
            deleteUserButton.setDisable(false);
        }
    }

    private void setSkillsForUser() {
        skillsAssigned = new ArrayList<Skill>();
        skillsAvailable = new ArrayList<Skill>();
        skillCodesAvailable = skillComboBox.getItems();
        skillCodesAvailable.clear();
        skillCodesAssigned = skillListView.getItems();
        skillCodesAssigned.clear();
        if (user.getId() != 0) {
            //List<Integer> idsAssigned = restClient.getUserGroupIds(user.getId());
            List<Integer> idsAssigned = new ArrayList<>();
            int[] userSkillIds = user.getSkills();
            if (userSkillIds != null && userSkillIds.length > 0) {
                for (int id : userSkillIds) {
                    idsAssigned.add(id);
                }
            }
            if (idsAssigned != null) {
                for (int skillId : idsAssigned) {
                    for (Skill skill : skillsAll) {
                        if (skillId == skill.getId()) {
                            skillsAssigned.add(skill);
                            skillCodesAssigned.add(skill.getCode());
                        }
                    }
                }
            }
        }
        if (null != skillsForCategory) {
            for (Skill skill : skillsForCategory) {
                boolean assigned = false;
                for (Skill aSkill : skillsAssigned) {
                    if (aSkill.getId() == skill.getId()) {
                        assigned = true;
                    }
                }
                if (!assigned) {
                    skillsAvailable.add(skill);
                    skillCodesAvailable.add(skill.getCode());
                }
            }
        }
    }

    private void setGroupCombo() {

        userGridPane.getChildren().remove(groupComboBox);
        groupComboBox = new ComboBox();
        userGridPane.add(groupComboBox, 3, 6);

        groupComboBox.setItems(groupNamesAvailable);
        groupListView.setItems(groupNamesAssigned);


        if (groupNamesAvailable.size() > 0) {
            groupComboBox.setDisable(false);
        }

        groupComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                selectedComboGroup = t1;
                if (selectedComboGroup != null && !selectedComboGroup.equals("")) {
                    addGroupButton.setDisable(false);
                }
            }
        });

    }

    private void setSkillCombo() {

        userGridPane.getChildren().remove(skillComboBox);
        skillComboBox = new ComboBox();
        userGridPane.add(skillComboBox, 3, 2);

        skillComboBox.setItems(skillCodesAvailable);
        skillListView.setItems(skillCodesAssigned);


        if (skillCodesAvailable.size() > 0) {
            skillComboBox.setDisable(false);
        }

        skillComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                selectedComboSkill = t1;
                if (selectedComboSkill != null && !selectedComboSkill.equals("")) {
                    addSkillButton.setDisable(false);
                }
            }
        });

    }

    public void setUserField(String txt) {
        userField.setText(txt);
    }

    public void handleEditUserButton(ActionEvent event) {
        userGridPane.setDisable(false);
        saveUserButton.setDisable(false);
        editUserButton.setDisable(true);
        setChangeListeners();

    }

    public void setChangeListeners() {
        userModel.usernameProperty().addListener(stringChangeListener);
        userModel.firstNameProperty().addListener(stringChangeListener);
        userModel.lastNameProperty().addListener(stringChangeListener);
        userModel.addressProperty().addListener(stringChangeListener);
        userModel.postalCodeProperty().addListener(stringChangeListener);
        userModel.cityProperty().addListener(stringChangeListener);
        userModel.cellPhoneProperty().addListener(stringChangeListener);
        userModel.phoneProperty().addListener(stringChangeListener);
        userModel.taxCodeProperty().addListener(stringChangeListener);
        userModel.availableProperty().addListener(booleanChangeListener);

        countryComboBox.valueProperty().addListener(countryListener);
        skillCategoryComboBox.valueProperty().addListener(categoryListener);
        if (root) {

            domainComboBox.valueProperty().addListener(domainListener);
        }

    }

    public void removeChangeListeners() {
        userModel.usernameProperty().removeListener(stringChangeListener);
        userModel.firstNameProperty().removeListener(stringChangeListener);
        userModel.lastNameProperty().removeListener(stringChangeListener);
        userModel.addressProperty().removeListener(stringChangeListener);
        userModel.postalCodeProperty().removeListener(stringChangeListener);
        userModel.cityProperty().removeListener(stringChangeListener);
        userModel.cellPhoneProperty().removeListener(stringChangeListener);
        userModel.phoneProperty().removeListener(stringChangeListener);
        userModel.taxCodeProperty().removeListener(stringChangeListener);
        userModel.availableProperty().removeListener(booleanChangeListener);
        countryComboBox.valueProperty().removeListener(countryListener);
        skillCategoryComboBox.valueProperty().removeListener(categoryListener);
        if (root) {
            domainComboBox.valueProperty().removeListener(domainListener);
        }
    }

    public void handleSaveUserButton(ActionEvent event) {

        save();
    }

    public void handleDeleteUserButton(ActionEvent event) {
        String question = String.format(rb.getString("QuestionDeleteUser"), user.getUsername());
        if (MessageBox.YES == MessageBox.show((Stage) userDetailBasePane.getScene().getWindow(),
                question,
                "",
                MessageBox.ICON_QUESTION | MessageBox.YES | MessageBox.NO | MessageBox.DEFAULT_BUTTON2)) {
            restClient.deleteUser(user.getId());
            try {
                aptiMobMainController.reloadMaintainUsers();


            } catch (IOException ex) {
                Logger.getLogger(UserDetailController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void handleNewUser() {
        handleNewUserButton(null);
    }

    public void handleNewUserButton(ActionEvent event) {
        userGridPane.setDisable(false);
        newUserButton.setDisable(true);
        editUserButton.setDisable(true);
        saveUserButton.setDisable(false);
        deleteUserButton.setDisable(true);
        setUser(new User());
        setChangeListeners();
        newUser = true;
    }

    public void setViewMode() {
        removeChangeListeners();
        userGridPane.setDisable(true);
        newUserButton.setDisable(false);
        //editUserButton.setDisable(false);
        saveUserButton.setDisable(true);
        //deleteUserButton.setDisable(false);
    }

    private void save() {
        if (null == userModel.getUsername() || userModel.getUsername().equals("")) {
            MessageBox.show((Stage) userDetailBasePane.getScene().getWindow(),
                    rb.getString("UsernameEmpty"),
                    "",
                    MessageBox.ICON_ERROR | MessageBox.OK);
            return;
        }
        if (newUser && (null == userModel.getUsername() || userModel.getUsername().equals(""))) {
            MessageBox.show((Stage) userDetailBasePane.getScene().getWindow(),
                    rb.getString("PasswordEmpty"),
                    "",
                    MessageBox.ICON_ERROR | MessageBox.OK);
            return;
        }
        if (null == groupsAssigned || groupsAssigned.isEmpty()) {
            MessageBox.show((Stage) userDetailBasePane.getScene().getWindow(),
                    rb.getString("GroupsEmpty"),
                    "",
                    MessageBox.ICON_ERROR | MessageBox.OK);
            return;
        }
//        saveUserButton.setDisable(true);
        editUserButton.setDisable(false);
        newUserButton.setDisable(false);
        removeChangeListeners();
        setViewMode();
        user.setUsername(userModel.getUsername());
        user.setDomain(userModel.getDomain());
        user.setPassword(userModel.getPassword());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setAddress(userModel.getAddress());
        user.setPostalCode(userModel.getPostalCode());
        user.setCity(userModel.getCity());
        user.setCountry(userModel.getCountry());
        user.setCellPhone(userModel.getCellPhone());
        user.setPhone(userModel.getPhone());
        user.setTaxCode(userModel.getTaxCode());
        user.setAvailable(userModel.isAvailable());
        userChanged = false;
        restClient.createUpdateUser(user);
        newUser = false;
        List<Integer> groupIdsAssigned = new ArrayList<>();
        for (Group g : groupsAssigned) {
            groupIdsAssigned.add(g.getId());
        }
        user.setGroups(toIntArray(groupIdsAssigned));
        List<Integer> skillIdsAssigned = new ArrayList<>();
        for (Skill s : skillsAssigned) {
            skillIdsAssigned.add(s.getId());
        }
        user.setSkills(toIntArray(skillIdsAssigned));
        userTableController.refresh(user);
        restClient.setGroups(userModel.getUsername(), groupsAssigned);
        restClient.setSkills(userModel.getUsername(), skillsAssigned);
        //userTableController.loadUsers();
    }

    private int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        int i = 0;
        for (Integer e : list) {
            ret[i++] = e.intValue();
        }
        return ret;
    }

    @FXML
    private void handleAddGroupButton(ActionEvent event) {
        //groupComboBox.getSelectionModel().select(0);

        for (Group group : groupsAvailable) {
            if (group.getGroup().equals(selectedComboGroup)) {
                groupsAssigned.add(group);
            }
        }
        groupNamesAssigned.add(selectedComboGroup);

        groupsAvailable.clear();
        groupNamesAvailable.clear();
        for (Group group : groupsAll) {
            boolean assigned = false;
            for (Group aGroup : groupsAssigned) {
                if (aGroup.getId() == group.getId()) {
                    assigned = true;
                }
            }
            if (!assigned) {
                groupsAvailable.add(group);
                groupNamesAvailable.add(group.getGroup());
            }
        }
        if (groupNamesAvailable.size() > 0) {
            //groupComboBox.getSelectionModel().clearSelection();
            setGroupCombo();
            addGroupButton.setDisable(true);
        } else {
            groupComboBox.setDisable(true);
        }
        userChanged = true;
        groupListView.requestFocus();
    }

    @FXML
    private void handleAddSkillButton(ActionEvent event) {
        //groupComboBox.getSelectionModel().select(0);

        for (Skill skill : skillsAvailable) {
            if (skill.getCode().equals(selectedComboSkill)) {
                skillsAssigned.add(skill);
            }
        }
        skillCodesAssigned.add(selectedComboSkill);

        skillsAvailable.clear();
        skillCodesAvailable.clear();
        for (Skill skill : skillsForCategory) {
            boolean assigned = false;
            for (Skill aSkill : skillsAssigned) {
                if (aSkill.getId() == skill.getId()) {
                    assigned = true;
                }
            }
            if (!assigned) {
                skillsAvailable.add(skill);
                skillCodesAvailable.add(skill.getCode());
            }
        }
        if (skillCodesAvailable.size() > 0) {
            //groupComboBox.getSelectionModel().clearSelection();
            setSkillCombo();
            addSkillButton.setDisable(true);
        } else {
            skillComboBox.setDisable(true);
        }
        userChanged = true;
        skillListView.requestFocus();
    }

    @FXML
    private void handleRemoveGroupButton(ActionEvent event) {
        String toRemove = selectedListGroup;
        groupNamesAssigned.clear();
        List<Group> newGroupsAssigned = new ArrayList<>();
        for (Group group : groupsAssigned) {
            if (!group.getGroup().equals(toRemove)) {
                newGroupsAssigned.add(group);
                groupNamesAssigned.add(group.getGroup());
            }
        }
        groupsAssigned = newGroupsAssigned;

        groupsAvailable.clear();
        groupNamesAvailable.clear();
        for (Group group : groupsAll) {
            boolean assigned = false;
            for (Group aGroup : groupsAssigned) {
                if (aGroup.getId() == group.getId()) {
                    assigned = true;
                }
            }
            if (!assigned) {
                groupsAvailable.add(group);
                groupNamesAvailable.add(group.getGroup());
            }
        }
        setGroupCombo();
        removeGroupButton.setDisable(true);
        userChanged = true;
        groupListView.requestFocus();
    }

    @FXML
    private void handleRemoveSkillButton(ActionEvent event) {
        String toRemove = selectedListSkill;
        skillCodesAssigned.clear();
        List<Skill> newSkillsAssigned = new ArrayList<>();
        for (Skill skill : skillsAssigned) {
            if (!skill.getCode().equals(toRemove)) {
                newSkillsAssigned.add(skill);
                skillCodesAssigned.add(skill.getCode());
            }
        }
        skillsAssigned = newSkillsAssigned;

        skillsAvailable.clear();
        skillCodesAvailable.clear();
        if (null != skillsForCategory) {
            for (Skill skill : skillsForCategory) {
                boolean assigned = false;
                for (Skill aSkill : skillsAssigned) {
                    if (aSkill.getId() == skill.getId()) {
                        assigned = true;
                    }
                }
                if (!assigned) {
                    skillsAvailable.add(skill);
                    skillCodesAvailable.add(skill.getCode());
                }
            }
            setSkillCombo();
        }
        removeSkillButton.setDisable(true);
        userChanged = true;
        skillListView.requestFocus();
    }

    @FXML
    private void handleRefreshButton(ActionEvent event) {
        try {
            checkChanges();
            aptiMobMainController.reloadUsers();
        } catch (IOException ex) {
            Logger.getLogger(AvailabilityTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setUserTableController(UserTableController controller) {
        this.userTableController = controller;
    }

    public void setMainController(AptiMobMainController controller) {
        this.aptiMobMainController = controller;
    }

    public void checkChanges() {
        if (userChanged) {
            if (null != userDetailBasePane) {
                if (MessageBox.YES == MessageBox.show((Stage) userDetailBasePane.getScene().getWindow(),
                        rb.getString("QuestionSaveChanges"),
                        "",
                        MessageBox.ICON_QUESTION | MessageBox.YES | MessageBox.NO | MessageBox.DEFAULT_BUTTON2)) {
                    save();
                }
                userChanged = false;
            }
            removeChangeListeners();
        }
    }

    /**
     * @return the userChanged
     */
    public boolean isUserChanged() {
        return userChanged;
    }

    /**
     * @param userChanged the userChanged to set
     */
    public void setUserChanged(boolean userChanged) {
        this.userChanged = userChanged;


    }

    static class GroupCell extends ListCell<GroupModel> {

        @Override
        public void updateItem(GroupModel item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getGroup());
            }
        }
    }
}
