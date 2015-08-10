/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop;

import aptimob.desktop.entities.Country;
import aptimob.desktop.entities.Domain;
import aptimob.desktop.entities.Group;
import aptimob.desktop.entities.Message;
import aptimob.desktop.entities.Skill;
import aptimob.desktop.entities.SkillCategory;
import aptimob.desktop.entities.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 *
 * @author helfrich
 */
public class RestClient {

    public static final String HTTP_RESPONSE = "httpResponse";
    public static final String TRANSACTION_ID = "transactionID";
    public static final String HTTP_RESPONSE_CODE = "httpResponseCode";
    HttpsURLConnection con;
    int r;
    private String mAction;
    private String mTransactionID;
    private String payload = "";
    Preferences prefs;
    String user;
    String passwd;
    String authString;
    Base64 base64;
    String urlString;
    URL url;
    Gson gson;
    private int domain = 0;

    public RestClient(String urlString) throws MalformedURLException {
        this.urlString = urlString;
        this.gson = new Gson();
        // mAction = action;
        prefs = Preferences.userNodeForPackage(getClass());
        user = prefs.get("user", "");
        String encryptedPassword = prefs.get("passwd", "");
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("swordfish69");
        passwd = textEncryptor.decrypt(encryptedPassword);
        authString = user + ":" + passwd;
        base64 = new Base64();
        domain = prefs.getInt("domain", 0);
    }

    public RestClient(String action, String transactionID) {
        mAction = action;
        mTransactionID = transactionID;
    }

