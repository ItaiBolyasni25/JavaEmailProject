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
import java.io.IOException;
import java.util.logging.Level;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    private Text action;
    
    
    MainApp main;
    private final static Logger LOG = LoggerFactory.getLogger(LoginController.class);
    private final EmailDAO dao;
    StringProperty emailString = new SimpleStringProperty();
    StringProperty passString = new SimpleStringProperty();
    
    public LoginController() {
        this.dao = new EmailDAO();
    }
    
    @FXML
    private void initialize() {
        Bindings.bindBidirectional(email.textProperty(), emailString);
        Bindings.bindBidirectional(password.textProperty(), passString);

    }
    
    @FXML
    protected void onSubmit(ActionEvent event) {
        
        if (true) {
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(MainApp.class.getResource("/fxml/root.fxml"));
            
            Scene scene;
            try {
                scene = new Scene((AnchorPane) loader.load());
                RootLayoutController root = loader.getController();
                main.setScene(scene);
            } catch (IOException ex) {
               LOG.info(ex + "");
            }
        } else {
            LOG.info("THIS");
            action.setText("Wrong email address or password!");
        }
        

        
    }
    
    
    public void setMain(MainApp main) {
        this.main = main;
    }
}
