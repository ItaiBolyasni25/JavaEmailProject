/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.table;

import com.emailsystem.application.MainApp;
import com.emailsystem.data.EmailBean;
import com.emailsystem.data.FxBeanFactory;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.presentation.rootController.RootLayoutController;
import com.emailsystem.presentation.viewEmail.ViewController;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GamingDanik
 */
public class EmailTableController {
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane tablePane;

    @FXML
    private TableView<FxBeanFactory> emailTableView;

    @FXML
    private TableColumn<FxBeanFactory, String> emailAddress;

    @FXML
    private TableColumn<FxBeanFactory, String> subject;

    @FXML
    private TableColumn<FxBeanFactory, String> description;
    
    private EmailDAO dao = new EmailDAO();
    private RootLayoutController root;
    private final static Logger LOG = LoggerFactory.getLogger(EmailTableController.class);
    
    public void initialize() {
                // Connects the property in the FishData object to the column in the
        // table
        emailAddress.setCellValueFactory(cellData -> cellData.getValue().getFromProperty());
        subject.setCellValueFactory(cellData -> cellData.getValue().getSubjectProperty());
        description.setCellValueFactory(cellData -> cellData.getValue().getTextMsgProperty());
        
         emailTableView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) -> showEmailDetails(newValue));
        
    }
    
    public void displayTheTable(String folderName) throws SQLException {
        // Add observable list data to the table
        emailTableView.setItems(FxBeanFactory.transformBeanListToFxList(dao.findEmailsInFolder(folderName)));
    }

    private void showEmailDetails(FxBeanFactory newValue) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/viewEmail.fxml"));
            
            AnchorPane viewEmail = (AnchorPane)loader.load();
            ViewController view = loader.getController();
            this.root.setViewController(view);
            view.loadEmail(newValue);
            
            root.changeBottomPane(viewEmail);
        } catch (IOException ex) {
            LOG.error("initLeftPane failed ");
        }
        
    }
    
    public void setRootController(RootLayoutController root) {
        this.root = root;
    }
}