    public RestResult request(String function, String requestMethod, String body) {
        try {
            url = new URL(urlString + "/" + function);
            System.out.println(url.toString());
            con = (HttpsURLConnection) url.openConnection();
            if (null != body) {
                byte[] bodyBytes = body.getBytes();
                con.setRequestProperty("Content-Length",
                        String.valueOf(bodyBytes.length));
            }
            con.setReadTimeout(30000 /* milliseconds */);
            con.setConnectTimeout(50000 /* milliseconds */);
            con.setRequestMethod(requestMethod);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Authorization", "Basic " + base64.encodeToString(authString.getBytes()));
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

            if (null != body) {
                OutputStreamWriter writer = new OutputStreamWriter(
                        con.getOutputStream(), "UTF-8");
                writer.write(body);
                System.out.println(body);
                writer.flush();
            }

            // Start the query
            con.connect();
            r = con.getResponseCode();

            // Read results from the query
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    con.getInputStream(), "UTF-8"), 8 * 1024);
            payload = reader.readLine();
            reader.close();
            System.out.println(payload);

        } catch (IOException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = new RestResult();
        result.responseCode = r;
        result.payload = payload;
        return result;
    }

    public List<User> getUsers() {
        RestResult result = request("users", "GET", null);
        if (result.responseCode == 200) {
            Type userType = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(result.payload, userType);
            System.out.println(gson.toJson(userList));
            return userList;
        } else {
            return null;
        }
    }

    public List<User> getAvailableUsers() {
        RestResult result = request("availableUsers", "GET", null);
        if (result.responseCode == 200) {
            Type userType = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(result.payload, userType);
            System.out.println(gson.toJson(userList));
            return userList;
        } else {
            return null;
        }
    }

    public User getUserById(int userId) {
        String data = "";
        try {
            data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(userId), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("userById", "POST", data);
        if (result.responseCode == 200) {
            User user = gson.fromJson(result.payload, User.class);
            return user;
        } else {
            return null;
        }
    }

    public int getUserId() {
        return getUserId(user);
    }

    public int getUserId(String username) {
        String data = "";
        try {
            data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("userId", "POST", data);
        if (result.responseCode == 200) {
            JsonParser parser = new JsonParser();
            JsonArray jResult = parser.parse(result.payload).getAsJsonArray();
            JsonObject joId = jResult.get(0).getAsJsonObject();
            JsonPrimitive jId = joId.getAsJsonPrimitive("id");
            int id = jId.getAsInt();
            return id;
        } else {
            return 0;
        }
    }

    public void createUpdateUser(User user) {
        String id = Integer.toString(user.getId());
        String username = user.getUsername() == null ? "" : user.getUsername();
        String domain = Integer.toString(user.getDomain());
        String password = user.getPassword() == null ? "" : user.getPassword();
        String firstName = user.getFirstName() == null ? "" : user.getFirstName();
        String lastName = user.getLastName() == null ? "" : user.getLastName();
        String available = user.isAvailable() ? "1" : "0";
        String address = user.getAddress() == null ? "" : user.getAddress();
        String postalCode = user.getPostalCode() == null ? "" : user.getPostalCode();
        String city = user.getCity() == null ? "" : user.getCity();
        String country = Integer.toString(user.getCountry());
        String cellPhone = user.getCellPhone() == null ? "" : user.getCellPhone();
        String phone = user.getPhone() == null ? "" : user.getPhone();
        String taxCode = user.getTaxCode() == null ? "" : user.getTaxCode();
        String data = "";
        try {
            data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("domain", "UTF-8") + "=" + URLEncoder.encode(domain, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            data += "&" + URLEncoder.encode("firstName", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8");
            data += "&" + URLEncoder.encode("lastName", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8");
            data += "&" + URLEncoder.encode("available", "UTF-8") + "=" + URLEncoder.encode(available, "UTF-8");
            data += "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8");
            data += "&" + URLEncoder.encode("postalCode", "UTF-8") + "=" + URLEncoder.encode(postalCode, "UTF-8");
            data += "&" + URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(city, "UTF-8");
            data += "&" + URLEncoder.encode("country", "UTF-8") + "=" + URLEncoder.encode(country, "UTF-8");
            data += "&" + URLEncoder.encode("cellPhone", "UTF-8") + "=" + URLEncoder.encode(cellPhone, "UTF-8");
            data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
            data += "&" + URLEncoder.encode("taxCode", "UTF-8") + "=" + URLEncoder.encode(taxCode, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("createUpdateUser", "POST", data);
        System.out.println(result.responseCode);
        if (result.responseCode == 200) {
            // Ok
        }
    }

    public void deleteUser(int id) {
        String data = "";
        try {
            data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(id), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("deleteUser", "POST", data);
        if (result.responseCode == 200) {
            // Ok
        }
    }

    public List<Group> getGroups() {
        RestResult result = request("groups", "GET", null);
        if (result.responseCode == 200) {
            Type groupType = new TypeToken<List<Group>>() {
            }.getType();
            List<Group> groupList = gson.fromJson(result.payload, groupType);
            System.out.println(gson.toJson(groupList));
            return groupList;
        } else {
            return null;
        }
    }

    public void setGroups(String username, List<Group> groups) {
        List<String> groupNames = new ArrayList<>();
        for (Group group : groups) {
            groupNames.add(group.getGroup());
        }
        String data = "";
        String jsonGroups = gson.toJson(groupNames);
        try {
            data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("groups", "UTF-8") + "=" + URLEncoder.encode(jsonGroups, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(data);
        RestResult result = request("setUserGroups", "POST", data);
        if (result.responseCode == 200) {
            // Ok
        }
    }

    public List<Integer> getUserGroupIds(Integer userId) {
        String data = "";
        try {
            data = "userId=" + URLEncoder.encode(userId.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("userGroupIds", "POST", data);
        if (result.responseCode == 200) {
            Type groupType = new TypeToken<List<Integer>>() {
            }.getType();
            List<Integer> groupIds = gson.fromJson(result.payload, groupType);
            System.out.println(gson.toJson(groupIds));
            return groupIds;
        } else {
            return null;
        }
    }

    public List<String> getUserGroups(Integer userId) {
        String data = "";
        try {
            data = "userId=" + URLEncoder.encode(userId.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("userGroups", "POST", data);
        if (result.responseCode == 200) {
            Type groupType = new TypeToken<List<String>>() {
            }.getType();
            List<String> groups = gson.fromJson(result.payload, groupType);
            System.out.println(gson.toJson(groups));
            return groups;
        } else {
            return null;
        }
    }

    public List<Domain> getDomains() {
        RestResult result = request("domains", "GET", null);
        if (result.responseCode == 200) {
            Type domainType = new TypeToken<List<Domain>>() {
            }.getType();
            List<Domain> domainList = gson.fromJson(result.payload, domainType);
            System.out.println(gson.toJson(domainList));
            return domainList;
        } else {
            return null;
        }
    }

    public void createUpdateDomain(Domain domain) {
        String id = Integer.toString(domain.getId());
        String data = "";
        try {
            data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(domain.getName(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("createUpdateDomain", "POST", data);
        System.out.println(result.responseCode);
        if (result.responseCode == 200) {
            // Ok
        }
    }

    public List<Country> getCountries() {
        RestResult result = request("countryCodes", "GET", null);
        if (result.responseCode == 200) {
            Type countryType = new TypeToken<List<Country>>() {
            }.getType();
            List<Country> countryList = gson.fromJson(result.payload, countryType);
            System.out.println(gson.toJson(countryList));
            return countryList;
        } else {
            return null;
        }
    }

    public List<Skill> getSkills() {
        RestResult result = request("skills", "GET", null);
        if (result.responseCode == 200) {
            Type skillType = new TypeToken<List<Skill>>() {
            }.getType();
            List<Skill> skillList = gson.fromJson(result.payload, skillType);
            System.out.println(gson.toJson(skillList));
            return skillList;
        } else {
            return null;
        }
    }
    
     public void setSkills(String username, List<Skill> skills) {
        List<Integer> skillIds = new ArrayList<>();
        for (Skill skill : skills) {
            skillIds.add(skill.getId());
        }
        String data = "";
        String jsonSkills = gson.toJson(skillIds);
        try {
            data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("skills", "UTF-8") + "=" + URLEncoder.encode(jsonSkills, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(data);
        RestResult result = request("setUserSkills", "POST", data);
        if (result.responseCode == 200) {
            // Ok
        }
    }

    public List<SkillCategory> getSkillCategories() {
        RestResult result = request("skillCategories", "GET", null);
        if (result.responseCode == 200) {
            Type skillCategoryType = new TypeToken<List<SkillCategory>>() {
            }.getType();
            List<SkillCategory> skillCategoryList = gson.fromJson(result.payload, skillCategoryType);
            System.out.println(gson.toJson(skillCategoryList));
            return skillCategoryList;
        } else {
            return null;
        }
    }

    public void createUpdateSkill(Skill skill) {
        String id = Integer.toString(skill.getId());
        String data = "";
        try {
            data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(skill.getCategory()), "UTF-8");
            data += "&" + URLEncoder.encode("domain", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(skill.getDomain()), "UTF-8");
            data += "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(skill.getCode(), "UTF-8");
            data += "&" + URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(skill.getDescription(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("createUpdateSkill", "POST", data);
        System.out.println(result.responseCode);
        if (result.responseCode == 200) {
            // Ok
        }
    }

    public void deleteSkill(int id) {
        String data = "";
        try {
            data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(id), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("deleteSkill", "POST", data);
        if (result.responseCode == 200) {
            // Ok
        }
    }

    public void deleteSkillCategory(int id) {
        String data = "";
        try {
            data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(id), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("deleteSkillCategory", "POST", data);
        if (result.responseCode == 200) {
            // Ok
        }
    }

    public void createUpdateSkillCategory(SkillCategory category) {
        String id = Integer.toString(category.getId());
        String data = "";
        try {
            data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("domain", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(category.getDomain()), "UTF-8");
            data += "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(category.getCode(), "UTF-8");
            data += "&" + URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(category.getDescription(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("createUpdateSkillCategory", "POST", data);
        System.out.println(result.responseCode);
        if (result.responseCode == 200) {
            // Ok
        }
    }

    public int getCurrentUserDomain() {
        RestResult result = request("currentUserDomain", "GET", null);
        if (result.responseCode == 200) {

            JsonParser parser = new JsonParser();
            JsonObject jResult = parser.parse(result.payload).getAsJsonObject();
            JsonPrimitive jId = jResult.getAsJsonPrimitive("domain");
            int domain = jId.getAsInt();
            System.out.println(gson.toJson(domain));
            return domain;
        } else {
            return 0;
        }
    }
    
    public void sendMessage(List<Integer> userIds, int refId, int senderId, int domain, String to, String from, String subject, String body) {
        String data = "";
        String jsonUserIds = gson.toJson(userIds);
        try {
            data = URLEncoder.encode("userIds", "UTF-8") + "=" + URLEncoder.encode(jsonUserIds, "UTF-8");
            data += "&" + URLEncoder.encode("refId", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(refId), "UTF-8");
            data += "&" + URLEncoder.encode("senderId", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(senderId), "UTF-8");
            data += "&" + URLEncoder.encode("domain", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(domain), "UTF-8");
            data += "&" + URLEncoder.encode("to", "UTF-8") + "=" + URLEncoder.encode(to, "UTF-8");
            data += "&" + URLEncoder.encode("from", "UTF-8") + "=" + URLEncoder.encode(from, "UTF-8");
            data += "&" + URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8");
            data += "&" + URLEncoder.encode("body", "UTF-8") + "=" + URLEncoder.encode(body, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(data);
        RestResult result = request("message", "POST", data);
        if (result.responseCode == 200) {
            // Ok
        }
    }
    
    public void notify(List<String> usernames, String message) {
        String data = "";
        String jsonUsernames = gson.toJson(usernames);
         try {
            data = URLEncoder.encode("users", "UTF-8") + "=" + URLEncoder.encode(jsonUsernames, "UTF-8");
            data += "&" + URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(data);
        RestResult result = request("notify", "POST", data);
        if (result.responseCode == 200) {
            // Ok
        }
    }
    
     public List<Message> getMessagesSent(Integer userId) {
        String data = "";
        try {
            data = "userId=" + URLEncoder.encode(userId.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("messagesSent", "POST", data);
        if (result.responseCode == 200) {
            Type messageType = new TypeToken<List<Message>>() {
            }.getType();
            List<Message> messages = gson.fromJson(result.payload, messageType);
            System.out.println(gson.toJson(messages));
            return messages;
        } else {
            return null;
        }
    }
    
     public List<Message> getMessagesIn(Integer userId) {
        String data = "";
        try {
            data = "userId=" + URLEncoder.encode(userId.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        RestResult result = request("messagesIn", "POST", data);
        if (result.responseCode == 200) {
            Type messageType = new TypeToken<List<Message>>() {
            }.getType();
            List<Message> messages = gson.fromJson(result.payload, messageType);
            System.out.println(gson.toJson(messages));
            return messages;
        } else {
            return null;
        }
    }
}
