/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.configuration;

import com.emailsystem.presentation.rootController.RootLayoutController;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author GamingDanik
 */
public class ConfigurationController {

    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField dbUname;
    @FXML
    private TextField dbPassword;
    @FXML
    private TextField name;
    @FXML
    private TextField dbUrl;
    @FXML
    private TextField imapServer;
    @FXML
    private TextField smtpServer;
    @FXML
    private TextField imapPort;
    @FXML
    private TextField smtpPort;
    @FXML
    private ResourceBundle resources;

    private Properties prop;
    private Stage stage;
    private RootLayoutController root;

    @FXML
    public void initialize() {

    }

    public void populateForm() {
        this.email.setText(prop.getProperty("emailValue"));
        this.password.setText(prop.getProperty("passwordValue"));
        this.dbUname.setText(prop.getProperty("dbUname"));
        this.dbPassword.setText(prop.getProperty("dbPassword"));
        this.dbUrl.setText(prop.getProperty("dbUrl"));
        this.imapServer.setText(prop.getProperty("imapServer"));
        this.smtpServer.setText(prop.getProperty("smtpServer"));
        this.imapPort.setText(prop.getProperty("imapPort"));
        this.smtpPort.setText(prop.getProperty("smtpPort"));
        this.name.setText(prop.getProperty("name"));
    }

    @FXML
    public void onSubmit(ActionEvent event) {
        try (OutputStream os = new FileOutputStream("src/main/resources/UserInfo.properties")) {
                prop.setProperty("emailValue", email.getText());
                prop.setProperty("passwordValue", password.getText());
                prop.setProperty("dbUname", dbUname.getText());
                prop.setProperty("dbPassword", dbPassword.getText());
                prop.setProperty("name", name.getText());
                prop.setProperty("imapServer", imapServer.getText());
                prop.setProperty("smtpServer", smtpServer.getText());
                prop.setProperty("dbUrl", dbUrl.getText());
                prop.setProperty("imapPort", imapPort.getText());
                prop.setProperty("smtpPort", smtpPort.getText());
                prop.store(os, null);
            } catch (IOException ex) {
            Logger.getLogger(ConfigurationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.stage.close();
        root.doWork();
    }
    
    public void setRootLayout(RootLayoutController root) {
        this.root = root;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProperties(Properties prop) {
        this.prop = prop;
    }
}
