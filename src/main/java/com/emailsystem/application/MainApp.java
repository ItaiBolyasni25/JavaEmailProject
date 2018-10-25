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
import com.emailsystem.presentation.rootController.RootLayoutController;
import com.emailsystem.presentation.viewEmail.ViewController;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
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
    private AnchorPane loginPage;
    Locale currentLocale;

    private LoginController loginController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.primaryStage.setTitle("Email Application");


        try(InputStream in = getClass().getResourceAsStream("/UserInfo.properties")) {
            Properties propIn = new Properties();
            propIn.load(in);
            if (propIn.containsKey("emailValue") || propIn.containsKey("passwordValue")
                    || propIn.containsKey("dbUname") || propIn.containsKey("dbPassword")) {
                loadRoot();
            } else {
                initLayout();
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadRoot() throws IOException {
        this.currentLocale = new Locale("en", "CA");
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("Bundle", currentLocale));
        loader.setLocation(MainApp.class.getResource("/fxml/root.fxml"));
        Scene scene;
        scene = new Scene((AnchorPane) loader.load());
        RootLayoutController root = loader.getController();
        setScene(scene);
    }

    private void initLayout() {

        this.currentLocale = new Locale("en", "CA");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("Bundle", currentLocale));
            loader.setLocation(MainApp.class.getResource("/fxml/login.fxml"));
            loginPage = (AnchorPane) loader.load();
            loginController = loader.getController();
            loginController.setLocale(this.currentLocale);
            loginController.setMain(this);
            setScene(new Scene(loginPage));
        } catch (IOException ex) {
            LOG.error("initLayout failed " + ex);
        }
    }

    public static void main(String[] args) {

        launch(args);
        System.exit(0);
    }

    public void setScene(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

}
