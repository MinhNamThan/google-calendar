package artificialAlert;

import TimeAlarm.AlarmEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.text.Text;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class AlertController implements Initializable {
    @FXML
    private Text textInfo;
    @FXML
    private Button okBT;
    public static String s = null;
    public void Setup(){
        textInfo.setText(s);

    }
    public void exit(){
        okBT.getScene().getWindow().hide();
    }
    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        Setup();
    }
}
