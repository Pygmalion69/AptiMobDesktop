/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.entities.Skill;
import aptimob.desktop.entities.SkillCategory;
import aptimob.desktop.entities.User;
import com.sun.deploy.util.StringUtils;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 *
 * @author helfrich
 */
public class ShowUserDetailsController implements Initializable {

    private ResourceBundle rb;
    private User user;
    @FXML
    AnchorPane rootPane;
    @FXML
    Label fullNameText;
    @FXML
    Label skillsText;
    @FXML
    Label addressText;
    @FXML
    Label postalCodeAndCityText;
    @FXML
    Label countryText;
    @FXML
    Label cellPhoneText;
    @FXML
    Label phoneText;
    @FXML
    Button emailButton;
    @FXML
    Button messageButton;
    @FXML
    Button closeButton;
    private HashMap<Integer, String> countryMap;
    private Comparator<Skill> skillComparator;
    private Comparator<SkillCategory> categoryComparator;
    private List<ListedSkill> listedSkills = new ArrayList();
    private Comparator<ListedSkill> listedSkillByCategoryComparator;
    private FXMLLoader newMessageLoader;
    private NewMessageController newMessageController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        categoryComparator = new Comparator<SkillCategory>() {
            @Override
            public int compare(SkillCategory o1, SkillCategory o2) {
                return o1.getCode().toUpperCase().compareTo(o2.getCode().toUpperCase());
            }
        };


        skillComparator = new Comparator<Skill>() {
            @Override
            public int compare(Skill o1, Skill o2) {
                return o1.getCode().toUpperCase().compareTo(o2.getCode().toUpperCase());
            }
        };

        listedSkillByCategoryComparator = new Comparator<ListedSkill>() {
            @Override
            public int compare(ListedSkill o1, ListedSkill o2) {
                return o1.categoryCode.toUpperCase().compareTo(o2.categoryCode.toUpperCase());
            }
        };

    }

    public void setCountryMap(HashMap map) {
        this.countryMap = map;
    }

    public void setUser(User u, List<Skill> skills, List<SkillCategory> categories) {
        String skillCode = null;
        String categoryCode = null;
        Collections.sort(categories, categoryComparator);
        Collections.sort(skills, skillComparator);
        this.user = u;
        String fullName = user.getFirstName() + " " + user.getLastName();
        fullNameText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        ((Stage) rootPane.getScene().getWindow()).setTitle(fullName);
        fullNameText.setText(fullName);
        addressText.setText(user.getAddress());
        postalCodeAndCityText.setText(user.getPostalCode() + " " + user.getCity());
        countryText.setText(countryMap.get(user.getCountry()));
        cellPhoneText.setText(user.getCellPhone());
        phoneText.setText(user.getPhone());
        emailButton.setText(user.getUsername());
        // List<String> userSkills = new ArrayList();
        if (null != u.getSkills() && u.getSkills().length > 0) {
            for (int skillId : u.getSkills()) {
                for (Skill skill : skills) {
                    if (skillId == skill.getId()) {
                        skillCode = skill.getCode();
                        for (SkillCategory category : categories) {
                            if (skill.getCategory() == category.getId()) {
                                categoryCode = category.getCode();
                                break;
                            }
                        }
                        break;
                    }
                }
                if (null != skillCode && null != categoryCode) {
                    listedSkills.add(new ListedSkill(categoryCode, skillCode));
                }
            }
        }
        if (listedSkills.size() > 0) {
            Collections.sort(listedSkills, listedSkillByCategoryComparator);
        }

        List<String> skillsPerCategory = new ArrayList<>();
        StringBuilder skillsTextBuilder = new StringBuilder();
        //List<String> userCategoryCodes = new ArrayList<>();

        String prevCat = "";
        for (ListedSkill listedSkill : listedSkills) {
            if (!listedSkill.categoryCode.equals(prevCat)) {
                //userCategoryCodes.add(listedSkill.categoryCode);
                prevCat = listedSkill.categoryCode;
                List<String> skillsForCategory = new ArrayList<>();
                for (ListedSkill ls : listedSkills) {
                    if (ls.categoryCode.equals(listedSkill.categoryCode)) {
                        skillsForCategory.add(ls.skillCode);
                    }
                }
                skillsTextBuilder.append(listedSkill.categoryCode).append(": ").append(StringUtils.join(skillsForCategory, ", "));
                skillsPerCategory.add(skillsTextBuilder.toString());
                skillsTextBuilder = new StringBuilder();
            }
        }
        skillsText.setText(StringUtils.join(skillsPerCategory, "; "));

//        for (int skillId : u.getSkills()) {
//            for (Skill skill : skills) {
//                if (skillId == skill.getId()) {
//                    userSkills.add(skill.getCode());
//                }
//            }
//        }
//        if (userSkills.size() > 0) {
//            skillsText.setText("(" + StringUtils.join(userSkills, ", ") + ")");
//        }
    }

    @FXML
    private void handleCloseButton(ActionEvent event) {
        rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleEmailButton(ActionEvent event) throws IOException, URISyntaxException {
        Desktop.getDesktop().mail(new URI("mailto:" + user.getUsername()));
    }

    @FXML
    private void handleMessageButton(ActionEvent event) throws IOException {
        Stage newMessageStage = new Stage();
        newMessageLoader = new FXMLLoader(getClass().getResource("NewMessage.fxml"), ResourceBundle.getBundle("bundles.strings"));
        newMessageStage.setScene(new Scene((Parent) newMessageLoader.load()));
        newMessageStage.setTitle(rb.getString("ApplicationName") + " - " + rb.getString("Message"));
        newMessageStage.show();
        newMessageController = newMessageLoader.getController();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setNewMessageRecipient();
            }
        });
    }

    private void setNewMessageRecipient() {
        List<User> users = new ArrayList();
        users.add(user);
        newMessageController.setUsers(users);
    }

    private class ListedSkill {

        public ListedSkill(String categoryCode, String skillCode) {
            this.categoryCode = categoryCode;
            this.skillCode = skillCode;
        }
        String categoryCode;
        String skillCode;
    }
}
