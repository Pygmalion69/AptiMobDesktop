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
public class SkillCategoryModel {
    
    private IntegerProperty id = new SimpleIntegerProperty();
    private IntegerProperty domain = new SimpleIntegerProperty();
    private StringProperty code = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    
    public SkillCategoryModel(int id, int domain, String code, String description) {
        this.id.set(id);
        this.domain.set(domain);
        this.code.set(code);
        this.description.set(description);
    }

    public SkillCategoryModel() {
        
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
    
    public int getDomain() {
        return domain.get();
    }
    
    public void setDomain(int d) {
        domain.set(d);
    }

    public IntegerProperty domainProperty() {
        return domain;
    }
    
    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code.set(code);
    }
    
     public StringProperty codeProperty() {
        return code;
    }
    
     public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
    
     public StringProperty descriptionProperty() {
        return description;
    }
}
