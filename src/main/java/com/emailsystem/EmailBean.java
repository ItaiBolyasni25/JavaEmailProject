/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 1633867
 */
public class EmailBean {
    private String from;
    private String[] to = {};
    private String[] cc = {};
    private String[] bcc = {};
    private String subject;
    private String textMsg;
    private String HTMLMsg;
    private AttachmentBean[] attach = {};
    private AttachmentBean[] embedAttach = {};
    private int priority;
    
    private LocalDateTime sentTime;
    private LocalDateTime receivedTime;
    
    public EmailBean() {
        sentTime = LocalDateTime.now();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String[] getCc() {
        return cc;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public void setBcc(String[] bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTextMsg() {
        return textMsg;
    }

    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
    }

    public String getHTMLMsg() {
        return HTMLMsg;
    }

    public void setHTMLMsg(String HTMLMsg) {
        this.HTMLMsg = HTMLMsg;
    }

    public AttachmentBean[] getAttach() {
        return attach;
    }

    public void setAttach(AttachmentBean[] attach) {
        this.attach = attach;
    }

    public AttachmentBean[] getEmbedAttach() {
        return embedAttach;
    }

    public void setEmbedAttach(AttachmentBean[] embedAttach) {
        this.embedAttach = embedAttach;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public LocalDateTime getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(LocalDateTime receivedTime) {
        this.receivedTime = receivedTime;
    }
    



}
