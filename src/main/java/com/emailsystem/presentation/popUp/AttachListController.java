/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.popUp;

import com.emailsystem.application.MainApp;
import com.emailsystem.data.AttachmentBean;
import com.emailsystem.presentation.attachmentView.AttachmentController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author 1633867
 */
public class AttachListController {

    @FXML
    ListView<Hyperlink> listView = new ListView<Hyperlink>();
    @FXML
    private ResourceBundle resources;

    private ObservableList<Hyperlink> items;
    private List<AttachmentBean> attachList;

    @FXML
    public void initialize() {

    }
    /**
     * Method that displays a list of attachments that were sent
     *
     * @param items - a list of attachmentbeans 
     * @version 1.0.0
     */
    public void displayList(List<AttachmentBean> items) {
        List<Hyperlink> nameList = new ArrayList();
        for (int i = 0; i < items.size(); i++) {
            Hyperlink link = new Hyperlink();
            link.setText(items.get(i).getName());
            link.setId(i + "");
            nameList.add(link);
            link.setOnAction((e) -> loadAttach(e));
        }
        this.items = FXCollections.observableArrayList(nameList);
        listView.setItems(this.items);
        attachList = items;
    }
     /**
     * Method to load individual attachments from the list
     *
     * @param EmailBean bean
     * @version 1.0.0
     */
    private void loadAttach(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/attachView.fxml"));
            AnchorPane attachView = (AnchorPane) loader.load();
            AttachmentController attachController = loader.getController();
            int index = Integer.parseInt(((Hyperlink) event.getSource()).getId());
            attachController.loadImage(attachList.get(index).getAttach());
            Scene scene = new Scene(attachView);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(AttachListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
