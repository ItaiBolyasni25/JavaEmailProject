/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.popUp;

import com.emailsystem.application.MainApp;
import com.emailsystem.presentation.rootController.RootLayoutController;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author GamingDanik
 */
public class LanguageController {

    private MainApp main;
    private Stage stage;
    private Properties prop;
    
    @FXML
    private ResourceBundle resources;

    public void setProperties(Properties prop) {
        this.prop = prop;
    }

    public void setMain(MainApp main) {
        this.main = main;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {

    }

    @FXML
    public void setEnResources(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("Bundle", new Locale("en", "CA")));
            loader.setLocation(MainApp.class.getResource("/fxml/root.fxml"));
            Scene scene;
            scene = new Scene((AnchorPane) loader.load());
            RootLayoutController root = loader.getController();
            root.setProperties(prop);
            root.setMain(main);
            root.doWork();

            main.setScene(scene);
            stage.close();
        } catch (IOException ex) {
            Logger.getLogger(LanguageController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    public void setFrResources(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("Bundle", new Locale("fr", "CA")));
            loader.setLocation(MainApp.class.getResource("/fxml/root.fxml"));
            Scene scene;
            scene = new Scene((AnchorPane) loader.load());
            RootLayoutController root = loader.getController();
            root.setProperties(prop);
            root.setMain(main);
            root.doWork();

            main.setScene(scene);
            stage.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
