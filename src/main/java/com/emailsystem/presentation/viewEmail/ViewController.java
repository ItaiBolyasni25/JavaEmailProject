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
import com.emailsystem.presentation.popUp.AttachListController;
import com.emailsystem.presentation.rootController.RootLayoutController;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    @FXML
    private Button openAttach;
    @FXML
    private ResourceBundle resources;

    private List<AttachmentBean> list;
    private AttachmentController attachController;
    private AttachmentDAO attachDao;
    private EmailFXBean fx;
    private RootLayoutController root;
    private int numOfAttach = 0;
    private final static Logger LOG = LoggerFactory.getLogger(ViewController.class);
    private Properties prop;

    public ViewController() {

    }

    public void setProperties(Properties prop) {
        this.prop = prop;
    }

    @FXML
    private void initialize() {

    }

    public void start() {

    }

    public void setAttachDAO(AttachmentDAO attachDao) {
        this.attachDao = attachDao;
    }

    public void loadEmail(EmailFXBean fx) {
        try {
            this.attachDao = new AttachmentDAO(prop.getProperty("dbUname"), prop.getProperty("dbPassword"));
            System.out.println(fx.getId());
            System.out.println(this.attachDao);
            this.list = attachDao.read(fx.getId(), false);
            this.from.setText(fx.getFrom());
            this.subject.setText(fx.getSubject());
            this.htmlView.getEngine().loadContent(fx.getHtmlMsg());
            this.to.setText(fx.getTo());
            this.cc.setText(fx.getCc());
            this.bcc.setText(fx.getBcc());
            this.fx = fx;
            this.openAttach.setText(this.openAttach.getText() + " (" + list.size() + ")");
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openAttach(ActionEvent action) {
        try {
            loadAttach();
        } catch (SQLException ex) {
            LOG.warn("Couldnt open attach");
        }
    }

    public void setRootController(RootLayoutController root) {
        this.root = root;
    }

    private void loadAttach() throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader();
            Scene scene = null;
            this.numOfAttach = list.size();
            if (this.numOfAttach == 1) {
                loader.setLocation(MainApp.class.getResource("/fxml/attachView.fxml"));
                AnchorPane attachView = (AnchorPane) loader.load();
                attachController = loader.getController();
                attachController.loadImage(list.get(0).getAttach());
                scene = new Scene(attachView);
            } else if (this.numOfAttach > 1) {
                loader.setLocation(MainApp.class.getResource("/fxml/attachViewPopUp.fxml"));
                AnchorPane popUp = (AnchorPane) loader.load();
                AttachListController listController = loader.getController();
                listController.displayList(list);
                scene = new Scene(popUp);
                System.out.println("HERE");
            } else {
                return;
            }
            Stage primaryStage = new Stage();
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
        root.replyEmail(this.fx);
    }

    public void fwdEmail(ActionEvent action) {
        root.fwdEmail(this.fx);
    }
}
