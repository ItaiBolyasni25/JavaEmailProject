/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem;

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
    
    public AttachmentBean() {
        
    }
}
