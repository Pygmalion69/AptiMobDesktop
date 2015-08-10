/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author helfrich
 */
public class CountryModel {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty code = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();

    public CountryModel() {
    }

    public CountryModel(int id, String code, String name) {
        this.id.set(id);
        this.code.set(code);
        this.name.set(name);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int i) {
        this.id.set(i);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getCode() {
        return code.get();
    }

    public void setCode(String c) {
        this.code.set(c);
    }

    public StringProperty code() {
        return code;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty name() {
        return name;
    }
}
