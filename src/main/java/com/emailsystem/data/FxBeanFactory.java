/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.data;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author GamingDanik
 */
public class FxBeanFactory {
    
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty from = new SimpleStringProperty();
    private StringProperty subject = new SimpleStringProperty();
    private StringProperty textMsg = new SimpleStringProperty();
    private StringProperty folderName = new SimpleStringProperty();
    
    public FxBeanFactory(EmailBean bean) {
        this.id.set(bean.getId());
        this.from.set(bean.getFrom());
        this.subject.set(bean.getSubject());
        this.textMsg.set(bean.getHTMLMsg());   
        this.folderName.set(bean.getFolderName());
    }
    
    public FxBeanFactory() {
        
    }

    public StringProperty getFolderNameProperty() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName.set(folderName);
    }
    
    public String getFolderName() {
        return this.folderName.get();
    }

    
    public static ObservableList<FxBeanFactory> transformBeanListToFxList(List<EmailBean> beans) {
        ObservableList<FxBeanFactory> list = FXCollections.observableArrayList();
        for (EmailBean bean: beans) {
            FxBeanFactory fx = new FxBeanFactory(bean);
            list.add(fx);
        }
        return list;
    }

    public StringProperty getFromProperty() {
        return from;
    }

    public void setFrom(String from) {
        this.from.set(from);
    }
    
    public String getFrom() {
        return this.from.get();
    }

    public StringProperty getSubjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }
    
    public String getSubject() {
        return this.subject.get();
    }

    public StringProperty getTextMsgProperty() {
        return textMsg;
    }

    public void setTextMsg(String textMsg) {
        this.textMsg.set(textMsg);
    }
    
    public String getTextMsg() {
        return this.textMsg.get();
    }
    
}
