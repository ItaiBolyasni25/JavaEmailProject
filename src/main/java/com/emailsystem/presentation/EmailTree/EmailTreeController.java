/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.EmailTree;

import com.emailsystem.data.FxBeanFactory;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.persistence.FolderDAO;
import com.emailsystem.presentation.table.EmailTableController;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GamingDanik
 */
public class EmailTreeController {

    private final static Logger LOG = LoggerFactory.getLogger(EmailTreeController.class);

    private EmailDAO dao = new EmailDAO();


    @FXML
    private TreeView<FxBeanFactory> treeView;

    // Resource bundle is injected when controller is loaded
    @FXML
    private ResourceBundle resources;

    @FXML
    private void initialize() {
        // We need a root node for the tree and it must be the same type as all
        // nodes
        FxBeanFactory rootFolder = new FxBeanFactory();
        LOG.info("did this!!");
        // The tree will display common name so we set this for the root
        // Because we are using i18n the root name comes from the resource
        // bundle
        rootFolder.setFolderName("Folders");

        treeView.setRoot(new TreeItem<>(rootFolder));

        // This cell factory is used to choose which field in the FihDta object
        // is used for the node name
        treeView.setCellFactory((e) -> new TreeCell<FxBeanFactory>() {
            @Override
            protected void updateItem(FxBeanFactory item, boolean empty) {
                super.updateItem(item, empty);
                LOG.info("did this!!");
                if (item != null) {
                    setText(item.getFolderName());
                    setGraphic(getTreeItem().getGraphic());
                } else {
                    setText("");
                    setGraphic(null);
                }
            }
        });
        LOG.info("did this!!");
        // We are going to drag and drop
        treeView.setOnDragDetected((MouseEvent event) -> {
            /* drag was detected, start drag-and-drop gesture */
            LOG.debug("onDragDetected");

            /* allow any transfer mode */
            Dragboard db = treeView.startDragAndDrop(TransferMode.ANY);

            /* put a string on dragboard */
            ClipboardContent content = new ClipboardContent();
            content.putString(treeView.getSelectionModel().getSelectedItem().getValue().toString());

            db.setContent(content);

            event.consume();
        });
    }
    
    @FXML
    private void dragDetected(MouseEvent event) {
        /* drag was detected, start drag-and-drop gesture */
        LOG.debug("onDragDetected");

        /* allow any transfer mode */
        Dragboard db = treeView.startDragAndDrop(TransferMode.ANY);

        /* put a string on dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(treeView.getSelectionModel().getSelectedItem().getValue().toString());

        db.setContent(content);

        event.consume();
    }
    
    public void displayTree() throws SQLException {
        
        // Retrieve the list of fish
        ObservableList<FxBeanFactory> folders = dao.findAllFoldersFX();

        // Build an item for each fish and add it to the root
        if (folders != null) {
            folders.stream().map((fd) -> new TreeItem<>(fd)).map((item) -> {
                ImageView folderImage = new ImageView(getClass().getResource("/images/thumbsup.jpg").toExternalForm());
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
//        fishFXTreeView.getSelectionModel().selectedItemProperty()
//                .addListener((observable, oldValue, newValue) -> showFishDetailsTree(newValue));
    }
}
