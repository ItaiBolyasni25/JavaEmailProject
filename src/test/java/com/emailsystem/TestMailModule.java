package com.emailsystem;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author GamingDanik
 */
public class TestMailModule {
    
    private MailModule mail;
    private EmailBean bean;
    
    @Before
    public void init() {
        bean = new EmailBean();
        bean.setFrom("send.1633867@gmail.com");
        bean.setTo(new String[] {"receive.1633867@gmail.com"});
        bean.setSubject("Sending emails with java test new program");
        bean.setTextMsg("Test");
        bean.setCc(new String[] {"send.1633867@gmail.com", "cc2@gmail.com"});
        bean.setHTMLMsg("<!DOCTYPE HTML><html><head></head><body>" + bean.getTextMsg() + "</body></html>");
        mail = new MailModule();
    }
    
    @Test
    public void sendingEmailWithoutAttachments() {
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        EmailBean bean2 = mail.receive()[0];
        System.out.println(bean.toString());
        System.out.println(bean2.toString());
        Assert.assertEquals(bean2, bean);
    }
    
    @Test
    public void sendingEmailWithNormalAttachment() {
        List<AttachmentBean> attach = new ArrayList<AttachmentBean>();
        try {
            attach.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
            bean.setAttach(attach);
        } catch (Exception e) {
            
        }
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        EmailBean bean2 = mail.receive()[0];
        System.out.println(bean.toString());
        System.out.println(bean2.toString());
        Assert.assertEquals(bean2, bean);
    }
    
    @Test
    public void sendingEmailWithAllAttachments() {
        List<AttachmentBean> attach = new ArrayList<AttachmentBean>();
        List<AttachmentBean> embed = new ArrayList<AttachmentBean>();
        
        try {
            embed.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
            attach.add(new AttachmentBean("thumbsup.jpg", Files.readAllBytes(new File("thumbsup.jpg").toPath())));
            bean.setAttach(attach);
            bean.setEmbedAttach(embed);
            bean.setHTMLMsg("<!DOCTYPE HTML><html><head></head><body>Test<br><h2>This is attached </h2><img style='width: 300px; height: 250px' src='cid:java.jpg'></body></html>");
        } catch (Exception e) {
            
        }
        
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        EmailBean bean2 = mail.receive()[0];
        System.out.println(bean.toString());
        System.out.println(bean2.toString());
        Assert.assertEquals(bean2, bean);
    }
}
