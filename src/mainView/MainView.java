package mainView;

import TimeAlarm.AlarmEvent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.stage.Stage;



public class MainView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        primaryStage.setTitle("Calendar");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image("Image/icons8-calendar-96.png"));
        primaryStage.show();
        AlarmEvent.alarmDay();
        AlarmEvent.alarmMonth();
        AlarmEvent.AlertAlarm();
        AlarmEvent.deleteOldEvent();
    }
}
