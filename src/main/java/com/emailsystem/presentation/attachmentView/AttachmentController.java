package com.emailsystem.presentation.attachmentView;

import com.emailsystem.application.MainApp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class AttachmentController {
    
    @FXML
    private ImageView attach;
    
    @FXML
    private void initialize() {
        
    }
    
     /**
     * Method to load an image to the ImageView
     *
     * @param byte[] - the image byte stream
     * @version 1.0.0
     */
    public void loadImage(byte[] byteStream) {
        InputStream is = new ByteArrayInputStream(byteStream);
        attach.setImage(new Image(is));
    }
    

    
}