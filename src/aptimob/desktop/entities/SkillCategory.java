/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop.entities;

/**
 *
 * @author helfrich
 */
public class SkillCategory {
    
    private int id;
    private int domain;
    private String code;
    private String description;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the domain
     */
    public int getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(int domain) {
        this.domain = domain;
    }
    
}
