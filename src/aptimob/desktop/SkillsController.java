/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.entities.Skill;
import aptimob.desktop.entities.SkillCategory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

/**
 * FXML Controller class
 *
 * @author helfrich
 */
public class SkillsController implements Initializable {

    @FXML
    AnchorPane basePane;
    @FXML
    TreeView skillTreeView;
    private ResourceBundle rb;
    private RestClient restClient;
    private ScrollPane skillsContainer;
    private SkillDetailController skillDetailController;
    private List<SkillCategory> categories;
    private List<Skill> skills;
    private TreeItem<String> skillRootItem;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        try {
            restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
        } catch (MalformedURLException e) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, e);
        }
        categories = restClient.getSkillCategories();
        skills = restClient.getSkills();

        Comparator<SkillCategory> categoryComparator = new Comparator<SkillCategory>() {
            @Override
            public int compare(SkillCategory o1, SkillCategory o2) {
                return o1.getCode().toUpperCase().compareTo(o2.getCode().toUpperCase());
            }
        };
        if (null != categories && categories.size() > 0) {
            Collections.sort(categories, categoryComparator);
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

        if (null != skills && skills.size() > 0) {
            double prefHeight = Font.getDefault().getSize() * (skills.size() + categories.size()) * 1.8;
            basePane.setPrefHeight(prefHeight);
            skillTreeView.setMinHeight(prefHeight);
            System.out.print("prefHeight = " + Double.toString(prefHeight));
        }

        skillRootItem = new TreeItem<String>(rb.getString("FunctionProfile"));
        skillRootItem.setExpanded(true);

        if (categories != null && categories.size() > 0) {
            for (SkillCategory c : categories) {
                TreeItem categoryItem = new TreeItem<String>(c.getCode() + " - " + c.getDescription());
                skillRootItem.getChildren().add(categoryItem);
                if (skills != null && skills.size() > 0) {
                    for (Skill s : skills) {
                        if (s.getCategory() == c.getId()) {
                            TreeItem skillItem = new TreeItem<String>(s.getCode() + " - " + s.getDescription());
                            categoryItem.getChildren().add(skillItem);
                        }
                    }
                }
            }
        }
        skillTreeView.setRoot(skillRootItem);

        skillTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> t, TreeItem<String> t1) {
                if (null != t1 && null != t1.getValue() && !t1.getValue().equals("")) {
                    skillDetailController.checkChanges();
                    skillDetailController.editButton.setDisable(true);
                    skillDetailController.deleteButton.setDisable(true);
                    if (null != skills && skills.size() > 0) {
                        for (Skill s : skills) {
                            if (t1.getValue().equals(s.getCode() + " - " + s.getDescription())) {
                                skillDetailController.setSkill(s);
                                skillDetailController.setViewMode();
//                        skillDetailController.editButton.setDisable(false);
//                        skillDetailController.deleteButton.setDisable(false);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    public void setContainer(ScrollPane container) {
        this.skillsContainer = container;
        skillTreeView.
                prefWidthProperty().
                bind(skillsContainer.widthProperty());
        // skillTreeView.prefHeightProperty().bind(skillsContainer.heightProperty());
        if (null != skills && skills.size() > 0) {
            skillTreeView.setMinHeight(Font.getDefault().getSize() * (skills.size() + categories.size()) * 1.8);
        }
    }

    public void setSkillDetailController(SkillDetailController controller) {
        this.skillDetailController = controller;
    }

    public List<Skill> getSkills() {
        return skills;
    }
}
