/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aptimob.desktop.models;

import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author helfrich
 */
public class MessageModel {

    private IntegerProperty id = new SimpleIntegerProperty();
    private IntegerProperty refId = new SimpleIntegerProperty();
    private IntegerProperty domain = new SimpleIntegerProperty();
    private ListProperty<Integer> userIds = new SimpleListProperty<>();
    private IntegerProperty senderId = new SimpleIntegerProperty();
    private StringProperty to = new SimpleStringProperty();
    private StringProperty from = new SimpleStringProperty();
    private StringProperty subject = new SimpleStringProperty();
    private StringProperty body = new SimpleStringProperty();
    private SimpleLongProperty timestamp = new SimpleLongProperty();
    private StringProperty dateTime = new SimpleStringProperty();

    public IntegerProperty idProperty() {
        return id;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int i) {
        this.id.set(i);
    }

    public int getRefId() {
        return refId.get();
    }

    public void setRefId(int rid) {
        refId.set(rid);
    }

    public int getDomain() {
        return domain.get();
    }

    public void setDomain(int d) {
        domain.set(d);
    }
    
    public ListProperty userIdsProperty() {
        return userIds;
    }

    public List<Integer> getUserIds() {
        return userIds.get();
    }

    public void setUserIds(ObservableList<Integer> ids) {
        userIds.set(ids);
    }

    public IntegerProperty senderIdProperty() {
        return senderId;
    }

    public int getSendertId() {
        return senderId.get();
    }

    public void setSenderId(int i) {
        this.senderId.set(i);
    }

    public StringProperty toProperty() {
        return to;
    }

    public String getTo() {
        return to.get();
    }

    public void setTo(String s) {
        this.to.set(s);
    }

    public StringProperty fromProperty() {
        return from;
    }

    public String getFrom() {
        return from.get();
    }

    public void setFrom(String s) {
        this.from.set(s);
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public String getSubject() {
        return subject.get();
    }

    public void setSubject(String s) {
        this.subject.set(s);
    }

    public StringProperty bodyProperty() {
        return body;
    }

    public String getBody() {
        return body.get();
    }

    public void setBody(String s) {
        this.body.set(s);
    }

    public SimpleLongProperty timestampProperty() {
        return timestamp;
    }

    public long getTimestamp() {
        return timestamp.get();
    }

    public void setTimestamp(long i) {
        this.timestamp.set(i);
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public void setDateTime(String s) {
        dateTime.set(s);
    }
}
