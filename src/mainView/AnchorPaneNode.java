package mainView;

import ViewDay.DayController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;

public class AnchorPaneNode extends AnchorPane {
    private LocalDate date;
    public void loadHTML(){
        DayController.day = date;
        this.getScene().getWindow().hide();
        Stage stage = new Stage();
        URL url = null;
        try {
            url = new File("src/ViewDay/Day.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public AnchorPaneNode (Node... Children){
        super(Children);
        this.setOnMouseClicked(e-> loadHTML());
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
