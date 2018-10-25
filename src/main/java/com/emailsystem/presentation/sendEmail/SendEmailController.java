/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.sendEmail;

import com.emailsystem.application.MainApp;
import com.emailsystem.data.AttachmentBean;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.presentation.attachmentView.AttachmentController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GamingDanik
 */
public class SendEmailController {
    
    private final static Logger LOG = LoggerFactory.getLogger(SendEmailController.class);
    
    @FXML
    private TextField to;
    @FXML
    private TextField subject;
    @FXML
    private TextField cc;
    @FXML
    private HTMLEditor emailEditor;
    @FXML
    private Hyperlink attachLink;
    @FXML
    private AnchorPane emailPane;
    @FXML
    private ResourceBundle resources;
    
    private List<AttachmentBean> attachList = new ArrayList();
    private int counter = 0;
    private AttachmentController attachController;
    private double lastLayoutX = 103;
    
    
    @FXML
    private void initialize() {
        
    }
    
    @FXML
    private void dragOver(DragEvent event) {
        /* data is dragged over the target */
        LOG.debug("onDragOver");

        // Accept it only if it is not dragged from the same control and if it
        // has a string data
        if (event.getGestureSource() != emailEditor && event.getDragboard().hasString()) {
            // allow for both copying and moving, whatever user chooses
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }
    
    @FXML
    private void dragDropped(DragEvent event) {
        LOG.debug("onDragDropped");
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            emailEditor.setHtmlText(db.getString());
            success = true;
        }
        //let the source know whether the string was successfully transferred
        // and used
        event.setDropCompleted(success);

        event.consume();
    }
    
    public void setTo(String email) {
        this.to.setText(email);
    }
    
    public void setSubject(String fwdSubject) {
        this.subject.setText(fwdSubject);
    }
    
    
    public void addAttach(ActionEvent action) {
      
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(to.getScene().getWindow());
        AttachmentBean attachmentBean = new AttachmentBean();
        try {
            attachmentBean.setName(file.getPath().substring(file.getPath().lastIndexOf("\\") + 1));
            attachmentBean.setAttach(Files.readAllBytes(file.toPath()));
            attachList.add(attachmentBean);
        } catch (IOException ex) {
            LOG.warn("Didn't choose file");
        }
        Hyperlink link = new Hyperlink();
        link.setText(attachmentBean.getName());
        link.setLayoutX(lastLayoutX);
        link.setLayoutY(487);
        link.setPrefWidth(100);
        link.setFont(new Font(14));
        link.setId(counter + "");
        this.counter++;
        link.setOnAction(e -> openAttachView(e));
        emailPane.getChildren().add(link);
        this.lastLayoutX += link.getLayoutBounds().getWidth() + 100;
    }
    
    public void openAttachView(ActionEvent action) {
        int clickedIndex = Integer.parseInt(((Hyperlink)action.getSource()).getId());
            FXMLLoader loader = new FXMLLoader();
            
            loader.setLocation(MainApp.class.getResource("/fxml/attachView.fxml"));
            
            AnchorPane attachView = null;
        try {
            attachView = (AnchorPane)loader.load();
        } catch (IOException ex) {
            LOG.warn("Couldn't find resource attachView " + ex.getMessage());
        }
            attachController = loader.getController();
            if (attachList.size() > 0)
                attachController.loadImage(attachList.get(clickedIndex).getAttach());
            Stage primaryStage = new Stage();
            Scene scene = new Scene(attachView);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
    }
   
}
