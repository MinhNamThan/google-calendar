package Dialog;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class AlertDialog {
    public static void display(String title, String message){
        Stage windown = new Stage();
        windown.setTitle(title);
        windown.setMinHeight(200);
        windown.setMinWidth(500);

        Label label = new Label();
        label.setText(message);
        label.setMinHeight(100);
        final double MAX_FONT_SIZE = 20.0; // define max font size you need
        label.setFont(new Font(MAX_FONT_SIZE)); // set to Label

        Button buttonOK = new Button("OK");
        buttonOK.setPrefSize(100,50);
        buttonOK.setOnAction(e->windown.close());
        VBox layout = new VBox(5);
        layout.getChildren().addAll(label,buttonOK);
        layout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(layout);
        windown.setScene(scene);
        windown.showAndWait();
    }
}
