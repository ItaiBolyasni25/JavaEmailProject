/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.application;

import com.emailsystem.business.MailModule;
import com.emailsystem.data.AttachmentBean;
import com.emailsystem.data.EmailBean;
import com.emailsystem.persistence.AttachmentDAO;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.presentation.LoginController.LoginController;
import com.emailsystem.presentation.viewEmail.ViewController;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GamingDanik
 */
public class MainApp extends Application {
    
    private final static Logger LOG = LoggerFactory.getLogger(MainApp.class);
    
    private Stage primaryStage;
    private AnchorPane rootLayout;
    private AnchorPane loginPage;
    
    private final static String URL = "jdbc:mysql://localhost:3306/EmailDB?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final static String UNAME = "a1633867";
    private final static String PASSWORD = "dawson";
    
    private LoginController loginController;
    private ViewController view;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        this.primaryStage.setTitle("Email Application");
        loadViewLayout();
        initLayout();
        
        Scene scene = new Scene(loginPage);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        
    }
    
    private void initLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            
            loader.setLocation(MainApp.class.getResource("/fxml/login.fxml"));
            loginPage = (AnchorPane)loader.load();
            loginController = loader.getController();
            loginController.setMain(this);

        } catch (IOException ex) {
            LOG.error("initLayout failed");
        }
    }
    
    private void loadViewLayout() {
            FXMLLoader loader = new FXMLLoader();
            
            loader.setLocation(MainApp.class.getResource("/fxml/viewEmail.fxml"));
        try {
            AnchorPane temp = loader.load();
            this.view = loader.getController();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    public static void main(String[] args) {
        //view.login = loginController;
        //System.out.println("this " + view.login);
        launch(args);
        System.exit(0);
    }
    
    public void setScene(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    
}
