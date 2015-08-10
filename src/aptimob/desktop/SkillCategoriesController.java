/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.entities.Domain;
import aptimob.desktop.entities.SkillCategory;
import aptimob.desktop.models.DomainModel;
import aptimob.desktop.models.SkillCategoryModel;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jfx.messagebox.MessageBox;

/**
 *
 * @author helfrich
 */
public class SkillCategoriesController implements Initializable {

    @FXML
    private ListView categoryListView;
    @FXML
    private TextField selectedCodeField;
    @FXML
    private TextField selectedDescriptionField;
    @FXML
    private Button saveButton;
    @FXML
    private AnchorPane rootWindow;
    @FXML
    private Button cancelButton;
    @FXML
    Button addButton;
    @FXML
    Button deleteButton;
    private RestClient restClient;
    private ResourceBundle rb;
    private List<SkillCategory> categoryList;
    private ObservableList<SkillCategoryModel> categoryModelList;
    private int selectedId;
    private int domain;
    private AptiMobMainController aptiMobMainController;
    private boolean categoryChanged;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.rb = rb;

        try {
            restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
        } catch (MalformedURLException e) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, e);
        }

        domain = restClient.getCurrentUserDomain();

        // categoryListView.setCellFactory(new PropertyValueFactory<DomainModel, String>("name"));
        categoryListView.setCellFactory(new Callback<ListView<SkillCategoryModel>, ListCell<SkillCategoryModel>>() {
            @Override
            public ListCell<SkillCategoryModel> call(ListView<SkillCategoryModel> p) {

                return new CategoryCell();
            }
        });
        categoryModelList = categoryListView.getItems();
        categoryList = restClient.getSkillCategories();
        if (categoryList != null) {
            for (SkillCategory c : categoryList) {
                categoryModelList.add(new SkillCategoryModel(c.getId(), c.getDomain(), c.getCode(), c.getDescription()));
            }

            categoryListView.setItems(categoryModelList);
        }
        categoryListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                SkillCategoryModel selectedCategory = (SkillCategoryModel) newValue;
                //System.out.println(selectedUserModel.username.get() + " selected.");
                if (selectedCategory != null) {
                    if (selectedCategory.getCode() != null) {
                        selectedCodeField.setText(selectedCategory.getCode());
                        selectedDescriptionField.setText(selectedCategory.getDescription());
                        selectedId = selectedCategory.getId();
                    }
                    deleteButton.setDisable(false);
                    selectedCodeField.setDisable(false);
                    selectedDescriptionField.setDisable(false);
                    saveButton.setDisable(false);
                    cancelButton.setDisable(false);
                }
            }
        });

    }

    public void handleCancelButton(ActionEvent event) throws IOException {
        Stage categoryStage = (Stage) rootWindow.getScene().getWindow();
        categoryStage.hide();
    }

    public void handleSaveButton(ActionEvent event) throws IOException {
        SkillCategory category = new SkillCategory();
        category.setId(selectedId);
        category.setDomain(domain);
        category.setCode(selectedCodeField.getText());
        category.setDescription(selectedDescriptionField.getText());
        saveButton.setDisable(true);
        selectedCodeField.setDisable(true);
        selectedDescriptionField.setDisable(true);
        restClient.createUpdateSkillCategory(category);
        categoryList = restClient.getSkillCategories();
        categoryModelList.clear();
        for (SkillCategory c : categoryList) {
            categoryModelList.add(new SkillCategoryModel(c.getId(), c.getDomain(), c.getCode(), c.getDescription()));
        }
        ((Stage) rootWindow.getScene().getWindow()).setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
                try {
                    aptiMobMainController.reloadSkills();
                } catch (IOException ex) {
                    Logger.getLogger(SkillCategoriesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void handleAddButton(ActionEvent event) throws IOException {
        categoryListView.getSelectionModel().select(null);
        selectedCodeField.setDisable(false);
        selectedDescriptionField.setDisable(false);
        selectedCodeField.setText("");
        selectedDescriptionField.setText("");
        selectedCodeField.requestFocus();
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        deleteButton.setDisable(true);
        selectedId = 0;
    }

    public void handleDeleteButton(ActionEvent event) throws IOException {
        if (selectedId > 0) {
            if (MessageBox.YES == MessageBox.show(rootWindow.getScene().getWindow(),
                    String.format(rb.getString("QuestionDeleteCategory"), selectedCodeField.getText()), "",
                    MessageBox.ICON_QUESTION | MessageBox.YES | MessageBox.NO | MessageBox.DEFAULT_BUTTON2)) {
                restClient.deleteSkillCategory(selectedId);
                categoryList = restClient.getSkillCategories();
                categoryModelList.clear();
                for (SkillCategory c : categoryList) {
                    categoryModelList.add(new SkillCategoryModel(c.getId(), c.getDomain(), c.getCode(), c.getDescription()));
                }
                ((Stage) rootWindow.getScene().getWindow()).setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent we) {
                        System.out.println("Stage is closing");
                        try {
                            aptiMobMainController.reloadSkills();
                        } catch (IOException ex) {
                            Logger.getLogger(SkillCategoriesController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        }
    }

    public void setMainController(AptiMobMainController controller) {
        this.aptiMobMainController = controller;
    }

    public void checkChanges() throws IOException {
        if (categoryChanged) {
            if (MessageBox.YES == MessageBox.show((Stage) rootWindow.getScene().getWindow(),
                    rb.getString("QuestionSaveChanges"),
                    "",
                    MessageBox.ICON_QUESTION | MessageBox.YES | MessageBox.NO | MessageBox.DEFAULT_BUTTON2)) {
                handleSaveButton(null);
            }
            categoryChanged = false;
        }
        //removeChangeListeners();
    }

    static class CategoryCell extends ListCell<SkillCategoryModel> {

        @Override
        public void updateItem(SkillCategoryModel item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getCode() + " - " + item.getDescription());
            }
        }
    }
}
