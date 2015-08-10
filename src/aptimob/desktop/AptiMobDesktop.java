package aptimob.desktop;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author helfrich
 */
public class AptiMobDesktop extends Application {

    Preferences prefs;
    @FXML
    private ScrollPane overview;
    @FXML
    private Button testButton;

    public static void main(String[] args) {
        Application.launch(AptiMobDesktop.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        prefs = Preferences.userNodeForPackage(getClass());
        String loginUrl = "https://www.nitri.de/aptimob/login";
        String restUrl = "https://www.nitri.de/aptimob/rest";
        prefs.put("login_url", loginUrl);
        prefs.put("rest_url", restUrl);
        //Parent root = FXMLLoader.load(getClass().getResource("LayoutTest.fxml"));
        //FXMLLoader loader = new FXMLLoader();
        //loader.setResources(ResourceBundle.getBundle("/bundles/en.properties"));

        Locale.setDefault(new Locale("en"));

        ResourceBundle rb = ResourceBundle.getBundle("bundles.strings");

        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"), rb);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(rb.getString("ApplicationName") + " - " + rb.getString("Login"));
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public void loggedIn() {
    }
}
