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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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

        emailAddress.setCellValueFactory(cellData -> cellData.getValue().getFromProperty());
        subject.setCellValueFactory(cellData -> cellData.getValue().getSubjectProperty());
        description.setCellValueFactory(cellData -> cellData.getValue().getTextMsgProperty());
        
        emailTableView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) -> showEmailDetails(newValue));
        drag();
    }
     /**
     * Displays the table
     *
     * @param folderName - the folder from which to display the items
     * @throws SQLException
     * @version 1.0.0
     */
    public void displayTheTable(String folderName) throws SQLException {
        // Add observable list data to the table
        MailModule mail = new MailModule();
        mail.setProperties(prop);
        
        for (EmailBean bean : mail.receive()) {
            LOG.info(bean + "");
            dao.createEmail(bean);
        }
        LOG.info(folderName + " " + prop.getProperty("emailValue"));
        LOG.info("dsf " + EmailFXBean.transformBeanListToFxList(dao.findEmailsInFolder(folderName)));
        emailTableView.setItems(EmailFXBean.transformBeanListToFxList(dao.findEmailsInFolder(folderName)));

    }

     /**
     * method to set up all the drag events
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    private void drag() {
        this.emailTableView.setRowFactory(tv -> {
            TableRow<EmailFXBean> row = new TableRow<>();
            
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() || row != null) {
                    if (e.isDragDetect()) {
                        EmailFXBean fxBean = (EmailFXBean) emailTableView.getSelectionModel().getSelectedItem();
                        if (fxBean != null && !fxBean.getFolderName().equalsIgnoreCase("Sent")) {
                            row.setOnDragDetected(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    Dragboard db = emailTableView.startDragAndDrop(TransferMode.ANY);
                                    ClipboardContent content = new ClipboardContent();
                                    content.putString(fxBean.getId() + "");
                                    db.setContent(content);
                                    LOG.info("Starting drag event on email with id => " + content.getString());
                                    event.consume();
                                }
                            });
                            
                            row.setOnDragOver(new EventHandler<DragEvent>() {
                                @Override
                                public void handle(DragEvent event) {
                                    LOG.debug("Here");
                                    Dragboard db = event.getDragboard();
                                    if (event.getDragboard().hasString()) {
                                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                                    }
                                    event.consume();
                                }
                            });
                        }
                    }
                }
            });
            return row;
        });
    }

    public void setProperties(Properties prop) {
        this.prop = prop;
    }

     /**
     * method to show email details on click
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    private void showEmailDetails(EmailFXBean newValue) {
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/viewEmail.fxml"));
            loader.setResources(resources);
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

    public TableView getTableView() {
        return this.emailTableView;
    }

    public void setEmailDao(EmailDAO dao) {
        this.dao = dao;
    }

    public void setRootController(RootLayoutController root) {
        this.root = root;
    }
}
