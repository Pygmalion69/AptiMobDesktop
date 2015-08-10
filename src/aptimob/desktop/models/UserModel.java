/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author helfrich
 */
public class UserModel {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty username = new SimpleStringProperty();
    private IntegerProperty domain = new SimpleIntegerProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty firstName = new SimpleStringProperty();
    private StringProperty lastName = new SimpleStringProperty();
    private BooleanProperty available = new SimpleBooleanProperty();
    private StringProperty address = new SimpleStringProperty();
    private StringProperty postalCode = new SimpleStringProperty();
    private StringProperty city = new SimpleStringProperty();
    private StringProperty cellPhone = new SimpleStringProperty();
    private StringProperty phone = new SimpleStringProperty();
    private StringProperty taxCode = new SimpleStringProperty();
    private IntegerProperty country = new SimpleIntegerProperty();
    
    public UserModel() {
        
    }

    public UserModel(int id, String username, int domain, String firstName, String lastName, boolean available, String address, String postalCode, String city, int country, String cellPhone, String phone, String taxCode) {
        this.id.set(id);
        this.username.set(username);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.available.set(available);
        this.address.set(address);
        this.postalCode.set(postalCode);
        this.city.set(city);
        this.country.set(country);
        this.cellPhone.set(cellPhone);
        this.phone.set(phone);
        this.taxCode.set(taxCode);
    }

    public UserModel(int id, String username, int domain, String firstName, String lastName, boolean available, String city, String cellPhone, String phone) {
        this.id.set(id);
        this.username.set(username);
        this.domain.set(domain);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.available.set(available);
        this.city.set(city);
        this.cellPhone.set(cellPhone);
        this.phone.set(phone);
    }
    
    public UserModel(int id, String username, int domain, String firstName, String lastName, boolean available, String city) {
        this.id.set(id);
        this.username.set(username);
        this.domain.set(domain);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.available.set(available);
        this.city.set(city);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }
    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String fName) {
        firstName.set(fName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String fName) {
        lastName.set(fName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String uname) {
        username.set(uname);
    }

    public StringProperty usernameProperty() {
        return username;
    }
    
     public int getDomain() {
        return domain.get();
    }

    public void setDomain(int id) {
        this.domain.set(id);
    }
    
    public IntegerProperty domainProperty() {
        return domain;
    }
    
     public String getPassword() {
        return password.get();
    }

    public void setPassword(String passwd) {
        password.set(passwd);
    }
    
    public StringProperty passwordProperty() {
        return password;
    }

    public boolean isAvailable() {
        return available.get();
    }

    public void setAvailable(boolean avail) {
        available.set(avail);
    }

    public BooleanProperty availableProperty() {
        return available;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String a) {
        address.set(a);
    }

    public StringProperty addressProperty() {
        return address;
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public void setPostalCode(String pc) {
        postalCode.set(pc);
    }

    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String c) {
        city.set(c);
    }

    public StringProperty cityProperty() {
        return city;
    }
    
    public int getCountry() {
        return country.get();
    }

    public void setCountry(int c) {
        country.set(c);
    }

    public IntegerProperty countryProperty() {
        return country;
    }

    public String getCellPhone() {
        return cellPhone.get();
    }

    public void setCellPhone(String cp) {
        cellPhone.set(cp);
    }

    public StringProperty cellPhoneProperty() {
        return cellPhone;
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String p) {
        phone.set(p);
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getTaxCode() {
        return taxCode.get();
    }

    public void setTaxCode(String tc) {
        taxCode.set(tc);
    }

    public StringProperty taxCodeProperty() {
        return taxCode;
    }
}
