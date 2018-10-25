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
public class EmailFXBean {
    
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty from = new SimpleStringProperty();
    private StringProperty subject = new SimpleStringProperty();
    private StringProperty textMsg = new SimpleStringProperty();
    private StringProperty folderName = new SimpleStringProperty();
    private StringProperty htmlMsg = new SimpleStringProperty();
    private StringProperty to = new SimpleStringProperty();
    private StringProperty cc = new SimpleStringProperty();
    private StringProperty bcc = new SimpleStringProperty();
    
    
    public EmailFXBean(EmailBean bean) {
        this.id.set(bean.getId());
        this.from.set(bean.getFrom());
        this.subject.set(bean.getSubject());
        this.textMsg.set(bean.getTextMsg().length() > 32 ? bean.getTextMsg().substring(0,32) + "..." : bean.getTextMsg());
        this.htmlMsg.set(bean.getHTMLMsg());
        this.folderName.set(bean.getFolderName());
        this.cc.set(buildRecipientString(bean.getCc()));
        this.bcc.set(buildRecipientString(bean.getBcc()));
        this.to.set(buildRecipientString(bean.getTo()));
        
        
    }
    
    private String buildRecipientString(String[] recipients) {
        String sb = "";
        for (int i = 0; i < recipients.length; i++) {
                sb += recipients[i];
                if (i != recipients.length - 1) {
                    sb += "; ";
                }   
            }
         return sb;
    }
    
    public EmailFXBean() {
        
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
    
    public StringProperty getCcProperty() {
        return cc;
    }

    public void setCc(String folderName) {
        this.cc.set(folderName);
    }
    
    public String getCc() {
        return this.cc.get();
    }
    
    public StringProperty getBccProperty() {
        return bcc;
    }

    public IntegerProperty getIdProperty() {
        return id;
    }
    
    public int getId() {
        return this.id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setBcc(String folderName) {
        this.bcc.set(folderName);
    }
    
    public String getBcc() {
        return this.bcc.get();
    }
    
    public StringProperty getToProperty() {
        return to;
    }

    public void setTo(String folderName) {
        this.to.set(folderName);
    }
    
    public String getTo() {
        return this.to.get();
    }
    
    public StringProperty getHtmlMsgProperty() {
        return this.htmlMsg;
    }

    public void setHtmlMsg(String folderName) {
        this.htmlMsg.set(folderName);
    }
    
    public String getHtmlMsg() {
        return this.htmlMsg.get();
    }
    
    public static ObservableList<EmailFXBean> transformBeanListToFxList(List<EmailBean> beans) {
        ObservableList<EmailFXBean> list = FXCollections.observableArrayList();
        for (EmailBean bean: beans) {
            EmailFXBean fx = new EmailFXBean(bean);
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
