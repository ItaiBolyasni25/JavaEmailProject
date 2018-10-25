/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.viewEmail;

import com.emailsystem.application.MainApp;
import com.emailsystem.data.AttachmentBean;
import com.emailsystem.data.EmailFXBean;
import com.emailsystem.persistence.AttachmentDAO;
import com.emailsystem.presentation.LoginController.LoginController;
import com.emailsystem.presentation.attachmentView.AttachmentController;
import com.emailsystem.presentation.rootController.RootLayoutController;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private EmailFXBean fx;
    private RootLayoutController root;
    private int numOfAttach = 0;
    private final static Logger LOG = LoggerFactory.getLogger(ViewController.class);
    
    public ViewController() {
        
        try {
            Properties prop = new Properties();
            InputStream is = getClass().getResourceAsStream("/UserInfo.properties");
            prop.load(is);
            attachDao = new AttachmentDAO(prop.getProperty("dbUname"), "dbPassword");
            
        } catch (IOException ex) {
            
        }
    }

    @FXML
    private void initialize() {
        
        
    }

//    public void setMain(MainApp main) {
//        this.main = main;
//    }
    public void loadEmail(EmailFXBean fx) {
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
            LOG.warn("Couldnt open attach");
        }
    }

    public void setRootController(RootLayoutController root) {
        this.root = root;
    }

    private void load() throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(MainApp.class.getResource("/fxml/attachView.fxml"));

            AnchorPane attachView = (AnchorPane) loader.load();
            attachController = loader.getController();
            List<AttachmentBean> list = attachDao.read(fx.getId(), true);
            if (attachDao.read(fx.getId(), true).size() > 0) {
                attachController.loadImage(list.get(0).getAttach());
            }
            this.numOfAttach = list.size();
            Stage primaryStage = new Stage();
            Scene scene = new Scene(attachView);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (IOException ex) {
            LOG.warn(ex.getMessage());
        }
    }

    public int getNumOfAttach() {
        return this.numOfAttach;
    }

    public void replyEmail(ActionEvent action) {
        root.replyEmail(fx.getFrom());
    }

    public void fwdEmail(ActionEvent action) {
        root.fwdEmail(this.fx);
    }
}
