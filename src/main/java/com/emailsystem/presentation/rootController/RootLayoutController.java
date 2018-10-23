/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.rootController;

import com.emailsystem.application.MainApp;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.presentation.EmailTree.EmailTreeController;
import com.emailsystem.presentation.sendEmail.SendEmailController;
import com.emailsystem.presentation.table.EmailTableController;
import com.emailsystem.presentation.viewEmail.ViewController;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    
    private final EmailDAO dao;
    private SendEmailController emailController;
    private EmailTableController tableController;
    private ViewController viewController;
    private EmailTreeController treeController;
    
    
    public RootLayoutController() {
        dao = new EmailDAO();
    }
    
    public void initialize() {
        
        initBottomSplit();
        initTopSplit();
        initLeftPane();
        
        try {
            tableController.displayTheTable();
            LOG.info("Executed");
            treeController.displayTree();
            
        } catch (SQLException ex) {
            LOG.info("Didnt Executed " + ex.getMessage());
        }
    }
    
    private void initBottomSplit() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/send_email.fxml"));
            
            AnchorPane sendEmail = (AnchorPane)loader.load();
            emailController = loader.getController();
            
            bottomSplit.getChildren().add(sendEmail);
        } catch (IOException ex) {
            LOG.error("initLeftPane failed ");
        }
    }
    
    private void initTopSplit() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            
            loader.setLocation(MainApp.class.getResource("/fxml/emailTable.fxml"));
            
            AnchorPane table = (AnchorPane)loader.load();
            tableController = loader.getController();
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
            LOG.info("before did this!!");
            leftPane.getChildren().add(view);
            LOG.info("did this!!");
        } catch (IOException ex) {
            LOG.error("initLeftPane failed ");
        }
    }
    
    public void changeBottomPane(AnchorPane view) {
        bottomSplit.getChildren().remove(0);
        bottomSplit.getChildren().add(view);
    }
}
