package mainView;

import Dialog.AlertDialog;
import TimeAlarm.AlarmController;
import TimeAlarm.AlarmEvent;
import TimeAlarm.CurrentTime;
import TimeAlarm.Time;
import ViewDay.DayController;
import Week.WeekController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class mainViewController implements Initializable {
    @FXML
    private DatePicker sCalendar;
    @FXML
    private AnchorPane MAIN;
    @FXML
    private GridPane myGridPane;
    @FXML
    private Text calendarTitle;
    @FXML
    private VBox myVbox;
    @FXML
    private CheckBox SK;
    @FXML
    private CheckBox SN;
    @FXML
    private CheckBox LH;
    @FXML
    private CheckBox NL;
    @FXML
    private ImageView reset;
    @FXML
    private Button resertBt;
    @FXML
    ComboBox<String> kindCalendar;
    @FXML
    private Button Search;




    public YearMonth currentYearMonth;
    private ArrayList<AnchorPaneNode> allCalendarDays = new ArrayList<>(35);
    ObservableList<String> listKind = FXCollections.observableArrayList("Tháng", "Ngày","Tuần");
    private Connection con = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private String [] repeatName = {"Hàng ngày","Hàng tuần","Hàng tháng","Hàng năm"};


    public void showalways(){
        sCalendar.show();
    }

    public void popularCalendar(YearMonth yearMonth){
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(),yearMonth.getMonth(),1);
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY")){
            calendarDate = calendarDate.minusDays(1);
        }
        for(AnchorPaneNode ap: allCalendarDays){
            if(ap.getChildren().size()!=0){
                ap.getChildren().clear();
            }
            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            txt.resize(100,100);
            ap.setDate(calendarDate);
            ap.setTopAnchor(txt, 10.0);
            ap.setLeftAnchor(txt, 50.0);
            ap.getChildren().add(txt);
            loadDateEvent(ap);
            calendarDate = calendarDate.plusDays(1);
        }
        calendarTitle.setText(yearMonth.getMonth().toString() + " " + String.valueOf(yearMonth.getYear()));
    }

    public void previousMonth(){
        currentYearMonth = currentYearMonth.minusMonths(1);
        popularCalendar(currentYearMonth);
    }
    public void nextMonth(){
        currentYearMonth = currentYearMonth.plusMonths(1);
        popularCalendar(currentYearMonth);
    }

    public void choseDatePicker(){
        LocalDate date = sCalendar.getValue();
        int monthNeed = date.getMonthValue();
        int monthCurrent = currentYearMonth.getMonthValue();
        int step;
        if(monthNeed>monthCurrent){
            step = monthNeed - monthCurrent;
            while(step>0){
                currentYearMonth = currentYearMonth.plusMonths(1);
                step--;
            }
        }else{
            step = monthCurrent - monthNeed;
            while(step>0){
                currentYearMonth = currentYearMonth.minusMonths(1);
                step--;
            }
        }
        int yearNeed = date.getYear();
        int yearCurrent = currentYearMonth.getYear();
        if(yearNeed>yearCurrent){
            //System.out.println("o day");
            step = yearNeed - yearCurrent;
            while(step>0){
                currentYearMonth = currentYearMonth.plusYears(1);
                step--;
            }
        }else{
            step = yearCurrent - yearNeed;
            while(step>0){
                currentYearMonth = currentYearMonth.minusYears(1);
                step--;
            }
        }
        popularCalendar(currentYearMonth);
        System.out.println(currentYearMonth.toString());

    }

    public void createEvent() throws IOException {
        Stage event = new Stage();
        URL url = new File("src/Event/eventView.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        event.getIcons().add(new Image("Image/icons8-add-96.png"));
        event.setScene(scene);
        event.show();

    }

    public boolean checkDataRepeat(LocalDate dateAp, LocalDate dateStartRepeat,LocalDate dateFinishRepeat ,String repeat){
        if(repeat.equals("Hàng ngày")) return true;
        if(repeat.equals("Hàng tuần")){
            int steps = dateFinishRepeat.getDayOfYear()-dateStartRepeat.getDayOfYear();
            //System.out.println("Start");
            //System.out.println(dateAp);
            while(steps>=0) {
                //System.out.println(steps);
                if (dateAp.getDayOfWeek() == dateStartRepeat.getDayOfWeek()){
                    //System.out.println("true");
                    return true;
                }
                else{
                    steps--;
                    dateStartRepeat = dateStartRepeat.plusDays(1);
                }
            }
            return false;
        }
        if(repeat.equals("Hàng tháng")){
            if(dateAp.getDayOfMonth() >= dateStartRepeat.getDayOfMonth() && dateAp.getDayOfMonth()<= dateFinishRepeat.getDayOfMonth()) return true;
            return false;
        }
        if(repeat.equals("Hàng năm")){
            if(dateAp.getDayOfYear() >= dateStartRepeat.getDayOfYear() && dateAp.getDayOfYear() <= dateFinishRepeat.getDayOfYear()) return true;
            return false;
        }
        return false;
    }


    public void loadDateEvent(AnchorPaneNode ap){
        VBox vb = new VBox();
        vb.setPrefSize(100, 30);
        Label labeltest1 = new Label();
        Label labeltest2 = new Label();
        vb.getChildren().addAll(labeltest1, labeltest2);
        if(SK.isSelected()) {
            String sql = "select * from Event where dateStart <= '" + ap.getDate().toString() + "' and '" + ap.getDate().toString()
                    + "'<=dateFinish and repeated = N'Không lặp lại' order by timeStart";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    Label label = new Label();
                    label.setPrefSize(100, 50);
                    label.setAlignment(Pos.CENTER);
                    label.setText(rs.getString(1));
                    label.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                    vb.getChildren().addAll(label);
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(int i = 0; i<4; i++){
                sql = "select * from Event where repeated = N'"+repeatName[i]+"' order by timeStart";

                try {
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        if(checkDataRepeat(ap.getDate(), rs.getDate(2).toLocalDate(),
                                rs.getDate(3).toLocalDate(), repeatName[i] )) {
                            Label label = new Label();
                            label.setPrefSize(100, 50);
                            label.setAlignment(Pos.CENTER);
                            label.setText(rs.getString(1));
                            label.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                            vb.getChildren().addAll(label);
                        }
                    }
                    rs = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if(NL.isSelected()) {
            String sql = "select * from Holiday where dateStart <= '" + ap.getDate().toString() +  "' and '" + ap.getDate().toString() + "'<=dateFinish and repeated = N'Không lặp lại' order by timeStart";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    Label label = new Label();
                    label.setPrefSize(100, 50);
                    label.setAlignment(Pos.CENTER);
                    label.setText(rs.getString(1));
                    label.setBackground(new Background(new BackgroundFill(Color.FUCHSIA, CornerRadii.EMPTY, Insets.EMPTY)));
                    vb.getChildren().addAll(label);
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(int i = 0; i<4; i++){
                sql = "select * from Holiday where repeated = N'"+repeatName[i]+"' order by timeStart";

                try {
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        if(checkDataRepeat(ap.getDate(), rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeatName[i] )) {
                            Label label = new Label();
                            label.setPrefSize(100, 50);
                            label.setAlignment(Pos.CENTER);
                            label.setText(rs.getString(1));
                            label.setBackground(new Background(new BackgroundFill(Color.FUCHSIA, CornerRadii.EMPTY, Insets.EMPTY)));
                            vb.getChildren().addAll(label);
                        }
                    }
                    rs = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if(SN.isSelected()) {
            String sql = "select * from Birthday where dateStart <= '" + ap.getDate().toString() + "' and '" + ap.getDate().toString() + "'<=dateFinish and repeated = N'Không lặp lại' order by timeStart";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    Label label = new Label();
                    label.setPrefSize(100, 50);
                    label.setAlignment(Pos.CENTER);
                    label.setText(rs.getString(1));
                    label.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    vb.getChildren().addAll(label);
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(int i = 0; i<4; i++){
                sql = "select * from Birthday where repeated = N'"+repeatName[i]+"' order by timeStart";

                try {
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        if(checkDataRepeat(ap.getDate(), rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeatName[i] )) {
                            Label label = new Label();
                            label.setPrefSize(100, 50);
                            label.setAlignment(Pos.CENTER);
                            label.setText(rs.getString(1));
                            label.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                            vb.getChildren().addAll(label);
                        }
                    }
                    rs = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if(LH.isSelected()) {
            String sql = "select * from Appointment where dateStart <= '" + ap.getDate().toString() + "' and '" + ap.getDate().toString() + "'<=dateFinish and repeated = N'Không lặp lại' order by timeStart";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    Label label = new Label();
                    label.setPrefSize(100, 50);
                    label.setAlignment(Pos.CENTER);
                    label.setText(rs.getString(1));
                    label.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
                    vb.getChildren().addAll(label);
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(int i = 0; i<4; i++){
                sql = "select * from Appointment where repeated = N'"+repeatName[i]+"' order by timeStart";

                try {
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        if(checkDataRepeat(ap.getDate(), rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeatName[i] )) {
                            Label label = new Label();
                            label.setPrefSize(100, 50);
                            label.setAlignment(Pos.CENTER);
                            label.setText(rs.getString(1));
                            label.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
                            vb.getChildren().addAll(label);
                        }
                    }
                    rs = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        vb.setAlignment(Pos.CENTER);
        vb.setSpacing(2);
        ap.getChildren().add(vb);
    }




    public void show(){
        popularCalendar(currentYearMonth);
    }

    public void reset(){
        popularCalendar(currentYearMonth);
        AlarmEvent.resert = true;
        AlarmEvent.AlertAlarm();

    }
    public void loadHTML(String sURL){
        kindCalendar.getScene().getWindow().hide();
        Stage stage = new Stage();
        URL url = null;
        try {
            url = new File(sURL).toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.getIcons().add(new Image("Image/icons8-calendar-96.png"));
            stage.show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void searchEvent(){
        loadHTML("src/Search/Search.fxml");
    }

    public void changeToHTML(){
        String name = kindCalendar.getValue();
        if(name.equals("Tháng")){
            loadHTML("src/mainView/MainView.fxml");
        }else if(name.equals("Ngày")){
            DayController.day = LocalDate.now();
            loadHTML("src/ViewDay/Day.fxml");

        }else if(name.equals("Tuần")){
            loadHTML("src/Week/week.fxml");
        }
    }



    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        con = Database.DBConnection.CalendarConnection();

        sCalendar.show();
        Image img = new Image("Image/reset-filter.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(30);
        view.setFitWidth(30);
        resertBt.setGraphic(view);
        currentYearMonth = YearMonth.now();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                AnchorPaneNode ap = new AnchorPaneNode();
                ap.setPrefSize(100,30);
                myGridPane.add(ap,j,i);
                allCalendarDays.add(ap);
            }
        }
        popularCalendar(currentYearMonth);
        kindCalendar.setItems(listKind);
        kindCalendar.setValue("Tháng");
        //alarmDay();


    }
}
