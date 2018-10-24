/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.sendEmail;

import com.emailsystem.persistence.EmailDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.web.HTMLEditor;
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
    
    private final EmailDAO dao = new EmailDAO();
    
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
    
}
