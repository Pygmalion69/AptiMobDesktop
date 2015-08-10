/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 *
 * @author helfrich
 */
public class UserController implements Initializable {

    private ResourceBundle bundle;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfPassword;
    @FXML
    private Button btnAdd;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.bundle = arg1;
//        UserModel model = new UserModel();
//        model.name.set("Serge Helfrich");
//        btnAdd.setText("Add");
//        tfName.setText("zzz");
//        tfUsername.setText("xxx");
//        tfName.textProperty().bindBidirectional(model.name);

    }
}
