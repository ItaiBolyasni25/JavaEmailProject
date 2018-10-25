package com.emailsystem.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.emailsystem.business.MailModule;
import com.emailsystem.data.AttachmentBean;
import com.emailsystem.data.EmailBean;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Itai Bolyasni
 */
public class TestMailModule extends Assert {

    private MailModule mail;
    private EmailBean bean = new EmailBean();;
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(MailModule.class);

    @Before
    public void init() {
        bean.setFrom("send.1633867@gmail.com");
        bean.setTo(new String[]{"receive.1633867@gmail.com"});
        bean.setSubject("Sending emails with java test new program");
        bean.setTextMsg("Test");
        bean.setCc(new String[]{"send.1633867@gmail.com", "cc2@gmail.com"});
        bean.setHTMLMsg("<!DOCTYPE HTML><html><head></head><body>" + bean.getTextMsg() + "</body></html>");
        mail = new MailModule();
    }

    @Test
    public void sendingEmailWithoutAttachments() throws SQLException{
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        EmailBean bean2 = mail.receive()[0];
        assertEquals(bean2, bean);
    }

    @Test
    public void sendingEmailWithNormalAttachment() throws SQLException{
        List<AttachmentBean> attach = new ArrayList<AttachmentBean>();
        try {
            attach.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
            bean.setAttach(attach);
        } catch (IOException e) {

        }
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        EmailBean bean2 = mail.receive()[0];
        assertEquals(bean2, bean);
    }

    @Test
    public void sendingEmailWithAllAttachments() throws SQLException{
        List<AttachmentBean> attach = new ArrayList<AttachmentBean>();
        List<AttachmentBean> embed = new ArrayList<AttachmentBean>();

        try {
            embed.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
            attach.add(new AttachmentBean("thumbsup.jpg", Files.readAllBytes(new File("thumbsup.jpg").toPath())));
            bean.setAttach(attach);
            bean.setEmbedAttach(embed);
            // The HTMLStringBuilder will return a parsed(with img tag) html while the normal bean doesn't have an img tag
            bean.setHTMLMsg("<!DOCTYPE HTML><html><head></head><body>Test<br><img style='width: 300px; height: 250px' src='cid:java.jpg'></body></html>");
        } catch (IOException e) {

        }

        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        EmailBean bean2 = mail.receive()[0];
        assertEquals(bean2, bean);
    }

    @Test(expected=SQLException.class)
    public void sendEmailWithInvalidFrom() throws SQLException {
        bean.setFrom("kshflkjshfgj12369");
        mail.send(bean);
    }

    @Test(expected=SQLException.class)
    public void sendEmailWithInvalidTo() throws SQLException{
        bean.setTo(new String[]{"kshflkjshfgj12369"});
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(mail.receive().length, 0);
    }

    @Test
    public void sendAndReceiveTwoEmails() throws SQLException{
        mail.send(bean);
        try {
            Thread.sleep(2000);
            mail.send(bean);
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertEquals(mail.receive().length, 2);
    }

    @Test(expected=SQLException.class)
    public void sendEmailWithInvalidCc() throws SQLException{
        bean.setCc(new String[]{"kshflkjshfgj12369"});
        mail.send(bean);
    }

    @Test(expected=SQLException.class)
    public void sendEmailWithInvalidBcc() throws SQLException{
        bean.setBcc(new String[]{"kshflkjshfgj12369"});
        mail.send(bean);
    }

    @Test(expected=SQLException.class)
    public void sendEmptyEmail() throws SQLException{
        bean = new EmailBean();
        mail.send(bean);
    }

    @Test
    public void sendTextFileAsAttachment() throws IOException, SQLException {
        List<AttachmentBean> attach = new ArrayList<AttachmentBean>();
        attach.add(new AttachmentBean("text.txt", Files.readAllBytes(new File("text.txt").toPath())));
        bean.setAttach(attach);
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(attach.get(0), mail.receive()[0].getAttach().get(0));
    }

    //For some reason using @Test(IOException.class) gave me a runtime error
    @Test(expected=IOException.class)
    public void invalidAttachmentPath() throws SQLException, IOException{
        List<AttachmentBean> attach = new ArrayList<AttachmentBean>();
        attach.add(new AttachmentBean("notfound.jpg", Files.readAllBytes(new File("notfound.jpg").toPath())));
        bean.setAttach(attach);
        mail.send(bean);
    }

    @Test
    public void sendToMultiplePeople() throws SQLException{
        bean.setTo(new String[]{"send.1633867@gmail.com", "receive.1633867@gmail.com"});
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(Arrays.toString(mail.receive()[0].getTo()), Arrays.toString(new String[]{"send.1633867@gmail.com", "receive.1633867@gmail.com"}));
    }

    @Test
    public void sendToMultipleCc() throws SQLException{
        bean.setCc(new String[]{"send.1633867@gmail.com", "receive.1633867@gmail.com", "cc.1633867@gmail.com"});
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertEquals(Arrays.toString(mail.receive()[0].getCc()), Arrays.toString(new String[]{"send.1633867@gmail.com", "receive.1633867@gmail.com", "cc.1633867@gmail.com"}));
    }

    @Test
    public void sendEmailWithMultipleFields() throws SQLException{
        bean.setCc(new String[]{"send.1633867@gmail.com", "receive.1633867@gmail.com", "cc.1633867@gmail.com"});
        bean.setBcc(new String[]{"send.1633867@gmail.com", "receive.1633867@gmail.com", "cc.1633867@gmail.com"});
        bean.setTo(new String[]{"send.1633867@gmail.com", "receive.1633867@gmail.com"});
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(mail.receive()[0], bean);
    }

    @Test
    public void sendingEmailWithMultipleAttachments() throws SQLException{
        List<AttachmentBean> attach = new ArrayList<AttachmentBean>();
        List<AttachmentBean> embed = new ArrayList<AttachmentBean>();

        try {
            embed.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
            attach.add(new AttachmentBean("thumbsup.jpg", Files.readAllBytes(new File("thumbsup.jpg").toPath())));
            attach.add(new AttachmentBean("test.txt", Files.readAllBytes(new File("text.txt").toPath())));
            embed.add(new AttachmentBean("FreeFall.jpg", Files.readAllBytes(new File("FreeFall.jpg").toPath())));
            bean.setAttach(attach);
            bean.setEmbedAttach(embed);
            bean.setHTMLMsg("<!DOCTYPE HTML><html><head></head><body>Test<br><img style='width: 300px; height: 250px' src='cid:java.jpg'><br><img style='width: 300px; height: 250px' src='cid:FreeFall.jpg'></body></html>");
        } catch (IOException e) {

        }

        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMailModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(mail.receive()[0], bean);
    }

}
