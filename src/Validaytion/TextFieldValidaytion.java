package Validaytion;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TextFieldValidaytion {
    public static boolean isTextFieldNotEmpty(TextField tf){
        boolean b = false;
        if(tf.getText().length() != 0 || !tf.getText().isEmpty()){
            b = true;
        }
        return b;
    }

    public static boolean isTextFieldNotEmpty(TextField tf, Label lb, String errorMsg){
        boolean b = true;
        String msg = null;
        tf.getStyleClass().remove("error");
        if(!isTextFieldNotEmpty(tf)){
            b = false;
            msg = errorMsg;
            tf.getStyleClass().add("error");
        }
        lb.setText(msg);
        return b;
    }
}
