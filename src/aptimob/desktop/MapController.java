/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.entities.Skill;
import aptimob.desktop.entities.SkillCategory;
import aptimob.desktop.entities.User;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

/**
 * FXML Controller class
 *
 * @author helfrich
 */
public class MapController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    StackPane mapPane;
    MyBrowser myBrowser;
    //WebView webView;
    private ScrollPane mapContainer;
    private List<User> users = new ArrayList<User>();
    private AvailabilityTableController availabilityTableController;
    private AptiMobMainController aptiMobMainController;
    private RestClient restClient;
    private HashMap<Integer, String> countryMap;
    private Preferences prefs;
    private double centerLon;
    private double centerLat;
    Text text = new Text();
    LonLat location;
    private int selectedUserId;
    private List<Integer> selectedUserIds;
    private List<Skill> skills;
    private List<SkillCategory> skillCategories;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        prefs = Preferences.userNodeForPackage(getClass());

        centerLon = prefs.getDouble("center_lon", 6);
        centerLat = prefs.getDouble("center_lat", 52);

        try {
            restClient = new RestClient(Preferences.userNodeForPackage(AptiMobDesktop.class).get("rest_url", ""));
        } catch (MalformedURLException e) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, e);
        }

        users = restClient.getAvailableUsers();
        skills = restClient.getSkills();
        skillCategories = restClient.getSkillCategories();

        myBrowser = new MyBrowser();
        //scene = new Scene(myBrowser, 800, 600);
        mapPane.getChildren().add(myBrowser);
    }

    /**
     * @return the selectedUserId
     */
    public int getSelectedUserId() {
        return selectedUserId;
    }

    /**
     * @param selectedUserId the selectedUserId to set
     */
    public void setSelectedUserId(int selectedUserId) {
        this.selectedUserId = selectedUserId;
    }

    /**
     * @return the selectedUserIds
     */
    public List<Integer> getSelectedUserIds() {
        return selectedUserIds;
    }

    /**
     * @param selectedUserIds the selectedUserIds to set
     */
    public void setSelectedUserIds(List<Integer> selectedUserIds) {
        this.selectedUserIds = selectedUserIds;
    }

    class MyBrowser extends Region {

        //HBox toolbar;
        // webView = new WebView();
        WebView webView = new WebView();
        WebEngine webEngine;

        public MyBrowser() {

            webEngine = webView.getEngine();

            // process page loading
            webEngine.getLoadWorker().stateProperty().addListener(
                    new ChangeListener<Worker.State>() {
                        @Override
                        public void changed(ObservableValue<? extends Worker.State> ov,
                                Worker.State oldState, Worker.State newState) {
                            if (newState == Worker.State.SUCCEEDED) {
                                JSObject win =
                                        (JSObject) webEngine.executeScript("window");
                                win.setMember("fxapp", new JavaApp());
                                //webEngine.executeScript("setMarker(6.24, 51.71, 12345, \"Piet Lozekoot\")");
                                setCenter(centerLon, centerLat);
                                if (users != null && users.size() > 0) {
                                    for (User user : users) {
                                        webEngine.executeScript("setMarker(" + Double.toString(user.getLon()) + ", " + Double.toString(user.getLat()) + ", " + user.getId() + ", \"" + user.getFirstName() + " " + user.getLastName() + "\", 0)");
                                    }
                                }
                            }
                        }
                    });

            final URL urlOSM = getClass().getResource("googlemaps.html");
            webEngine.load(urlOSM.toExternalForm());

            webView.setContextMenuEnabled(false);
            getChildren().add(webView);
        }

        public void clearMarkers() {
            webEngine.executeScript("clearMarkers()");
        }

        public void setMarkers() {
            int highlight;
            for (User user : users) {

                // int highlight = (user.getId() == selectedUserId) ? 1 : 0;
                highlight = 0;
                if (null != selectedUserIds && selectedUserIds.size() > 0) {
                    for (int id : selectedUserIds) {
                        if (user.getId() == id) {
                            highlight = 1;
                        }
                    }
                }
                webEngine.executeScript("setMarker(" + Double.toString(user.getLon()) + ", " + Double.toString(user.getLat()) + ", " + user.getId() + ", \"" + user.getFirstName() + " " + user.getLastName() + "\", " + Integer.toString(highlight) + ")");
            }
        }

        public void setCenter(double lon, double lat) {
            webEngine.executeScript("setCenter(" + Double.toString(lon) + ", " + Double.toString(lat) + ")");
        }

        public LonLat getCenter() {
            LonLat centerLonLat = new LonLat();
            centerLonLat.lon = Double.valueOf((String) webEngine.executeScript("getCenterLon()"));
            centerLonLat.lat = Double.valueOf((String) webEngine.executeScript("getCenterLat()"));
            return centerLonLat;
        }

        public void setLocation() {
            webEngine.executeScript("selectLocation()");
        }

        public void loadSource(String resource) {
            final URL urlOSM = getClass().getResource(resource);
            // webEngine.reload();
            webEngine.load(urlOSM.toExternalForm());
        }
    }

    // JavaScript interface object
    public class JavaApp {

        private ShowUserDetailsController showUserDetailsController;

        public void out(int id) {
            System.out.println(Integer.toString(id));
        }

        public void locationSelected(double lon, double lat) {
            mapPane.getScene().setCursor(Cursor.DEFAULT);
            setText("");
            System.out.println("lon: " + Double.toString(lon) + ", lat: " + Double.toString(lat));
            myBrowser.webEngine.executeScript("setLocationMarker(" + Double.toString(lon) + ", " + Double.toString(lat) + ")");
            availabilityTableController.setLocationButton.setDisable(false);
            availabilityTableController.distanceComboBox.setDisable(false);
            location = new LonLat(lon, lat);
            availabilityTableController.setLocation(location);
        }

        public void showDetails(int id) throws IOException {
            Stage userStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ShowUserDetails.fxml"), ResourceBundle.getBundle("bundles.strings"));
            userStage.setScene(new Scene((Parent) loader.load()));
            userStage.show();
            User user = null;
            for (User u : users) {
                if (u.getId() == id) {
                    user = u;
                    break;
                }
            }
            showUserDetailsController = loader.getController();
            showUserDetailsController.setCountryMap(countryMap);
            showUserDetailsController.setUser(user, skills, skillCategories);

            // Store map center
            LonLat centerLonLat = myBrowser.getCenter();
            double mapCenterLon = centerLonLat.lon;
            double mapCenterLat = centerLonLat.lat;

//            System.out.println("Lon: " + Double.toString(mapCenterLon));
//            System.out.println("Lat: " + Double.toString(mapCenterLat));
            prefs.putDouble("center_lon", mapCenterLon);
            prefs.putDouble("center_lat", mapCenterLat);
        }
    }

    public void setContainer(ScrollPane container) {
        this.mapContainer = container;
//        mapPane.
//                prefWidthProperty().
//                bind(mapContainer.widthProperty());
        myBrowser.webView.
                prefWidthProperty().
                bind(mapContainer.widthProperty());
    }

    public void setUsers(List<User> users) {
        this.users = users;
        //myBrowser.webEngine.reload();
        drawUsers();
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public void drawUsers() {
        myBrowser.clearMarkers();
        myBrowser.setMarkers();
        if (null != location) {
            myBrowser.webEngine.executeScript("setLocationMarker(" + Double.toString(location.lon) + ", " + Double.toString(location.lat) + ")");
        }
    }

    public void setText(String txt) {
        if ((null == txt || txt.equals("")) && null != text) {
            mapPane.getChildren().remove(text);
        } else {
            text.setText(txt);
//        text.setX(40f);
//        text.setY(40f);
            text.setFill(Color.BLUE);
            text.setFont(Font.font(null, FontWeight.BOLD, 16));
            mapPane.getChildren().add(text);
        }
    }

    public void setCountryMap(HashMap map) {
        this.countryMap = map;
    }

    public HashMap getCountryMap() {
        return this.countryMap;
    }

    public void setAvailabilityTableController(AvailabilityTableController controller) {
        this.availabilityTableController = controller;
    }

    public void setMainController(AptiMobMainController controller) {
        this.aptiMobMainController = controller;
    }

//    public void setMapSource(String mapSource) {
//        if (mapSource.equals("osm")) {
//            myBrowser.loadSource("openstreetmap.html");
//        }
//         if (mapSource.equals("wmsosm")) {
//            myBrowser.loadSource("wmsopenstreetmap.html");
//        }
//    }
    public class LonLat {

        double lon;
        double lat;

        private LonLat(double lon, double lat) {
            this.lon = lon;
            this.lat = lat;
        }

        private LonLat() {
        }
    }
}
