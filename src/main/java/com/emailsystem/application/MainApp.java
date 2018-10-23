/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.application;

import com.emailsystem.presentation.LoginController.LoginController;
import java.io.IOException;
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
    
    private LoginController loginController;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Email Application");
        initLayout();
        Scene scene = new Scene(loginPage);
        primaryStage.setScene(scene);
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
    
    public static void main(String[] args) {
        launch(args);
        System.exit(0);
    }
    
    public void setScene(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
}
