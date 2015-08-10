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
public class GroupModel {
    
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty group = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    
    public GroupModel(int id, String group, String description) {
        this.id.set(id);
        this.group.set(group);
        this.description.set(description);
    }

    public int getId() {
        return id.get();
    }

    public String getGroup() {
        return group.get();
    }

    public void setName(String group) {
        this.group.set(group);
    }
    
     public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
