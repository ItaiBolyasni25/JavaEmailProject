/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private List<AttachmentBean> attach;
    private List<AttachmentBean> embedAttach;
    private int priority;
    
    private LocalDateTime sentTime;
    private LocalDateTime receivedTime;
    
    public EmailBean() {
        sentTime = LocalDateTime.now();
        attach = new ArrayList<AttachmentBean>();
        embedAttach = new ArrayList<AttachmentBean>();
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

    public List<AttachmentBean> getAttach() {
        return attach;
    }

    public void setAttach(List<AttachmentBean> attach) {
        this.attach = attach;
    }

    public List<AttachmentBean> getEmbedAttach() {
        return embedAttach;
    }

    public void setEmbedAttach(List<AttachmentBean> embedAttach) {
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.from);
        hash = 59 * hash + Arrays.deepHashCode(this.to);
        hash = 59 * hash + Arrays.deepHashCode(this.cc);
        hash = 59 * hash + Objects.hashCode(this.subject);
        hash = 59 * hash + Objects.hashCode(this.textMsg);
        hash = 59 * hash + Objects.hashCode(this.HTMLMsg);
        hash = 59 * hash + Objects.hashCode(this.attach);
        hash = 59 * hash + Objects.hashCode(this.embedAttach);
        hash = 59 * hash + this.priority;
        hash = 59 * hash + Objects.hashCode(this.sentTime);
        return hash;
    }

    @Override
    public String toString() {
        return "EmailBean{" + "from=" + from + ", to=" + Arrays.toString(to) + ", cc=" + Arrays.toString(cc) + ", bcc=" + bcc + ", subject=" + subject + ", textMsg=" + textMsg + ", HTMLMsg=" + HTMLMsg + ", attach=" + attach + ", embedAttach=" + embedAttach + ", priority=" + priority + ", sentTime=" + sentTime + ", receivedTime=" + receivedTime + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EmailBean other = (EmailBean) obj;
        if (this.priority != other.priority) {
            return false;
        }
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        if (!Objects.equals(this.textMsg, other.textMsg)) {
            return false;
        }
        if (!Objects.equals(this.HTMLMsg, other.HTMLMsg)) {
            return false;
        }
        if (!Arrays.deepEquals(this.to, other.to)) {
            return false;
        }
        if (!Arrays.deepEquals(this.cc, other.cc)) {
            return false;
        }
        for (int i = 0; i < other.attach.size() ; i++) {
            if (!Objects.equals(this.attach.get(i),other.attach.get(i))) {
                return false;
            }
        }
        for (int i = 0; i < other.embedAttach.size() ; i++) {
            if (!Objects.equals(this.embedAttach.get(i),other.embedAttach.get(i))) {
                return false;
            }
        }
        return true;
    }
    
}
