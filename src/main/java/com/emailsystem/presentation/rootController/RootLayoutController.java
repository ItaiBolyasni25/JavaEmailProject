/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.rootController;

import com.emailsystem.application.MainApp;
import com.emailsystem.data.EmailFXBean;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.persistence.FolderDAO;
import com.emailsystem.presentation.EmailTree.EmailTreeController;
import com.emailsystem.presentation.LoginController.LoginController;
import com.emailsystem.presentation.sendEmail.SendEmailController;
import com.emailsystem.presentation.table.EmailTableController;
import com.emailsystem.presentation.viewEmail.ViewController;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GamingDanik
 */
public class RootLayoutController {
    
    private static final Logger LOG = LoggerFactory.getLogger(RootLayoutController.class);
    
    @FXML
    private AnchorPane leftPane;
    @FXML
    private AnchorPane topSplit;
    @FXML
    private AnchorPane bottomSplit;
    @FXML
    private ResourceBundle resources;
    @FXML
    private TextField folderName;
    @FXML
    private Button compose;
    
    private String URL = "jdbc:mysql://localhost:3306/EmailDB?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    private String UNAME = "a1633867";
    private String PASSWORD = "dawson";
    private boolean isComposing = true;
    private SendEmailController emailController;
    private EmailTableController tableController;
    private ViewController viewController;
    private EmailTreeController treeController;
    private LoginController loginController;
    
    public void initialize() {
        
        
        initTopSplit();
        initLeftPane();
        initBottomSplit();
        setTableControllerToTree(this.tableController);
        try {
            tableController.displayTheTable("Inbox");

            treeController.displayTree();
        } catch (SQLException ex) {
            LOG.info("Didnt Executed " + ex.getMessage());
        }
        
    }
    
    private void setTableControllerToTree(EmailTableController table) {
        this.treeController.setEmailTableController(table);
    }
    
    private void initBottomSplit() {
        try {
            bottomSplit.getChildren().clear();
            FXMLLoader loader = new FXMLLoader();
            //loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/send_email.fxml"));
            AnchorPane sendEmail = (AnchorPane)loader.load();
            emailController = loader.getController();
            bottomSplit.getChildren().add(sendEmail);
        } catch (IOException ex) {
            LOG.error("initLeftPane failed ");
        }
    }
    
    
    public void setLoginController(LoginController login) {
        this.loginController = login;
    }
    
    private void initTopSplit() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            
            loader.setLocation(MainApp.class.getResource("/fxml/emailTable.fxml"));
            
            AnchorPane table = (AnchorPane)loader.load();
            tableController = loader.getController();
            //tableController.setEmailDao(dao);
            tableController.setRootController(this);
            topSplit.getChildren().add(table);
        } catch (IOException ex) {
            LOG.error(ex + "");
        }
    }
    
    private void initLeftPane() {
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/emailTree.fxml"));
            
            AnchorPane view = (AnchorPane)loader.load();
            
            treeController = loader.getController();
            //treeController.setEmailDao(dao);
            leftPane.getChildren().add(view);
        } catch (IOException ex) {
            LOG.error("initLeftPane failed ");
        }
    }
    
    public void replyEmail(String email) {
        bottomSplit.getChildren().clear();
        initBottomSplit();
        emailController.setTo(email);
        this.isComposing = true;
        compose.setText("Send Email");
    }
    
    public void fwdEmail(EmailFXBean fx) {
        initBottomSplit();
        emailController.setTo(fx.getFrom());
        emailController.setSubject("FWD: " + fx.getSubject());
        this.isComposing = true;
        compose.setText("Send Email");
    }
    
    public void changeBottomPane(AnchorPane view) {
        bottomSplit.getChildren().remove(0);
        bottomSplit.getChildren().add(view);
        this.isComposing = false;
        this.viewController.setRootController(this);
        compose.setText("Compose Email");
    }
    
    public void createFolder(ActionEvent action) {
        FolderDAO folderDao = new FolderDAO(URL,UNAME,PASSWORD);
        try {
            folderDao.create(folderName.getText());
            this.treeController.displayTree();
            folderName.clear();           
        } catch (SQLException ex) {
            errorAlert(ex + "");
        }
    }
    
    public void setViewController(ViewController view) {
        this.viewController = view;
    }
    
    public void composeEmail(ActionEvent action) {
        if (this.isComposing) {
            sendEmail();
        } else {
            bottomSplit.getChildren().remove(0);
            this.initBottomSplit();
            compose.setText("Send Email");
            this.isComposing = true;
        }
    }
    
    private void errorAlert(String msg) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle(resources.getString("sqlError"));
        dialog.setHeaderText(resources.getString("sqlError"));
        dialog.setContentText(resources.getString(msg));
        dialog.show();
    }
    
    private void sendEmail() {
        LOG.info("Email was sent!");
        initBottomSplit();
    }
}
