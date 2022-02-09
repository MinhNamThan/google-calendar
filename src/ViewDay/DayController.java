package ViewDay;

import Convert.ConvertName;
import Dialog.AlertDialog;
import OperationOfEvent.OOEcontroller;
import Week.WeekController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import mainView.AnchorPaneNode;

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

public class DayController implements Initializable {
    @FXML
    private DatePicker sCalendar;
    @FXML
    private Text calendarTitle;
    @FXML
     private ComboBox<String> kindCalendar;
    @FXML
    private CheckBox SK;
    @FXML
    private CheckBox SN;
    @FXML
    private CheckBox LH;
    @FXML
    private CheckBox NL;
    @FXML
    private HBox myEvent;
    @FXML
    private AnchorPane warningAlert;
    @FXML
    private Button resertBt;


    public static LocalDate day = LocalDate.now();
    ObservableList<String> listKind = FXCollections.observableArrayList("Tháng", "Ngày","Tuần");
    private Connection con = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private LocalDate dateCurrent;
    private boolean warning;
    float [] s = new float[100];
    float [] f = new float[100];
    int n = 0;
    final double MAX_FONT_SIZE = 15.0; // define max font size you need




    public void popularDay(LocalDate date){
        n=0;
        dateCurrent = date;
        System.out.println(date);
        if(myEvent.getChildren().size()!=0){
            myEvent.getChildren().clear();
        }
        loadDateEvent(date);
        loadDateEventRepeated(date,"Hàng ngày");
        loadDateEventRepeated(date,"Hàng tuần");
        loadDateEventRepeated(date,"Hàng tháng");
        loadDateEventRepeated(date,"Hàng năm");
        calendarTitle.setText(date.toString());
        warning = isRepeatAppoinment(s,f,n);
        if(warning){
            warningAlert.setVisible(true);
        }else{
            warningAlert.setVisible(false);
        }
    }
    public void previousDay(){
        dateCurrent = dateCurrent.minusDays(1);
        popularDay(dateCurrent);
    }
    public void nextDay(){
        dateCurrent = dateCurrent.plusDays(1);
        popularDay(dateCurrent);
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
            loadHTML("src/ViewDay/Day.fxml");
        }else if(name.equals("Tuần")){
            WeekController.dateOfWeek = dateCurrent;
            loadHTML("src/Week/week.fxml");
        }
    }

    public void loadInfo(String kind, String Title){
        OOEcontroller.kindEvent = kind;
        OOEcontroller.nameTitle = Title;
        myEvent.getScene().getWindow().hide();
        Stage stage = new Stage();
        URL url = null;
        try {
            url = new File("src/OperationOfEvent/OOE.fxml").toURI().toURL();
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

    public float setUpSizePane(String time){
        int tmp = time.indexOf(':');
//        System.out.println(time.substring(0,tmp));
        int hour = Integer.parseInt(time.substring(0,tmp));
        int minute = Integer.parseInt(time.substring(tmp+1));
//        System.out.println(hour);
//        System.out.println(hour*27+(float)(0.45*minute));
        return hour*27+(float)(0.45*minute);
    }

    public boolean isRepeatAppoinment(float s[], float f[], int n){
        System.out.println("n" + n);
        for(int i = 0; i<n-1;i++){
            for(int j = i+1; j<n; j++){
                System.out.println(s[i]);
                System.out.println(s[j]);
                System.out.println(f[i]);
                System.out.println(f[j]);
                if((s[i] <= s[j] && f[i] > s[j] )|| (s[j] <= s[i] && f[j] > s[i] )){
                    System.out.println("trung");
                    return true;
                }
            }
        }
        return false;
    }

    public void loadDateEvent(LocalDate date) {


        if (SK.isSelected()) {
            String sql = "select * from Event where dateStart <= '" + date.toString() + "' and '" + date.toString() + "'<=dateFinish" +
                    " and repeated = N'Không lặp lại'";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    VBox pane = new VBox();
                    String title = rs.getString(1);
                    pane.setOnMouseClicked(e -> loadInfo("Event",title));
                    int x=0,y; // độ dài pane
                    Label label = new Label();
//                    label.setPrefSize(50, 50);
                    label.setAlignment(Pos.CENTER);

                    label.setText(rs.getString(1));
                    label.setFont(new Font(MAX_FONT_SIZE)); // set to Label
                    String dateStart = rs.getString(2);
                    String dateEnd = rs.getString(3);
                    String timeStart = rs.getString(4);
                    if(date.toString().equals(dateStart)){
                        x = (int) setUpSizePane(timeStart);
                        pane.setTranslateY(x);
                        pane.setMinWidth(50);
                    }

                    if(date.toString().equals(dateEnd)) {

                        String timeFinish = rs.getString(5);

                        y = (int) setUpSizePane(timeFinish);

                        pane.setMaxHeight(y-x);
                    }
                    Text addresses = new Text("Địa chỉ : "+ rs.getString(6));
                    addresses.setFont(new Font(MAX_FONT_SIZE));
                    addresses.setTextAlignment(TextAlignment.CENTER);
                    Text describe = new Text(rs.getString(7));
                    describe.setFont(new Font(MAX_FONT_SIZE));
                    describe.setTextAlignment(TextAlignment.CENTER);
                    pane.getChildren().addAll(label,addresses,describe);
                    pane.setSpacing(5);
                    pane.setStyle("-fx-background-radius: 10;-fx-background-color: GREENYELLOW;");
                    pane.setPrefWidth(200);

                    myEvent.getChildren().add(pane);
                    myEvent.setSpacing(2);
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (SN.isSelected()) {
            String sql = "select * from Birthday where dateStart <= '" + date.toString() + "' and '" + date.toString() + "'<=dateFinish and repeated = N'Không lặp lại'";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    VBox pane = new VBox();
                    String title = rs.getString(1);
                    pane.setOnMouseClicked(e -> loadInfo("Birthday",title));
                    int x = 0,y; // độ dài pane
                    Label label = new Label();
                    label.setPrefSize(100, 50);
                    label.setAlignment(Pos.CENTER);
                    label.setText(rs.getString(1));

                    String dateStart = rs.getString(2);
                    String dateEnd = rs.getString(3);
                    String timeStart = rs.getString(4);
                    if(date.toString().equals(dateStart)){
                        x = (int) setUpSizePane(timeStart);
                        pane.setTranslateY(x);
                    }

                    if(date.toString().equals(dateEnd)) {

                        String timeFinish = rs.getString(5);

                        y = (int) setUpSizePane(timeFinish);

                        pane.setMaxHeight(y-x);
                    }
                    Text addresses = new Text("Địa chỉ: "+ rs.getString(6));
                    Text  describe = new Text(rs.getString(7));
                    label.setFont(new Font(MAX_FONT_SIZE));
                    addresses.setFont(new Font(MAX_FONT_SIZE));
                    describe.setFont(new Font(MAX_FONT_SIZE));
                    pane.getChildren().addAll(label,addresses,describe);
                    pane.setSpacing(5);
                    pane.setStyle("-fx-background-radius: 10;-fx-background-color: RED;");
                    pane.setPrefWidth(200);
                    myEvent.getChildren().add(pane);
                    myEvent.setSpacing(2);
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (LH.isSelected()) {
            String sql = "select * from Appointment where dateStart <= '" + date.toString() + "' and '" + date.toString() + "'<=dateFinish and repeated = N'Không lặp lại'";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();

                while (rs.next()) {
                    VBox pane = new VBox();
                    String title = rs.getString(1);
                    pane.setOnMouseClicked(e -> loadInfo("Appointment",title));
                    int x = 0,y; // độ dài pane
                    Label label = new Label();
                    label.setPrefSize(100, 50);
                    label.setAlignment(Pos.CENTER);
                    label.setText(rs.getString(1));
                    String dateStart = rs.getString(2);
                    String dateEnd = rs.getString(3);
                    String timeStart = rs.getString(4);
                    if(date.toString().equals(dateStart)){
                        x = (int) setUpSizePane(timeStart);
                        pane.setTranslateY(x);
                        s[n] = setUpSizePane(timeStart);
                    }else{
                        s[n] = 0;
                    }

                    if(date.toString().equals(dateEnd)) {

                        String timeFinish = rs.getString(5);

                        y = (int) setUpSizePane(timeFinish);
//                        System.out.println(x);
//                        System.out.println(y);
                        pane.setMaxHeight(y-x);
                        f[n] = setUpSizePane(timeFinish);
                    }else{
                        f[n] = 648;
                    }
                    Text addresses = new Text("Địa chỉ: "+ rs.getString(6));
                    Text  describe = new Text(rs.getString(7));
                    label.setFont(new Font(MAX_FONT_SIZE));
                    addresses.setFont(new Font(MAX_FONT_SIZE));
                    describe.setFont(new Font(MAX_FONT_SIZE));
                    pane.getChildren().addAll(label,addresses,describe);
                    pane.setSpacing(5);
                    pane.setStyle("-fx-background-radius: 10;-fx-background-color: BLUEVIOLET;");
                    pane.setPrefWidth(200);
                    //pane.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
                    myEvent.getChildren().add(pane);
                    myEvent.setSpacing(2);
                    n++;
                }


                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (NL.isSelected()) {
            String sql = "select * from Holiday where dateStart <= '" + date.toString() + "' and '" + date.toString() + "'<=dateFinish and repeated = N'Không lặp lại'";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    VBox pane = new VBox();
                    String title = rs.getString(1);
                    pane.setOnMouseClicked(e -> loadInfo("Holiday",title));
                    int x = 0,y; // độ dài pane
                    Label label = new Label();
                    label.setPrefSize(100, 50);
                    label.setAlignment(Pos.CENTER);
                    label.setText(rs.getString(1));
                    String dateStart = rs.getString(2);
                    String dateEnd = rs.getString(3);
                    String timeStart = rs.getString(4);
                    if(date.toString().equals(dateStart)){
                        x = (int) setUpSizePane(timeStart);
                        pane.setTranslateY(x);
                    }

                    if(date.toString().equals(dateEnd)) {

                        String timeFinish = rs.getString(5);

                        y = (int) setUpSizePane(timeFinish);

                        pane.setMaxHeight(y-x);
                    }
                    Text addresses = new Text("Địa chỉ: "+ rs.getString(6));
                    Text  describe = new Text(rs.getString(7));
                    label.setFont(new Font(MAX_FONT_SIZE));
                    addresses.setFont(new Font(MAX_FONT_SIZE));
                    describe.setFont(new Font(MAX_FONT_SIZE));
                    pane.getChildren().addAll(label,addresses,describe);
                    pane.setSpacing(5);
                    pane.setStyle("-fx-background-radius: 10;-fx-background-color: FUCHSIA;");
                    pane.setPrefWidth(200);
                    //pane.setBackground(new Background(new BackgroundFill(Color.FUCHSIA, CornerRadii.EMPTY, Insets.EMPTY)));
                    myEvent.getChildren().add(pane);
                    myEvent.setSpacing(2);
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

    public void loadDateEventRepeated(LocalDate date, String repeat) {
        if (SK.isSelected()) {
            String sql = "select * from Event where repeated = N'"+repeat+"'";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    if (checkDataRepeat(date,rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeat)){
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
                        if(daySideRepeat(date,dateStart,repeat)){
                            x = (int) setUpSizePane(timeStart);
                            pane.setTranslateY(x);
                        }

                        if(daySideRepeat(date,dateEnd,repeat)) {

                            String timeFinish = rs.getString(5);

                            y = (int) setUpSizePane(timeFinish);

                            pane.setMaxHeight(y-x);
                        }
                        Text addresses = new Text("Địa chỉ : " + rs.getString(6));
                        addresses.setTextAlignment(TextAlignment.CENTER);
                        Text describe = new Text(rs.getString(7));
                        describe.setTextAlignment(TextAlignment.CENTER);
                        label.setFont(new Font(MAX_FONT_SIZE));
                        addresses.setFont(new Font(MAX_FONT_SIZE));
                        describe.setFont(new Font(MAX_FONT_SIZE));
                        pane.getChildren().addAll(label, addresses, describe);
                        pane.setSpacing(5);
                        pane.setStyle("-fx-background-radius: 10;-fx-background-color: GREENYELLOW;");
                        pane.setPrefWidth(200);
                        //pane.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                        myEvent.getChildren().add(pane);
                        myEvent.setSpacing(2);
                    }
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (SN.isSelected()) {
            String sql = "select * from Birthday where repeated = N'"+repeat+"'";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    if (checkDataRepeat(date,rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeat)) {
                        VBox pane = new VBox();
                        String title = rs.getString(1);
                        pane.setOnMouseClicked(e -> loadInfo("Birthday", title));
                        int x = 0, y; // độ dài pane
                        Label label = new Label();
                        label.setPrefSize(100, 50);
                        label.setAlignment(Pos.CENTER);
                        label.setText(rs.getString(1));
                        LocalDate dateStart = rs.getDate(2).toLocalDate();
                        LocalDate dateEnd = rs.getDate(3).toLocalDate();
                        String timeStart = rs.getString(4);
                        if(daySideRepeat(date,dateStart,repeat)){
                            x = (int) setUpSizePane(timeStart);
                            pane.setTranslateY(x);
                        }

                        if(daySideRepeat(date,dateEnd,repeat)) {

                            String timeFinish = rs.getString(5);

                            y = (int) setUpSizePane(timeFinish);

                            pane.setMaxHeight(y-x);
                        }
                        Text addresses = new Text("Địa chỉ: " + rs.getString(6));
                        Text describe = new Text(rs.getString(7));
                        label.setFont(new Font(MAX_FONT_SIZE));
                        addresses.setFont(new Font(MAX_FONT_SIZE));
                        describe.setFont(new Font(MAX_FONT_SIZE));
                        pane.getChildren().addAll(label, addresses, describe);
                        pane.setSpacing(5);
                        pane.setStyle("-fx-background-radius: 10;-fx-background-color: RED;");
                        pane.setPrefWidth(200);
                        //pane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                        myEvent.getChildren().add(pane);
                        myEvent.setSpacing(2);
                    }
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (LH.isSelected()) {
            String sql = "select * from Appointment where repeated = N'"+repeat+"'";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();

                while (rs.next()) {
                    if (checkDataRepeat(date,rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeat)) {
                        VBox pane = new VBox();
                        String title = rs.getString(1);
                        pane.setOnMouseClicked(e -> loadInfo("Appointment", title));
                        int x = 0, y; // độ dài pane
                        Label label = new Label();
                        label.setPrefSize(100, 50);
                        label.setAlignment(Pos.CENTER);
                        label.setText(rs.getString(1));
                        LocalDate dateStart = rs.getDate(2).toLocalDate();
                        LocalDate dateEnd = rs.getDate(3).toLocalDate();
                        String timeStart = rs.getString(4);
                        String timeFinish = rs.getString(5);
                        if(daySideRepeat(date,dateStart,repeat)){
                            x = (int) setUpSizePane(timeStart);
                            pane.setTranslateY(x);
                        }

                        if(daySideRepeat(date,dateEnd,repeat)) {



                            y = (int) setUpSizePane(timeFinish);

                            pane.setMaxHeight(y-x);
                        }
                            f[n] = setUpSizePane(timeFinish);

                        Text addresses = new Text("Địa chỉ: " + rs.getString(6));
                        Text describe = new Text(rs.getString(7));
                        label.setFont(new Font(MAX_FONT_SIZE));
                        addresses.setFont(new Font(MAX_FONT_SIZE));
                        describe.setFont(new Font(MAX_FONT_SIZE));
                        pane.getChildren().addAll(label, addresses, describe);
                        pane.setSpacing(5);
                        pane.setStyle("-fx-background-radius: 10;-fx-background-color: BLUEVIOLET;");
                        pane.setPrefWidth(200);
                        //pane.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
                        myEvent.getChildren().add(pane);
                        myEvent.setSpacing(2);
                        n++;
                    }
                }


                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (NL.isSelected()) {
            String sql = "select * from Holiday where repeated = N'"+repeat+"'";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    if (checkDataRepeat(date,rs.getDate(2).toLocalDate(),rs.getDate(3).toLocalDate(), repeat)) {
                        VBox pane = new VBox();
                        String title = rs.getString(1);
                        pane.setOnMouseClicked(e -> loadInfo("Holiday", title));
                        int x = 0, y; // độ dài pane
                        Label label = new Label();
                        label.setPrefSize(100, 50);
                        label.setAlignment(Pos.CENTER);
                        label.setText(rs.getString(1));
                        LocalDate dateStart = rs.getDate(2).toLocalDate();
                        LocalDate dateEnd = rs.getDate(3).toLocalDate();
                        String timeStart = rs.getString(4);

                        if(daySideRepeat(date,dateStart,repeat)){
                            x = (int) setUpSizePane(timeStart);
                            pane.setTranslateY(x);
                        }

                        if(daySideRepeat(date,dateEnd,repeat)) {

                            String timeFinish = rs.getString(5);

                            y = (int) setUpSizePane(timeFinish);

                            pane.setMaxHeight(y-x);
                        }

                        Text addresses = new Text("Địa chỉ: " + rs.getString(6));
                        Text describe = new Text(rs.getString(7));
                        label.setFont(new Font(MAX_FONT_SIZE));
                        addresses.setFont(new Font(MAX_FONT_SIZE));
                        describe.setFont(new Font(MAX_FONT_SIZE));
                        pane.getChildren().addAll(label, addresses, describe);
                        pane.setSpacing(5);
                        pane.setStyle("-fx-background-radius: 10;-fx-background-color: FUCHSIA;");
                        pane.setPrefWidth(200);
                        //pane.setBackground(new Background(new BackgroundFill(Color.FUCHSIA, CornerRadii.EMPTY, Insets.EMPTY)));
                        myEvent.getChildren().add(pane);
                        myEvent.setSpacing(2);
                    }
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
    public void show(){
        popularDay(dateCurrent);
    }

    public void hideWarning(){
        warningAlert.setVisible(false);
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

        popularDay(day);
        kindCalendar.setItems(listKind);
        kindCalendar.setValue("Ngày");

    }
}