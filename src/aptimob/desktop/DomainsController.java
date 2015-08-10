/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.models.DomainModel;
import aptimob.desktop.entities.Domain;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author helfrich
 */
public class DomainsController implements Initializable {

    @FXML
    private ListView domainListView;
    @FXML
    private TextField selectedDomainField;
    @FXML
    private Button saveButton;
    @FXML
    private AnchorPane rootWindow;
    @FXML
    private Button cancelButton;
    private RestClient restClient;
    private ResourceBundle rb;
    private List<Domain> domainList;
    private ObservableList<DomainModel> domainModelList;
    private int selectedId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.rb = rb;

        try {
            restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
        } catch (MalformedURLException e) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, e);
        }

        // domainListView.setCellFactory(new PropertyValueFactory<DomainModel, String>("name"));
        domainListView.setCellFactory(new Callback<ListView<DomainModel>, ListCell<DomainModel>>() {
            @Override
            public ListCell<DomainModel> call(ListView<DomainModel> p) {

                return new DomainNameCell();
            }
        });
        domainModelList = domainListView.getItems();
        domainList = restClient.getDomains();
        for (Domain d : domainList) {
            domainModelList.add(new DomainModel(d.getId(), d.getName()));
        }

        domainListView.setItems(domainModelList);
        domainListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                DomainModel selectedDomain = (DomainModel) newValue;
                //System.out.println(selectedUserModel.username.get() + " selected.");
                if (selectedDomain != null) {
                    if (selectedDomain.getName() != null) {
                        selectedDomainField.setText(selectedDomain.getName());
                        selectedId = selectedDomain.getId();
                    }
                    selectedDomainField.setDisable(false);
                    saveButton.setDisable(false);
                    cancelButton.setDisable(false);
                }
            }
        });

    }

    public void handleCancelButton(ActionEvent event) throws IOException {
        Stage domainStage = (Stage) rootWindow.getScene().getWindow();
        domainStage.hide();
    }

    public void handleSaveButton(ActionEvent event) throws IOException {
        Domain domain = new Domain(selectedDomainField.getText());
        domain.setId(selectedId);
        saveButton.setDisable(true);
        selectedDomainField.setDisable(true);
        restClient.createUpdateDomain(domain);
        domainList = restClient.getDomains();
        domainModelList.clear();
        for (Domain d : domainList) {
            domainModelList.add(new DomainModel(d.getId(), d.getName()));
        }
    }

    public void handleAddButton(ActionEvent event) throws IOException {
        domainListView.getSelectionModel().select(null);
        selectedDomainField.setDisable(false);
        selectedDomainField.setText("");
        selectedDomainField.requestFocus();
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        selectedId = 0;
    }

    static class DomainNameCell extends ListCell<DomainModel> {

        @Override
        public void updateItem(DomainModel item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getName());
            }
        }
    }
}
