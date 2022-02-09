package Week;

import OperationOfEvent.OOEcontroller;
import ViewDay.DayController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

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
import java.util.ResourceBundle;

public class WeekController implements Initializable {
    @FXML
    private ComboBox<String> kindCalendar;
    @FXML
    private DatePicker sCalendar;
    @FXML
    private GridPane InfoWeek;
    @FXML
    private Text calendarTitle;
    @FXML
    private GridPane myEvent;
    @FXML
    private RadioButton SK;
    @FXML
    private RadioButton SN;
    @FXML
    private RadioButton LH;
    @FXML
    private RadioButton NL;
    @FXML
    private Button resertBt;


    ObservableList<String> listKind = FXCollections.observableArrayList("Tháng","Tuần", "Ngày");
    private Connection con = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    public static LocalDate dateOfWeek = LocalDate.now();
    private String [] repeatName = {"Hàng ngày","Hàng tuần","Hàng tháng","Hàng năm"};

    public void createEvent() throws IOException {
        Stage event = new Stage();
        URL url = new File("src/Event/eventView.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        event.getIcons().add(new Image("Image/icons8-add-96.png"));
        event.setScene(scene);
        event.show();
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
            DayController.day = dateOfWeek;
            loadHTML("src/ViewDay/Day.fxml");
        }else if(name.equals("Tuần")){
            loadHTML("src/Week/week.fxml");
        }
    }
    public void showalways(){
        sCalendar.show();
    }

    public void loadDayView(LocalDate dateCur){
        DayController.day = dateCur;
        loadHTML("src/ViewDay/Day.fxml");
    }

    public void popularWeek(LocalDate date){
        while (!date.getDayOfWeek().toString().equals("SUNDAY")){
            date = date.minusDays(1);
        }
        if(myEvent.getChildren().size()!=0){
            myEvent.getChildren().clear();
        }

        for(int i = 0; i< 7 ;i++){

            LocalDate dateCur = date;
            Button bt = new Button();
            bt.setText(String.valueOf(date.getDayOfMonth()));
            bt.setPrefSize(50,50);
            Font f = new Font(20);
            bt.setFont(f);
            bt.setStyle(" -fx-background-radius: 30; -fx-color: #117fdf;");
            bt.setOnMouseClicked(e->loadDayView(dateCur));
            InfoWeek.add(bt,i,1);
            InfoWeek.setHalignment(bt, HPos.CENTER);
            loadEvent(date,i);
            date = date.plusDays(1);
        }
        calendarTitle.setText(date.getMonth().toString() + " " + String.valueOf(date.getYear()));
    }

    public void nextWeek(){
        dateOfWeek = dateOfWeek.plusDays(7);
        popularWeek(dateOfWeek);
    }
    public void previousWeek(){
        dateOfWeek = dateOfWeek.minusDays(7);
        popularWeek(dateOfWeek);
    }
    public void chooseDayOfWeek(){
        dateOfWeek = sCalendar.getValue();
        popularWeek(dateOfWeek);
    }
    public void loadInfo(String kind, String Title){
        OOEcontroller.kindEvent = kind;
        OOEcontroller.nameTitle = Title;
        loadHTML("src/OperationOfEvent/OOE.fxml");
    }

