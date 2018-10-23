/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.presentation.viewEmail;

import com.emailsystem.data.FxBeanFactory;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 *
 * @author GamingDanik
 */
public class ViewController {
    @FXML
    private Text to;
    @FXML
    private Text subject;
    @FXML
    private Text message;
    @FXML
    private Text from;
    
    @FXML
    private void initialize() {
        
    }
    
    public void loadEmail(FxBeanFactory fx) {
        this.from.setText(fx.getFrom());
        this.subject.setText(fx.getSubject());
        this.message.setText(fx.getTextMsg());
    }
}
