/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.rootController;

import com.emailsystem.application.MainApp;
import com.emailsystem.business.MailModule;
import com.emailsystem.data.EmailFXBean;
import com.emailsystem.persistence.AttachmentDAO;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.persistence.FolderDAO;
import com.emailsystem.presentation.EmailTree.EmailTreeController;
import com.emailsystem.presentation.LoginController.LoginController;
import com.emailsystem.presentation.configuration.ConfigurationController;
import com.emailsystem.presentation.popUp.LanguageController;
import com.emailsystem.presentation.sendEmail.SendEmailController;
import com.emailsystem.presentation.table.EmailTableController;
import com.emailsystem.presentation.viewEmail.ViewController;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GamingDanik
 */
public class RootLayoutController {

    private static final Logger LOG = LoggerFactory.getLogger(RootLayoutController.class);

    @FXML
    private AnchorPane leftPane;
    @FXML
    private AnchorPane topSplit;
    @FXML
    private AnchorPane bottomSplit;
    @FXML
    private ResourceBundle resources;
    @FXML
    private TextField folderName;
    @FXML
    private Button compose;

    private String URL;
    private String UNAME;
    private String PASSWORD;
    private boolean isComposing = true;
    private SendEmailController emailController;
    private EmailTableController tableController;
    private ViewController viewController;
    private EmailTreeController treeController;
    private EmailDAO dao;
    private AttachmentDAO attachDao;
    private Properties prop;
    private MainApp main;

    public RootLayoutController() {

    }

     /**
     * Method that initializes all the components of the root layout
     *
     * @version 1.0.0
     */
    public void start() {
        this.URL = prop.getProperty("dbUrl");
        this.UNAME = prop.getProperty("dbUname");
        this.PASSWORD = prop.getProperty("dbPassword");
        try {
            this.dao = new EmailDAO(this.UNAME, this.PASSWORD, this.URL);
            this.attachDao = new AttachmentDAO(prop.getProperty("dbUname"), prop.getProperty("dbPassword"));
        } catch (SQLException ex) {
            LOG.error(ex.getMessage());
        }
        initTopSplit();
        initLeftPane();
        initBottomSplit();
        setTableControllerToTree(this.tableController);
        try {
            tableController.displayTheTable("Inbox");

            treeController.displayTree();
        } catch (SQLException ex) {
            LOG.info("Didnt Executed " + ex.getMessage());
        }
    }

    @FXML
    public void initialize() {

    }

    public void setDao(EmailDAO dao) {
        this.dao = dao;
    }

    private void setTableControllerToTree(EmailTableController table) {
        this.treeController.setEmailTableController(table);
    }

    public void setProperties(Properties prop) {
        this.prop = prop;
    }

