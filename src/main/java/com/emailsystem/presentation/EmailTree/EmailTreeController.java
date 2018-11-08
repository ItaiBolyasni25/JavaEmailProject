/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.EmailTree;

import com.emailsystem.application.MainApp;
import com.emailsystem.business.MailModule;
import com.emailsystem.data.EmailBean;
import com.emailsystem.data.EmailFXBean;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.persistence.FolderDAO;
import com.emailsystem.presentation.popUp.FolderController;
import com.emailsystem.presentation.rootController.RootLayoutController;
import com.emailsystem.presentation.table.EmailTableController;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableRow;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GamingDanik
 */
public class EmailTreeController {

    private final static Logger LOG = LoggerFactory.getLogger(EmailTreeController.class);

    private EmailDAO dao;
    private EmailTableController emailTable;

    @FXML
    private TreeView<EmailFXBean> treeView;
    @FXML
    private ResourceBundle resources;

     /**
     * Method that initializes the TreeView
     *
     * @param folderName - the name of the folder to be created
     * @return int - the amount of rows affected
     * @version 1.0.0
     */
    @FXML
    private void initialize() {
        EmailFXBean rootFolder = new EmailFXBean();
        rootFolder.setFolderName(resources.getString("folders"));

        treeView.setRoot(new TreeItem<>(rootFolder));

        // This cell factory is used to choose which field in the FihDta object
        // is used for the node name
        treeView.setCellFactory((e) -> new TreeCell<EmailFXBean>() {
            @Override
            protected void updateItem(EmailFXBean item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item != null) {
                    setText(item.getFolderName());
                    setGraphic(getTreeItem().getGraphic());
                } else {
                    setText("");
                    setGraphic(null);
                }
                
                //Prevent dragging emails to the sent folder
                setOnDragOver(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        if (event.getDragboard().hasString() && !item.getFolderName().equalsIgnoreCase("Sent")) {
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                            LOG.info(item.getId() + "");
                        }
                    }
                });
                //set drop event
                setOnDragDropped(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        try {
                            dao.updateEmailFolder(item.getFolderName(), Integer.parseInt(event.getDragboard().getString()));
                            emailTable.displayTheTable("Inbox");
                        } catch (SQLException ex) {
                            java.util.logging.Logger.getLogger(EmailTreeController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        });

    }

     /**
     * EventHandler for the Delete ContextMenu item
     *
     * @param event the ActionEvent
     * @version 1.0.0
     */
    @FXML
    public void onDelete(ActionEvent event) {
        try {
            FolderDAO folders = dao.getFolderDAO();

            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/deletePopUp.fxml"));

            AnchorPane view = (AnchorPane) loader.load();
            FolderController folderController = loader.getController();
            folderController.setFolderDAO(folders);
            folderController.setTree(this);
            Scene scene = new Scene(view);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.centerOnScreen();
            folderController.setStage(stage);
            stage.show();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(EmailTreeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     /**
     * EventHandler for the Rename ContextMenu item
     *
     * @param ActionEvent event
     * @version 1.0.0
     */
    @FXML
    public void onRename(ActionEvent event) {
        try {
            FolderDAO folders = dao.getFolderDAO();

            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(MainApp.class.getResource("/fxml/renamePopUp.fxml"));

            AnchorPane view = (AnchorPane) loader.load();
            FolderController folderController = loader.getController();
            folderController.setFolderDAO(folders);
            folderController.setTree(this);
            Scene scene = new Scene(view);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.centerOnScreen();
            folderController.setStage(stage);
            stage.show();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(EmailTreeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     /**
     * Method that queries the database and sets up all the items on the TreeView
     * @throws SQLException
     * @version 1.0.0
     */
    public void displayTree() throws SQLException {
        treeView.getRoot().getChildren().clear();
        // Retrieve the list of fish
        ObservableList<EmailFXBean> folders = dao.findAllFoldersFX();

        // Build an item for each fish and add it to the root
        if (folders != null) {
            folders.stream().map((fd) -> new TreeItem<>(fd)).map((item) -> {
                ImageView folderImage = new ImageView(getClass().getResource("/images/folder.png").toExternalForm());
                folderImage.setFitHeight(20);
                folderImage.setFitWidth(20);
                item.setGraphic(folderImage);
                return item;
            }).forEachOrdered((item) -> {
                treeView.getRoot().getChildren().add(item);
            });
        }
        // Open the tree
        treeView.getRoot().setExpanded(true);
        // Listen for selection changes and show the fishData details when
        // changed.
        treeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changeEmailSelection(newValue));
    }

    public void setEmailTableController(EmailTableController table) {
        this.emailTable = table;
    }

    public void receiveEmails(TreeItem<EmailFXBean> fxBean) throws SQLException {

        this.emailTable.displayTheTable(fxBean.getValue().getFolderName());
    }

     /*
     * Method that changes the view of the table based on the selected folder
     *
     * @param fxBean - a tree item to know which folder was chosen
     * @return int - the amount of rows affected
     * @version 1.0.0
     */
    public void changeEmailSelection(TreeItem<EmailFXBean> fxBean) {
        try {
            receiveEmails(fxBean);
        } catch (SQLException ex) {
            LOG.error("Unexpected Error " + ex.getMessage());
        }
    }

    public void setEmailDao(EmailDAO dao) {
        this.dao = dao;
    }

}
