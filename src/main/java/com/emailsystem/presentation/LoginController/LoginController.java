/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.LoginController;

import com.emailsystem.application.MainApp;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.presentation.rootController.RootLayoutController;
import com.emailsystem.presentation.table.EmailTableController;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GamingDanik
 */
public class LoginController {

    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField dbUname;
    @FXML
    private TextField dbPassword;

    @FXML
    private Text action;

    private ResourceBundle resources;
    private Locale locale;

    MainApp main;
    private final static Logger LOG = LoggerFactory.getLogger(LoginController.class);
    private Properties propIn;

    @FXML
    private void initialize() {

    }
    public void setProperties(Properties prop) {
        this.propIn = prop;
    }
    @FXML
    protected void onSubmit(ActionEvent event) {
        try (InputStream in = getClass().getResourceAsStream("/UserInfo.properties")) {
            OutputStream os = new FileOutputStream("src/main/resources/UserInfo.properties");
            propIn.load(in);
            if (!propIn.containsKey("emailValue") || !propIn.containsKey("passwordValue")
                    || !propIn.containsKey("dbUname") || !propIn.containsKey("dbPassword")) {
                propIn.setProperty("emailValue", email.getText());
                propIn.setProperty("passwordValue", password.getText());
                propIn.setProperty("dbUname", dbUname.getText());
                propIn.setProperty("dbPassword", dbPassword.getText());
            }
            propIn.store(os, null);
            System.out.println("Prop1: " + propIn.getProperty("dbUname"));
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("Bundle", locale));
            loader.setLocation(MainApp.class.getResource("/fxml/root.fxml"));
            Parent bob = (AnchorPane)loader.load();
            RootLayoutController root = loader.getController();
            root.setProperties(propIn);
            root.setLoginController(this);
            root.doWork();

            Scene scene;
            scene = new Scene(bob);

            main.setScene(scene);
        } catch (IOException ex) {
            LOG.warn("Error loading root.fxml " + this.propIn);
            ex.printStackTrace();
        }



    }

    public void setMain(MainApp main) {
        this.main = main;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
