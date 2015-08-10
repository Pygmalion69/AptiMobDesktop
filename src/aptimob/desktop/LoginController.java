package aptimob.desktop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.net.ssl.HttpsURLConnection;
import org.jasypt.util.text.TextEncryptor;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.text.BasicTextEncryptor;
import se.mbaeumer.fxmessagebox.MessageBox;
import se.mbaeumer.fxmessagebox.MessageBoxType;

/**
 *
 * @author helfrich
 */
public class LoginController implements Initializable {

    @FXML
    private AnchorPane rootWindow;
    @FXML
    private TextField userField;
    @FXML
    private TextField passwdField;
    @FXML
    private Button loginButton;
    @FXML
    Label messageField;
    private String user;
    private String passwd;
    private ResourceBundle bundle;
    private Preferences prefs;
    private RestClient restClient;

    @Override
    public void initialize(URL arg0, ResourceBundle resources) {

        this.bundle = resources;

        prefs = Preferences.userNodeForPackage(getClass());
        String userPref = prefs.get("user", "");
        if (!userPref.equals("")) {
            user = userPref;
            userField.setText(userPref);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    passwdField.requestFocus();
                }
            });
        }

        passwdField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            final KeyCombination combo = new KeyCodeCombination(KeyCode.ENTER);

            @Override
            public void handle(KeyEvent t) {
                if (combo.match(t)) {
                    if (userField.getText() != null && passwdField.getText() != null) {
                        if (!userField.getText().equals(
                                "") && !userField.getText().equals("")) {
                            try {
                                handleLoginButton(null);
                            } catch (IOException ex) {
                                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        });
    }

    @FXML
    private void handleLoginButton(ActionEvent event) throws IOException {

        HttpsURLConnection con;
        int r = 0;

        Stage primaryStage = (Stage) rootWindow.getScene().getWindow();
        //rootWindow.getChildren().removeAll();

        user = userField.getText();
        passwd = passwdField.getText();

        Base64 base64 = new Base64();

        // Locale.setDefault(new Locale("nl"));
        
        messageField.setText("");

        if (user.isEmpty()) {
            MessageBox mb = new MessageBox(bundle.getString("EnterUserField"), MessageBoxType.OK_ONLY);
            mb.show();
        } else if (passwd.isEmpty()) {
            MessageBox mb = new MessageBox(bundle.getString("EnterPasswdField"), MessageBoxType.OK_ONLY);
            mb.show();
        } else {

            loginButton.setDisable(true);
            rootWindow.getScene().setCursor(Cursor.WAIT);
            try {
                URL url = new URL(Preferences.userNodeForPackage(AptiMobDesktop.class).get("login_url", ""));
                String body = "";
                byte[] bodyBytes = body.getBytes();
                con = (HttpsURLConnection) url.openConnection();
                con.setReadTimeout(30000 /* milliseconds */);
                con.setConnectTimeout(50000 /* milliseconds */);
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                String authString = user + ":" + passwd;
                con.setRequestProperty("Authorization", "Basic " + base64.encodeToString(authString.getBytes()));
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Content-Length",
                        String.valueOf(bodyBytes.length));
                OutputStreamWriter writer = new OutputStreamWriter(
                        con.getOutputStream(), "UTF-8");
                writer.write(body);
                writer.flush();

                // Start the query
                con.connect();
                r = con.getResponseCode();
                System.out.println(r);
                // Read results from the query
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        con.getInputStream(), "UTF-8"), 8 * 1024);
                String payload = reader.readLine();
                reader.close();
                //return true;
                //return payload;

            } catch (Exception e) {
                messageField.setTextFill(Color.RED);
                if (r == 401) {
                    messageField.setText(bundle.getString("InvalidUsernamePassword"));
                } else {
                    messageField.setText(bundle.getString("Error") + ": " + Integer.toString(r));
                }
                e.printStackTrace();
                //return false;
            }

            rootWindow.getScene().setCursor(Cursor.DEFAULT);
            loginButton.setDisable(false);
            if (r == 200) {
                BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                textEncryptor.setPassword("swordfish69");
                prefs.put("user", user);
                prefs.put("passwd", textEncryptor.encrypt(passwd));
                try {
                    restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(UserDetailController.class.getName()).log(Level.SEVERE, null, ex);
                }
                prefs.putInt("user_id", restClient.getUserId());
                ResourceBundle rb = ResourceBundle.getBundle("bundles.strings");
                Stage secondaryStage = new Stage();
                Scene mainScene = new Scene((Parent) FXMLLoader.load(getClass().getResource("AptiMobMain.fxml"), rb));
                mainScene.getStylesheets().add("/aptimob/desktop/css/booleancellbox.css");
                secondaryStage.setScene(mainScene);
                secondaryStage.setTitle(rb.getString("ApplicationName"));
                //secondaryStage.sizeToScene();
                secondaryStage.show();
                //secondaryStage.toFront();
                primaryStage.hide();
            }
        }
    }
}
