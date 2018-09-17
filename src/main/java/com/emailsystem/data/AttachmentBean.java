/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.data;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author 1633867
 */
public class AttachmentBean {
    private byte[] attach = {};
    private String name = "bob.jpg";
    

    public byte[] getAttach() {
        return attach;
    }

    public void setAttach(byte[] attach) {
        this.attach = attach;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public AttachmentBean(String name, byte[] attach) {
        this.name = name;
        this.attach = attach;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
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
        final AttachmentBean other = (AttachmentBean) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.attach.length != other.attach.length) {
            return false;
        }
        return true;
    }

    
    
}
