/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.table;

import com.emailsystem.application.MainApp;
import com.emailsystem.business.MailModule;
import com.emailsystem.data.EmailBean;
import com.emailsystem.data.EmailFXBean;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.presentation.rootController.RootLayoutController;
import com.emailsystem.presentation.viewEmail.ViewController;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
    private TableView<EmailFXBean> emailTableView;

    @FXML
    private TableColumn<EmailFXBean, String> emailAddress;

    @FXML
    private TableColumn<EmailFXBean, String> subject;

    @FXML
    private TableColumn<EmailFXBean, String> description;

    private EmailDAO dao;
    private RootLayoutController root;
    private final static Logger LOG = LoggerFactory.getLogger(EmailTableController.class);
    private Properties prop;

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
        MailModule mail = new MailModule();
        for (EmailBean bean : mail.receive()) {
            System.out.println(bean.getAttach().size() + " another " + bean.getEmbedAttach());
            dao.createEmail(bean);
        }
        emailTableView.setItems(EmailFXBean.transformBeanListToFxList(dao.findEmailsInFolder(folderName)));

    }

    public void setProperties(Properties prop) {
        this.prop = prop;
    }

    private void showEmailDetails(EmailFXBean newValue) {
        try {
            FXMLLoader loader = new FXMLLoader();

            //loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/viewEmail.fxml"));
            AnchorPane viewEmail = (AnchorPane) loader.load();
            ViewController view = loader.getController();
            root.setViewController(view);
            view.setProperties(this.prop);
            
            if (newValue != null) {
                view.loadEmail(newValue);
            }
            root.changeBottomPane(viewEmail);
        } catch (IOException ex) {
            LOG.error("initLeftPane failed " + ex);
            ex.printStackTrace();
        }

    }

    public void setEmailDao(EmailDAO dao) {
        this.dao = dao;
    }

    public void setRootController(RootLayoutController root) {
        this.root = root;
    }
}