     /**
     * EventHandler for the configuration contextmenu item
     * opens the configuration fxml
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    @FXML
    public void configureProperties(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/configuration.fxml"));
            AnchorPane configuration = (AnchorPane) loader.load();
            ConfigurationController configController = loader.getController();

            Scene scene = new Scene(configuration);
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(scene);
            secondaryStage.centerOnScreen();
            configController.setStage(secondaryStage);
            configController.setProperties(prop);
            configController.populateForm();
            configController.setRootLayout(this);
            secondaryStage.show();

        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }
    }

     /**
     * EventHandler for the change language context menu item
     * opens the language dialog box
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    @FXML
    public void changeLanguage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/languagePopUp.fxml"));
            AnchorPane language = (AnchorPane) loader.load();
            LanguageController langController = loader.getController();
            Scene scene = new Scene(language);
            Stage stage = new Stage();
            stage.setScene(scene);
            langController.setStage(stage);
            langController.setMain(this.main);
            langController.setProperties(prop);
            stage.centerOnScreen();
            
            stage.show();
        } catch (IOException ex) {
           LOG.error(ex.getMessage());
        }
    }

    public void setMain(MainApp main) {
        this.main = main;
    }

     /**
     * EventHandler for the English button, changes the locale and langauge of the application to English
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    private void initBottomSplit() {
        try {
            bottomSplit.getChildren().clear();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/send_email.fxml"));
            loader.setResources(resources);
            AnchorPane sendEmail = (AnchorPane) loader.load();
            emailController = loader.getController();
            emailController.setEmailDAO(dao);
            emailController.setProperties(prop);
            bottomSplit.getChildren().add(sendEmail);
        } catch (IOException ex) {
            LOG.error("initLeftPane failed ");
        }
    }

     /**
     * Switches the bottom split
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    private void switchBottomSplit() {
        bottomSplit.getChildren().clear();
        AnchorPane email = this.emailController.getEmailPane();
        bottomSplit.getChildren().add(email);
    }

     /**
     * method to initialize the top right anchor pane
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    private void initTopSplit() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);

            loader.setLocation(MainApp.class.getResource("/fxml/emailTable.fxml"));

            AnchorPane table = (AnchorPane) loader.load();
            tableController = loader.getController();
            tableController.setRootController(this);
            tableController.setEmailDao(dao);
            tableController.setProperties(this.prop);
            topSplit.getChildren().add(table);
        } catch (IOException ex) {
            LOG.error(ex + "");
        }
    }
     /**
     * method to initialize the top left anchor pane
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    private void initLeftPane() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/emailTree.fxml"));

            AnchorPane view = (AnchorPane) loader.load();

            treeController = loader.getController();
            treeController.setEmailDao(dao);
            leftPane.getChildren().add(view);
        } catch (IOException ex) {
            LOG.error("initLeftPane failed ");
        }
    }

     /**
     * method that sets up the reply form
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    public void replyEmail(EmailFXBean fx) {
        switchBottomSplit();
        emailController.setTo(fx.getFrom());
        emailController.setSubject("RE: " + fx.getSubject());
        emailController.setEmaiLEditor(fx.getHtmlMsg() + "<br/><br/><br/>");
        this.isComposing = true;
        compose.setText(resources.getString("sendEmail"));
    }

     /**
     * method that sets up the forward form
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    public void fwdEmail(EmailFXBean fx) {
        switchBottomSplit();
        emailController.setTo(fx.getFrom());
        emailController.setSubject("FWD: " + fx.getSubject());
        emailController.setEmaiLEditor(fx.getHtmlMsg() + "<br/><br/><br/>");
        this.isComposing = true;
        compose.setText(resources.getString("sendEmail"));
    }

     /**
     * method that changes the bottom pane with another view
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    public void changeBottomPane(AnchorPane view) {
        bottomSplit.getChildren().remove(0);
        bottomSplit.getChildren().add(view);
        this.isComposing = false;
        this.viewController.setRootController(this);
        this.viewController.setAttachDAO(attachDao);
        compose.setText(resources.getString("compose"));
    }

     /**
     * Event handler to create a folder
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    public void createFolder(ActionEvent action) {
        FolderDAO folderDao = new FolderDAO(URL, UNAME, PASSWORD);
        try {
            folderDao.create(folderName.getText());
            this.treeController.displayTree();
            folderName.clear();
        } catch (SQLException ex) {
            LOG.error(ex.getMessage());
        }
    }

    public void setViewController(ViewController view) {
        this.viewController = view;
    }

     /**
     * Event handler for composing and sending emails
     *
     * @param event - ActionEvent
     * @version 1.0.0
     */
    public void composeEmail(ActionEvent action) {
        if (this.isComposing) {
            emailController.composeEmail(action);
            sendEmail();

        } else {
            bottomSplit.getChildren().remove(0);
            switchBottomSplit();
            compose.setText(resources.getString("sendEmail"));
            this.isComposing = true;
        }
    }


    private void sendEmail() {
        LOG.info("Email was sent!");
        initBottomSplit();
    }
}
