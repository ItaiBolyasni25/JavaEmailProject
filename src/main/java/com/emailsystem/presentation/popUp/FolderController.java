/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.popUp;

import com.emailsystem.persistence.FolderDAO;
import com.emailsystem.presentation.EmailTree.EmailTreeController;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author GamingDanik
 */
public class FolderController {

    @FXML
    private TextField folderToDelete;
    @FXML
    private TextField oldName;
    @FXML
    private TextField newName;
    @FXML
    private Text errorDelete;
    @FXML
    private Text errorRename;

    private FolderDAO folders;
    private Stage stage;
    private EmailTreeController tree;

    @FXML
    public void initialize() {

    }

    @FXML
    public void onDelete(ActionEvent event) {
        try {
            folders.delete(folders.getId(folderToDelete.getText()));
            stage.close();
            tree.displayTree();
        } catch (SQLException ex) {
            errorDelete.setText(ex.getMessage());
        }
    }

    @FXML
    public void onRename(ActionEvent event) {
        try {
            folders.update(oldName.getText(), newName.getText());
            stage.close();
            tree.displayTree();
        } catch (SQLException ex) {
            errorRename.setText(ex.getMessage());
        }
    }

    public void setFolderDAO(FolderDAO folders) {
        this.folders = folders;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setTree(EmailTreeController tree) {
        this.tree = tree;
    }
}
