/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.entities.Domain;
import aptimob.desktop.entities.Skill;
import aptimob.desktop.entities.SkillCategory;
import aptimob.desktop.entities.User;
import aptimob.desktop.models.DomainModel;
import aptimob.desktop.models.SkillCategoryModel;
import aptimob.desktop.models.SkillModel;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfx.messagebox.MessageBox;

/**
 * FXML Controller class
 *
 * @author helfrich
 */
public class SkillDetailController implements Initializable {

    @FXML
    AnchorPane rootPane;
    @FXML
    GridPane skillGridPane;
    @FXML
    ToolBar detailToolBar;
    @FXML
    Button editButton;
    @FXML
    Button saveButton;
    @FXML
    Button deleteButton;
    @FXML
    Button newButton;
    @FXML
    ComboBox categoryComboBox;
    @FXML
    Button addCategoryButton;
    @FXML
    TextField codeTextField;
    @FXML
    TextField descriptionTextField;
    @FXML
    ComboBox domainComboBox;
    private Pane detailContainer;
    private ResourceBundle rb;
    private RestClient restClient;
    private Preferences prefs;
    private int userId;
    private User currentEndUser;
    private int currentEndUserDomain;
    private List<String> currentEndUserGroups;
    private boolean admin;
    private boolean root;
    private List<SkillCategory> categories;
    private ObservableList<String> categoryCodeList;
    private SkillCategoryModel selectedCategory = new SkillCategoryModel();
    private SkillModel skillModel = new SkillModel();
    private DomainModel selectedDomain = new DomainModel();
    private ChangeListener stringChangeListener;
    private ChangeListener<String> categoryListener;
    private boolean skillChanged;
    private ChangeListener<String> domainListener;
    private List<Domain> domains;
    private Skill skill;
    // private ObservableList<String> domainNameList;
    private AptiMobMainController aptiMobMainController;
    private SkillsController skillsController;

    /**
     * Initializes the controller class.
     */
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

//        if (root) {
//            domains = restClient.getDomains();
//        }

        categories = restClient.getSkillCategories();
        if (categories != null && categories.size() > 0) {
            categoryCodeList = categoryComboBox.getItems();
            categoryCodeList.clear();

            for (SkillCategory c : categories) {
                categoryCodeList.add(c.getCode());
            }

            categoryComboBox.setItems(categoryCodeList);
        } else {
            categoryComboBox.setItems((FXCollections.observableArrayList("")));
        }

        skillGridPane.setDisable(true);

        if (root || admin) {
            Bindings.bindBidirectional(codeTextField.textProperty(), skillModel.codeProperty());
            Bindings.bindBidirectional(descriptionTextField.textProperty(), skillModel.descriptionProperty());
            Bindings.bindBidirectional(selectedCategory.idProperty(), skillModel.categoryProperty());

//                if (root) {
//                    Bindings.bindBidirectional(selectedDomain.idProperty(), skillModel.domainProperty());
//                }

            editButton.setDisable(true);
            deleteButton.setDisable(false);
            newButton.setDisable(false);

            stringChangeListener = new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    skillChanged = true;
                }
            };
            categoryListener =
                    new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue ov, String t, String t1) {
                            for (SkillCategory c : categories) {
                                if (c.getCode().equals(t1)) {
                                    selectedCategory.setId(c.getId());
                                    selectedCategory.setCode(c.getCode());
                                    selectedCategory.setDescription(c.getDescription());
                                    skillChanged = true;
                                }
                            }
                        }
                    };
