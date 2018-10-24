/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.viewEmail;

import com.emailsystem.application.MainApp;
import com.emailsystem.data.FxBeanFactory;
import com.emailsystem.persistence.AttachmentDAO;
import com.emailsystem.presentation.LoginController.LoginController;
import com.emailsystem.presentation.attachmentView.AttachmentController;
import com.emailsystem.presentation.rootController.RootLayoutController;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author GamingDanik
 */
public class ViewController {
    @FXML
    private Text to;
    @FXML
    private Text subject;
    @FXML
    private WebView htmlView;
    @FXML
    private Text from;
    @FXML
    private Text cc;
    @FXML
    private Text bcc;
    
    private AttachmentController attachController;
    private AttachmentDAO attachDao;
    private FxBeanFactory fx;
    private String URL = "jdbc:mysql://localhost:3306/EmailDB?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    private String UNAME = "a1633867";
    private String PASSWORD = "dawson";
    private RootLayoutController root;
    
    @FXML
    private void initialize() {
        attachDao = new AttachmentDAO(URL,UNAME,PASSWORD);
    }
    
//    public void setMain(MainApp main) {
//        this.main = main;
//    }
    
    public void loadEmail(FxBeanFactory fx) {
        this.from.setText(fx.getFrom());
        this.subject.setText(fx.getSubject());
        this.htmlView.getEngine().loadContent(fx.getHtmlMsg());
        this.to.setText(fx.getTo());
        this.cc.setText(fx.getCc());
        this.bcc.setText(fx.getBcc());
        this.fx = fx;
    }
    
    public void openAttach(ActionEvent action) {
        try {
            load();
        } catch (SQLException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setRootController(RootLayoutController root) {
        this.root = root;
    }
    
    private void load() throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader();
            
            loader.setLocation(MainApp.class.getResource("/fxml/attachView.fxml"));
            
            AnchorPane attachView = (AnchorPane)loader.load();
            attachController = loader.getController();
            if (attachDao.read(fx.getId(), true).size() > 0)
                attachController.loadImage(attachDao.read(fx.getId(), true).get(0).getAttach());
            Stage primaryStage = new Stage();
            Scene scene = new Scene(attachView);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
        } catch (IOException ex) {
            System.out.println(ex + "");
        }
    }
    
    public void replyEmail(ActionEvent action) {
        root.replyEmail(fx.getFrom());
    }
    
    public void fwdEmail(ActionEvent action) {
        root.fwdEmail(this.fx);
    }
}