    public float setUpSizePane(String time){
        int tmp = time.indexOf(':');
//        System.out.println(time.substring(0,tmp));
        int hour = Integer.parseInt(time.substring(0,tmp));
        int minute = Integer.parseInt(time.substring(tmp+1));
//        System.out.println(hour);
//        System.out.println(hour*27+(float)(0.45*minute));
        return hour*27+(float)(0.45*minute);
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

    public boolean daySideRepeat(LocalDate ap, LocalDate dayStart, String repeat){
        if(repeat.equals("Hàng ngày")) return true;
        if(repeat.equals("Hàng tuần")){
            if(ap.getDayOfWeek() == dayStart.getDayOfWeek()) return true;
            return false;
        }
        if(repeat.equals("Hàng tháng")){
            if(ap.getDayOfMonth() == dayStart.getDayOfMonth()) return true;
            return false;
        }
        if(repeat.equals("Hàng năm")){
            if(ap.getDayOfYear() == dayStart. getDayOfYear()) return true;
            return false;
        }
        return false;
    }

    public void loadEvent(LocalDate date, int i){
        float oderView = 6;
        int startx = 0;
        AnchorPane  a = new AnchorPane();
        int  f = -1;
        myEvent.add(a,i,0);
        if(SK.isSelected()) {
            String sql = "select * from Event where dateStart <= '" + date.toString() + "' and '" + date.toString() +
                    "'<=dateFinish and repeated = N'Không lặp lại' order by timeStart";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    VBox pane = new VBox();


                    String title = rs.getString(1);
                    pane.setOnMouseClicked(e -> loadInfo("Event", title));
                    int x = 0, y; // độ dài pane
                    Label label = new Label();
//                    label.setPrefSize(50, 50);
                    label.setAlignment(Pos.CENTER);

                    label.setText(rs.getString(1));
                    String dateStart = rs.getString(2);
                    String dateEnd = rs.getString(3);
                    String timeStart = rs.getString(4);
                    if (date.toString().equals(dateStart)) {
                        x = (int) setUpSizePane(timeStart);
                        pane.setTranslateY(x);

                    }
                    if(x<f){
                        startx += 10;
                        oderView = oderView-1;
                        pane.setViewOrder(oderView);

                    }else{
                        startx  = 0;
                        oderView = 0;
                    }
                    pane.setTranslateX(startx);
                    pane.setPrefWidth(120-startx);
                    pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    if (date.toString().equals(dateEnd)) {

                        String timeFinish = rs.getString(5);

                        y = (int) setUpSizePane(timeFinish);
//                        System.out.println("f: " + f);
                        f = y;

                        pane.setPrefHeight(y - x);
//                        System.out.println(y);
                    }else{
                        f = 647;
                        pane.setPrefHeight(f-x);
                    }
                    Text addresses = new Text("Địa chỉ : " + rs.getString(6));
                    addresses.setTextAlignment(TextAlignment.CENTER);

                    pane.getChildren().addAll(label, addresses);
                    pane.setSpacing(5);
                    pane.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));


                    a.getChildren().add(pane);

                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(int t = 0; t < 4; t++){
                sql = "select * from Event where repeated = N'"+repeatName[t]+"' order by timeStart";
                try {
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        if (checkDataRepeat(date,rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeatName[t])){
                            VBox pane = new VBox();
                            String title = rs.getString(1);
                            pane.setOnMouseClicked(e -> loadInfo("Event", title));
                            int x = 0, y; // độ dài pane
                            Label label = new Label();
//                    label.setPrefSize(50, 50);
                            label.setAlignment(Pos.CENTER);

                            label.setText(rs.getString(1));
                            LocalDate dateStart = rs.getDate(2).toLocalDate();
                            LocalDate dateEnd = rs.getDate(3).toLocalDate();
                            String timeStart = rs.getString(4);

                            if (daySideRepeat(date,dateStart,repeatName[t])) {
                                x = (int) setUpSizePane(timeStart);
                                System.out.println(x);
                                pane.setTranslateY(x);

                            }
                            if(x<f){
                                startx += 10;
                                oderView = oderView-1;
                                pane.setViewOrder(oderView);

                            }else{
                                startx  = 0;
                                oderView = 0;
                            }
                            pane.setTranslateX(startx);
                            pane.setPrefWidth(120-startx);
                            pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            if (daySideRepeat(date,dateEnd,repeatName[t])) {

                                String timeFinish = rs.getString(5);

                                y = (int) setUpSizePane(timeFinish);
                                System.out.println(y);
//                        System.out.println("f: " + f);
                                f = y;

                                pane.setPrefHeight(y - x);
//                        System.out.println(y);
                            }else{
                                f = 647;
                                pane.setPrefHeight(f-x);
                            }

                            Text addresses = new Text("Địa chỉ : " + rs.getString(6));
                            addresses.setTextAlignment(TextAlignment.CENTER);
                            Text describe = new Text(rs.getString(7));
                            describe.setTextAlignment(TextAlignment.CENTER);
                            pane.getChildren().addAll(label, addresses, describe);
                            pane.setSpacing(5);
                            pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            pane.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                            pane.setViewOrder(5);
                            a.getChildren().add(pane);
                        }
                    }
                    rs = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if(SN.isSelected()) {
            String sql = "select * from Birthday where dateStart <= '" + date.toString() + "' and '" + date.toString() + "'<=dateFinish and repeated = N'Không lặp lại' order by timeStart";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    VBox pane = new VBox();
                    String title = rs.getString(1);
                    pane.setOnMouseClicked(e -> loadInfo("Event", title));
                    int x = 0, y; // độ dài pane
                    Label label = new Label();
//                    label.setPrefSize(50, 50);
                    label.setAlignment(Pos.CENTER);

                    label.setText(rs.getString(1));
                    String dateStart = rs.getString(2);
                    String dateEnd = rs.getString(3);
                    String timeStart = rs.getString(4);
                    if (date.toString().equals(dateStart)) {
                        x = (int) setUpSizePane(timeStart);
                        pane.setTranslateY(x);

                    }
                    System.out.println(f);
                    if(x<f){
                        startx += 10;
                        oderView = oderView-1;
                        pane.setViewOrder(oderView);

                    }else{
                        startx  = 0;
                        oderView = 0;
                    }
                    pane.setTranslateX(startx);
                    pane.setPrefWidth(120-startx);
                    pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                    if (date.toString().equals(dateEnd)) {

                        String timeFinish = rs.getString(5);

                        y = (int) setUpSizePane(timeFinish);
                        f = y;

                        pane.setMinHeight(y - x);
//                        System.out.println(y);
                    }else{
                        f = 647;
                        pane.setPrefHeight(f-x);
                    }
//                    System.out.println(x);

                    Text addresses = new Text("Địa chỉ : " + rs.getString(6));
                    addresses.setTextAlignment(TextAlignment.CENTER);

                    pane.getChildren().addAll(label, addresses);
                    pane.setSpacing(5);
                    pane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    a.getChildren().add(pane);
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(int t = 0; t < 4; t++){
                sql = "select * from Birthday where repeated = N'"+repeatName[t]+"' order by timeStart";
                try {
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        if (checkDataRepeat(date,rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeatName[t])){
                            VBox pane = new VBox();
                            String title = rs.getString(1);
                            pane.setOnMouseClicked(e -> loadInfo("Event", title));
                            int x = 0, y; // độ dài pane
                            Label label = new Label();
//                    label.setPrefSize(50, 50);
                            label.setAlignment(Pos.CENTER);

                            label.setText(rs.getString(1));
                            LocalDate dateStart = rs.getDate(2).toLocalDate();
                            LocalDate dateEnd = rs.getDate(3).toLocalDate();
                            String timeStart = rs.getString(4);

                            if (daySideRepeat(date,dateStart,repeatName[t])) {
                                x = (int) setUpSizePane(timeStart);
                                pane.setTranslateY(x);

                            }
                            if(x<f){
                                startx += 10;
                                oderView = oderView-1;
                                pane.setViewOrder(oderView);

                            }else{
                                startx  = 0;
                                oderView = 0;
                            }
                            pane.setTranslateX(startx);
                            pane.setPrefWidth(120-startx);
                            pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            if (daySideRepeat(date,dateEnd,repeatName[t])) {

                                String timeFinish = rs.getString(5);

                                y = (int) setUpSizePane(timeFinish);
//                        System.out.println("f: " + f);
                                f = y;

                                pane.setPrefHeight(y - x);
//                        System.out.println(y);
                            }else{
                                f = 647;
                                pane.setPrefHeight(f-x);
                            }

                            Text addresses = new Text("Địa chỉ : " + rs.getString(6));
                            addresses.setTextAlignment(TextAlignment.CENTER);
                            Text describe = new Text(rs.getString(7));
                            describe.setTextAlignment(TextAlignment.CENTER);
                            pane.getChildren().addAll(label, addresses, describe);
                            pane.setSpacing(5);
                            pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            pane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                            a.getChildren().add(pane);
                        }
                    }
                    rs = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        if(LH.isSelected()) {
            String sql = "select * from Appointment where dateStart <= '" + date.toString() + "' and '" + date.toString() + "'<=dateFinish and repeated = N'Không lặp lại' order by timeStart";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    VBox pane = new VBox();
                    String title = rs.getString(1);
                    pane.setOnMouseClicked(e -> loadInfo("Event", title));
                    int x = 0, y; // độ dài pane
                    Label label = new Label();
//                    label.setPrefSize(50, 50);
                    label.setAlignment(Pos.CENTER);

                    label.setText(rs.getString(1));
                    String dateStart = rs.getString(2);
                    String dateEnd = rs.getString(3);
                    String timeStart = rs.getString(4);
                    if (date.toString().equals(dateStart)) {
                        x = (int) setUpSizePane(timeStart);
                        pane.setTranslateY(x);

                    }

                    if(x<f){
                        startx += 10;
                        oderView = oderView-1;
                        pane.setViewOrder(oderView);

                    }else{
                        startx  = 0;
                        oderView = 0;
                    }
                    pane.setTranslateX(startx);
                    pane.setPrefWidth(120-startx);
                    pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                    if (date.toString().equals(dateEnd)) {

                        String timeFinish = rs.getString(5);

                        y = (int) setUpSizePane(timeFinish);

                        f = y;

                        pane.setMaxHeight(y - x);
                    }else{
                        f = 647;
                        pane.setPrefHeight(f-x);
                    }
                    Text addresses = new Text("Địa chỉ : " + rs.getString(6));
                    addresses.setTextAlignment(TextAlignment.CENTER);

                    pane.getChildren().addAll(label, addresses);
                    pane.setSpacing(5);
                    pane.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
                    a.getChildren().add(pane);
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(int t = 0; t < 4; t++) {
                sql = "select * from Appointment where repeated = N'" + repeatName[t] + "' order by timeStart";
                try {
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        if (checkDataRepeat(date, rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeatName[t])) {
                            VBox pane = new VBox();
                            String title = rs.getString(1);
                            pane.setOnMouseClicked(e -> loadInfo("Event", title));
                            int x = 0, y; // độ dài pane
                            Label label = new Label();
//                    label.setPrefSize(50, 50);
                            label.setAlignment(Pos.CENTER);

                            label.setText(rs.getString(1));
                            LocalDate dateStart = rs.getDate(2).toLocalDate();
                            LocalDate dateEnd = rs.getDate(3).toLocalDate();
                            String timeStart = rs.getString(4);

                            if (daySideRepeat(date,dateStart,repeatName[t])) {
                                x = (int) setUpSizePane(timeStart);
                                pane.setTranslateY(x);

                            }
                            if(x<f){
                                startx += 10;
                                oderView = oderView-1;
                                pane.setViewOrder(oderView);

                            }else{
                                startx  = 0;
                                oderView = 0;
                            }
                            pane.setTranslateX(startx);
                            pane.setPrefWidth(120-startx);
                            pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            if (daySideRepeat(date,dateEnd,repeatName[t])) {

                                String timeFinish = rs.getString(5);

                                y = (int) setUpSizePane(timeFinish);
//                        System.out.println("f: " + f);
                                f = y;

                                pane.setPrefHeight(y - x);
//                        System.out.println(y);
                            }else{
                                f = 647;
                                pane.setPrefHeight(f-x);
                            }

                            Text addresses = new Text("Địa chỉ : " + rs.getString(6));
                            addresses.setTextAlignment(TextAlignment.CENTER);
                            Text describe = new Text(rs.getString(7));
                            describe.setTextAlignment(TextAlignment.CENTER);
                            pane.getChildren().addAll(label, addresses, describe);
                            pane.setSpacing(5);
                            pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            pane.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
                            a.getChildren().add(pane);
                        }
                    }
                    rs = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if(NL.isSelected()) {
            String sql = "select * from Holiday where dateStart <= '" + date.toString() + "' and '" + date.toString() + "'<=dateFinish and repeated = N'Không lặp lại' order by timeStart";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    VBox pane = new VBox();
                    String title = rs.getString(1);
                    pane.setOnMouseClicked(e -> loadInfo("Event", title));
                    int x = 0, y; // độ dài pane
                    Label label = new Label();
//                    label.setPrefSize(50, 50);
                    label.setAlignment(Pos.CENTER);

                    label.setText(rs.getString(1));
                    String dateStart = rs.getString(2);
                    String dateEnd = rs.getString(3);
                    String timeStart = rs.getString(4);
                    if (date.toString().equals(dateStart)) {
                        x = (int) setUpSizePane(timeStart);
                        pane.setTranslateY(x);

                    }

                    if(x<f){
                        startx += 10;
                        oderView = oderView-1;
                        pane.setViewOrder(oderView);

                    }else{
                        startx  = 0;
                        oderView = 0;
                    }
                    pane.setTranslateX(startx);
                    pane.setPrefWidth(120-startx);
                    pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                    if (date.toString().equals(dateEnd)) {

                        String timeFinish = rs.getString(5);

                        y = (int) setUpSizePane(timeFinish);

                        f = y;

                        pane.setMaxHeight(y - x);
                    }else{
                        f = 647;
                        pane.setPrefHeight(f-x);
                    }
                    Text addresses = new Text("Địa chỉ : " + rs.getString(6));
                    addresses.setTextAlignment(TextAlignment.CENTER);

                    pane.getChildren().addAll(label, addresses);
                    pane.setSpacing(5);
                    pane.setBackground(new Background(new BackgroundFill(Color.FUCHSIA, CornerRadii.EMPTY, Insets.EMPTY)));
                    a.getChildren().add(pane);
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(int t = 0; t < 4; t++){
                sql = "select * from Holiday where repeated = N'"+repeatName[t]+"' order by timeStart";
                try {
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        if (checkDataRepeat(date,rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeatName[t])){
                            VBox pane = new VBox();
                            String title = rs.getString(1);
                            pane.setOnMouseClicked(e -> loadInfo("Event", title));
                            int x = 0, y; // độ dài pane
                            Label label = new Label();
//                    label.setPrefSize(50, 50);
                            label.setAlignment(Pos.CENTER);

                            label.setText(rs.getString(1));
                            LocalDate dateStart = rs.getDate(2).toLocalDate();
                            LocalDate dateEnd = rs.getDate(3).toLocalDate();
                            String timeStart = rs.getString(4);

                            if (daySideRepeat(date,dateStart,repeatName[t])) {
                                x = (int) setUpSizePane(timeStart);
                                pane.setTranslateY(x);

                            }
                            if(x<f){
                                startx += 10;
                                oderView = oderView-1;
                                pane.setViewOrder(oderView);

                            }else{
                                startx  = 0;
                                oderView = 0;
                            }
                            pane.setTranslateX(startx);
                            pane.setPrefWidth(120-startx);
                            pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            if (daySideRepeat(date,dateEnd,repeatName[t])) {

                                String timeFinish = rs.getString(5);

                                y = (int) setUpSizePane(timeFinish);
//                        System.out.println("f: " + f);
                                f = y;

                                pane.setPrefHeight(y - x);
//                        System.out.println(y);
                            }else{
                                f = 647;
                                pane.setPrefHeight(f-x);
                            }

                            Text addresses = new Text("Địa chỉ : " + rs.getString(6));
                            addresses.setTextAlignment(TextAlignment.CENTER);
                            Text describe = new Text(rs.getString(7));
                            describe.setTextAlignment(TextAlignment.CENTER);
                            pane.getChildren().addAll(label, addresses, describe);
                            pane.setSpacing(5);
                            pane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            pane.setBackground(new Background(new BackgroundFill(Color.FUCHSIA, CornerRadii.EMPTY, Insets.EMPTY)));
                            a.getChildren().add(pane);
                        }
                    }
                    rs = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }



    public void reset(){
        popularWeek(dateOfWeek);
    }

    public void choiceEvent(){
        SK.setSelected(true);
        SN.setSelected(false);
        LH.setSelected(false);
        NL.setSelected(false);
        popularWeek(dateOfWeek);
    }
    public void choiceBirthday(){
        SK.setSelected(false);
        SN.setSelected(true);
        LH.setSelected(false);
        NL.setSelected(false);
        popularWeek(dateOfWeek);
    }
    public void choiceAppointment(){
        SK.setSelected(false);
        SN.setSelected(false);
        LH.setSelected(true);
        NL.setSelected(false);
        popularWeek(dateOfWeek);
    }
    public void choiceHoliday(){
        SK.setSelected(false);
        SN.setSelected(false);
        LH.setSelected(false);
        NL.setSelected(true);
        popularWeek(dateOfWeek);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        con = Database.DBConnection.CalendarConnection();
//        sCalendar.show();
        Image img = new Image("Image/reset-filter.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(30);
        view.setFitWidth(30);
        resertBt.setGraphic(view);
        SK.setSelected(true);
        kindCalendar.setItems(listKind);
        kindCalendar.setValue("Tuần");
        popularWeek(dateOfWeek);

    }

}