//                if (root) {
//                    domainListener = new ChangeListener<String>() {
//                        @Override
//                        public void changed(ObservableValue<? extends String> ov, String t, String t1) {
//                            for (Domain d : domains) {
//                                if (d.getName().equals(t1)) {
//                                    selectedDomain.setId(d.getId());
//                                    selectedDomain.setName(d.getName());
//                                    skillChanged = true;
//                                }
//                            }
//                        }
//                    };
//                }

        }
    }

    public void setSkill(Skill skill) {
        //userModel = new UserModel(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.isAvailable(), user.getAddress(), user.getPostalCode(), user.getCity(), user.getCountry(), user.getCellPhone(), user.getPhone(), user.getTaxCode());
        this.skill = skill;
        skillModel.setId(skill.getId());
        skillModel.setCategory(skill.getCategory());
        if (skill.getDomain() > 0) {
            skillModel.setDomain(skill.getDomain());
        } else {
            skill.setDomain(currentEndUserDomain);
            skillModel.setDomain(currentEndUserDomain);
        }
        skillModel.setCode(skill.getCode());
        skillModel.setDescription(skill.getDescription());

        if (categories != null && categories.size() > 0) {
            for (SkillCategory c : categories) {
                if (c.getId() == skill.getCategory()) {
                    categoryComboBox.setValue(c.getCode());
                    selectedCategory.setId(c.getId());
                    selectedCategory.setCode(c.getCode());
                    selectedCategory.setDescription(c.getDescription());
                }
            }
            if (selectedCategory.getId() == 0) {
                //Use previously selected combo setting
                for (SkillCategory c : categories) {
                    if (c.getCode().equals(categoryComboBox.getValue())) {
                        selectedCategory.setId(c.getId());
                        selectedCategory.setCode(c.getCode());
                        selectedCategory.setDescription(c.getDescription());
                        skillChanged = true;
                    }
                }
            }
        }

//        if (root) {
//            domainComboBox.setVisible(true);
//
//
//            ObservableList<Domain> domainList = domainComboBox.getItems();
//            for (Iterator<Domain> it = domains.iterator(); it.hasNext();) {
//                Domain d = it.next();
//                domainList.add(new Domain(d.getId(), d.getName()));
//            }
//
//            domainNameList = domainComboBox.getItems();
//            domainNameList.clear();
//
//            for (Domain d : domains) {
//                domainNameList.add(d.getName());
//                if (d.getId() == skill.getDomain()) {
//                    domainComboBox.setValue(d.getName());
//                    selectedDomain.setId(d.getId());
//                    selectedDomain.setName(d.getName());
//                }
//            }
//        }
    }

    private void setChangeListeners() {
        skillModel.codeProperty().addListener(stringChangeListener);
        skillModel.descriptionProperty().addListener(stringChangeListener);
        categoryComboBox.valueProperty().addListener(categoryListener);
//        if (root) {
//            domainComboBox.valueProperty().addListener(domainListener);
//        }
    }

    public void removeChangeListeners() {
        skillModel.codeProperty().removeListener(stringChangeListener);
        skillModel.descriptionProperty().removeListener(stringChangeListener);
        categoryComboBox.valueProperty().removeListener(categoryListener);
//        if (root) {
//            domainComboBox.valueProperty().removeListener(domainListener);
//        }
    }

    @FXML
    private void handleEditSkillButton(ActionEvent event) {
        skillGridPane.setDisable(false);
        saveButton.setDisable(false);
        editButton.setDisable(true);
        setChangeListeners();
    }

    @FXML
    private void handleSaveSkillButton(ActionEvent event) throws IOException {
        //removeChangeListeners();
        save();
    }

    @FXML
    private void handleDeleteSkillButton(ActionEvent event) {
        String question = String.format(rb.getString("QuestionDeleteSkill"), skill.getCode());
        if (MessageBox.YES == MessageBox.show((Stage) rootPane.getScene().getWindow(),
                question,
                "",
                MessageBox.ICON_QUESTION | MessageBox.YES | MessageBox.NO | MessageBox.DEFAULT_BUTTON2)) {
            restClient.deleteSkill(skill.getId());
            try {
                aptiMobMainController.reloadSkills();


            } catch (IOException ex) {
                Logger.getLogger(UserDetailController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void handleNewSkill() {
        handleNewSkillButton(null);
    }

    @FXML
    private void handleNewSkillButton(ActionEvent event) {
        skillGridPane.setDisable(false);
        newButton.setDisable(true);
        editButton.setDisable(true);
        saveButton.setDisable(false);
        deleteButton.setDisable(true);
        setSkill(new Skill());
        setChangeListeners();
    }

    public void setViewMode() {
        skillGridPane.setDisable(true);
        newButton.setDisable(false);
        editButton.setDisable(false);
        saveButton.setDisable(true);
        deleteButton.setDisable(false);
    }

    private void save() {

        if (null == skillModel.getDescription()) {
            skillModel.setDescription("");
        }

        if (null == skillModel.getCode() || skillModel.getCode().equals("")) {
            MessageBox.show((Stage) rootPane.getScene().getWindow(),
                    rb.getString("CodeEmpty"),
                    "",
                    MessageBox.ICON_ERROR | MessageBox.OK);
            return;
        }
        
        
        
        List<Skill> skills = skillsController.getSkills();
        if (null != skills && skills.size() > 0) {
            for (Skill s : skills) {
                if (s.getId() != skill.getId() && s.getCategory() == skillModel.getCategory() && s.getCode().equals(skillModel.getCode())) {
                    MessageBox.show((Stage) rootPane.getScene().getWindow(),
                            rb.getString("CodeInUse"),
                            "",
                            MessageBox.ICON_ERROR | MessageBox.OK);
                    return;
                }
            }
        }
        
        if (skillModel.getCategory() == 0) {
            MessageBox.show((Stage) rootPane.getScene().getWindow(),
                    rb.getString("CategoryEmpty"),
                    "",
                    MessageBox.ICON_ERROR | MessageBox.OK);
            return;
        }
        
        saveButton.setDisable(true);
        editButton.setDisable(false);
        skill.setCode(skillModel.getCode());
        skill.setDomain(skillModel.getDomain());
        skill.setCategory(skillModel.getCategory());
        skill.setDescription(skillModel.getDescription());

        restClient.createUpdateSkill(skill);
        skillChanged = false;
        removeChangeListeners();
        try {
            aptiMobMainController.reloadSkills();
        } catch (IOException ex) {
            Logger.getLogger(SkillDetailController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setContainer(Pane container) {
        this.detailContainer = container;
        detailToolBar.
                prefWidthProperty().
                bind(detailContainer.widthProperty());
    }

    public void setSkillsController(SkillsController controller) {
        this.skillsController = controller;
    }

    public void setMainController(AptiMobMainController controller) {
        this.aptiMobMainController = controller;
    }

    public void checkChanges() {
        if (skillChanged) {
            if (MessageBox.YES == MessageBox.show((Stage) rootPane.getScene().getWindow(),
                    rb.getString("QuestionSaveChanges"),
                    "",
                    MessageBox.ICON_QUESTION | MessageBox.YES | MessageBox.NO | MessageBox.DEFAULT_BUTTON2)) {
                save();
            } else {
                setSkill(this.skill);
                setViewMode();
            }
            skillChanged = false;
        }
        removeChangeListeners();
    }
}
