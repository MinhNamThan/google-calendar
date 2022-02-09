package TimeAlarm;

import Dialog.AlertDialog;
import artificialAlert.AlertController;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
import java.time.LocalTime;

public class AlarmEvent {
    private static PreparedStatement pstST = null;
    private static ResultSet rsST = null;
    private static Connection conST = Database.DBConnection.CalendarConnection();
    static String [] kindEvent = {"Event", "Birthday", "Appointment", "Holiday"};
    public static int sz = 0;
    private static String [] repeatName = {"Hàng ngày","Hàng tuần","Hàng tháng","Hàng năm"};
    public static boolean resert = false;

    public static String caculatorTimeAlert(String timeStart, String nameRemind){
        int minuteMinus;
        if(nameRemind.equals("Trước 15 phút")){
            minuteMinus = 15;
        }else if(nameRemind.equals("Trước 30 phút")){
            minuteMinus = 30;
        }else{
            minuteMinus = 60;
        }
        String[] time = timeStart.split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = 0;
        if(Integer.parseInt(time[1]) - minuteMinus >=0) {
            minute = Integer.parseInt(time[1]) - minuteMinus;
        }else if(hour > 0){
            int i = 1;
            while(Integer.parseInt(time[1])+60*i - minuteMinus<0) {
                i++;
            }
            minute = Integer.parseInt(time[1])+ 60*i - minuteMinus;
            hour-= i;
        }else{
            int i = 1;
            while(Integer.parseInt(time[1]) +60*i - minuteMinus<0) {
                i++;
            }
            minute = Integer.parseInt(time[1]) + 60*i - minuteMinus;
            hour = 24- i;
        }
        return hour + ":" + minute;
    }
    public static String caculatorTimeAlertNextDay(String timeStart, String nameRemind){
        int minuteMinus;
        if(nameRemind.equals("Trước 15 phút")){
            minuteMinus = 15;
        }else if(nameRemind.equals("Trước 30 phút")){
            minuteMinus = 30;
        }else{
            minuteMinus = 60;
        }
        String[] time = timeStart.split(":");
        int hour = 23;
        int minute = 0;
        minute = Integer.parseInt(time[1])+60 - minuteMinus;
        return hour + ":" + minute;
    }
    public static boolean checkDataRepeat(LocalDate dateAp, LocalDate dateStartRepeat,String repeat){
        if(repeat.equals("Hàng ngày")) return true;
        if(repeat.equals("Hàng tuần")){
            if (dateAp.getDayOfWeek() == dateStartRepeat.getDayOfWeek()) {
                //System.out.println("true");
                return true;
            }
            return false;
        }
        if(repeat.equals("Hàng tháng")){
            if(dateAp.getDayOfMonth() == dateStartRepeat.getDayOfMonth() ) return true;
            return false;
        }
        if(repeat.equals("Hàng năm")){
            if(dateAp.getDayOfYear() == dateStartRepeat.getDayOfYear() ) return true;
            return false;
        }
        return false;
    }
    public static void alarmDay(){
        for(int t = 0; t<4;t++) {
            String sql = "select * from "+ kindEvent[t] +" where dateStart = '" + LocalDate.now().plusDays(1).toString() +
                    "'and reminder = N'Trước 1 ngày'";
            try {
                pstST = conST.prepareStatement(sql);
                rsST = pstST.executeQuery();
                while (rsST.next()) {
                    AlertDialog.display("Thông báo", "bạn có 1 sự kiện " + rsST.getString(1) + " sẽ diễn ra vào ngày mai");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(int u = 0; u < 4; u++){
                sql = "select * from "+ kindEvent[t] +" where repeated = N'"+repeatName[t]+"' and reminder = N'Trước 1 ngày'";
                try {
                    pstST = conST.prepareStatement(sql);
                    rsST = pstST.executeQuery();
                    while (rsST.next()) {
                        if(checkDataRepeat(LocalDate.now().plusDays(1),rsST.getDate(2).toLocalDate(),repeatName[u])) {
                            AlertDialog.display("Thông báo", "bạn có 1 sự kiện " + rsST.getString(1) +
                                    " sẽ diễn ra vào ngày mai");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void alarmMonth(){
        for(int t = 0; t<4;t++) {
            String sql = "select * from "+ kindEvent[t] +" where dateStart = '" + LocalDate.now().plusMonths(1).toString() +
                    "'and reminder = N'Trước 1 tháng'";
            try {
                pstST = conST.prepareStatement(sql);
                rsST = pstST.executeQuery();
                while (rsST.next()) {
                    AlertDialog.display("Thông báo", "Bạn có 1 sự kiện " + rsST.getString(1) + " sẽ diễn ra vào tháng sau");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rsST = null;
            pstST = null;
            for(int u = 0; u < 4; u++){
                sql = "select * from "+ kindEvent[t] +" where repeated = N'"+repeatName[t]+"' and reminder = N'Trước 1 tháng'";
                try {
                    pstST = conST.prepareStatement(sql);
                    rsST = pstST.executeQuery();
                    while (rsST.next()) {
                        if(checkDataRepeat(LocalDate.now().plusMonths(1),rsST.getDate(2).toLocalDate(),repeatName[u])) {
                            AlertDialog.display("Thông báo", "Bạn có 1 sự kiện " + rsST.getString(1) +
                                    " sẽ diễn ra vào tháng sau");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    static remindInfo [] info = new remindInfo[100];





    public static boolean biggerTime(remindInfo a, remindInfo b){
        String[] timeA = a.getStartTime().split(":");
        if(b == null) return false;
        String[] timeB = b.getStartTime().split(":");
        if(Integer.parseInt(timeA[0])*60+Integer.parseInt(timeA[1]) > Integer.parseInt(timeB[0])*60+Integer.parseInt(timeB[1]) ){
            return true;
        }
        return false;
    }

    public static void SortAlarmMinues(){

        for(int i = 0; i< sz-1; i++){
            for(int j = i+1; j<sz; j++){

                if(biggerTime(info[i], info[j])){
                    remindInfo tmp = info[i];
                    info[i] = info[j];
                    info[j] = tmp;
                }
            }
        }
    }
    static String [] reminder = {"Trước 15 phút", "Trước 30 phút", "Trước 1 tiếng"};
    public static void getAlarmMinues(){

        pstST = null;
        rsST = null;
        System.out.println("dang load");
        String[] timecur = LocalTime.now().toString().split(":");

        String[] minusMinue;

        for(int t = 0; t<4;t++) {
            for(int j = 0; j<3; j++) {
                minusMinue = reminder[j].split(" ");

                int timeCount = Integer.parseInt(timecur[0])*60+Integer.parseInt(timecur[1]) + Integer.parseInt(minusMinue[1]);
                //System.out.println("tinh day" + timeCount);
                String sql = "select * from " + kindEvent[t] + " where dateStart = '" + LocalDate.now().toString() + "'and reminder = N'"+reminder[j]+"' and (CAST(LEFT(timeStart,CHARINDEX(':',timeStart,1) -1) AS INT)*60 + CAST(RIGHT(timeStart,LEN(timeStart) - CHARINDEX(':',timeStart,1)) AS INT) >" + String.valueOf(timeCount) +")";
                try {
                    pstST = conST.prepareStatement(sql);
                    rsST = pstST.executeQuery();
                    while (rsST.next()) {
                        System.out.println(rsST.getString(4));
                        String timeAlert = caculatorTimeAlert(rsST.getString(4), reminder[j]);
                        //System.out.println("tinh xong");
                        info[sz] = new remindInfo(timeAlert, kindEvent[t], rsST.getString(1));
                        sz++;
                        //System.out.println("xong " + sz);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                for(int u = 0; u < 4; u++){
                    sql = "select * from "+ kindEvent[t] +" where repeated = N'"+repeatName[u]+"' and reminder = N'"+reminder[j]+"' and (CAST(LEFT(timeStart,CHARINDEX(':',timeStart,1) -1) AS INT)*60 + CAST(RIGHT(timeStart,LEN(timeStart) - CHARINDEX(':',timeStart,1)) AS INT) >" + String.valueOf(timeCount) +")";
                    try {
                        pstST = conST.prepareStatement(sql);
                        rsST = pstST.executeQuery();
                        while (rsST.next()) {

                            if(checkDataRepeat(LocalDate.now(),rsST.getDate(2).toLocalDate(),repeatName[u])) {

                                String timeAlert = caculatorTimeAlert(rsST.getString(4), reminder[j]);
                                //System.out.println("tinh xong");
                                info[sz] = new remindInfo(timeAlert, kindEvent[t], rsST.getString(1));
                                sz++;
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        //System.out.println("load xong");

    }

    public static void getAlarmMinuesOfNextDay(){

        pstST = null;
        rsST = null;
        System.out.println("dang load");

        for(int t = 0; t<4;t++) {

                String sql = "select * from " + kindEvent[t] + " where dateStart = '" + LocalDate.now().plusDays(1).toString() + "'and reminder = N'Trước 15 phút' and CAST(LEFT(timeStart,CHARINDEX(':',timeStart,1) -1) AS INT) = 0 and CAST(RIGHT(timeStart,LEN(timeStart) - CHARINDEX(':',timeStart,1)) AS INT) <= 15";
                try {
                    pstST = conST.prepareStatement(sql);
                    rsST = pstST.executeQuery();
                    while (rsST.next()) {
                        System.out.println(rsST.getString(4));
                        String timeAlert = caculatorTimeAlertNextDay(rsST.getString(4), "Trước 15 phút");
                        //System.out.println("tinh xong");
                        info[sz] = new remindInfo(timeAlert, kindEvent[t], rsST.getString(1));
                        sz++;
                        //System.out.println("xong " + sz);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            String sql1 = "select * from " + kindEvent[t] + " where dateStart = '" + LocalDate.now().plusDays(1).toString() + "'and reminder = N'Trước 30 phút' and CAST(LEFT(timeStart,CHARINDEX(':',timeStart,1) -1) AS INT) = 0 and CAST(RIGHT(timeStart,LEN(timeStart) - CHARINDEX(':',timeStart,1)) AS INT) <= 30";
            try {
                pstST = conST.prepareStatement(sql1);
                rsST = pstST.executeQuery();
                while (rsST.next()) {
                    System.out.println(rsST.getString(4));
                    String timeAlert = caculatorTimeAlertNextDay(rsST.getString(4), "Trước 30 phút");
                    //System.out.println("tinh xong");
                    info[sz] = new remindInfo(timeAlert, kindEvent[t], rsST.getString(1));
                    sz++;
                    //System.out.println("xong " + sz);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String sql2= "select * from " + kindEvent[t] + " where dateStart = '" + LocalDate.now().plusDays(1).toString() + "'and reminder = N'Trước 1 tiếng' and CAST(LEFT(timeStart,CHARINDEX(':',timeStart,1) -1) AS INT) = 0 and CAST(RIGHT(timeStart,LEN(timeStart) - CHARINDEX(':',timeStart,1)) AS INT) <= 59";
            try {
                pstST = conST.prepareStatement(sql2);
                rsST = pstST.executeQuery();
                while (rsST.next()) {
                    System.out.println(rsST.getString(4));
                    String timeAlert = caculatorTimeAlertNextDay(rsST.getString(4), "Trước 1 tiếng");
                    //System.out.println("tinh xong");
                    info[sz] = new remindInfo(timeAlert, kindEvent[t], rsST.getString(1));
                    sz++;
                    //System.out.println("xong " + sz);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        //System.out.println("load xong");

    }
    public static  int dem;
    public static void alertInfo(){
        Time time = new Time(new CurrentTime().currentTime());
        if(dem == sz) return;
        System.out.println(info[dem].getStartTime());
        if(time.getCurrentTime().equals(info[dem].getStartTime()+":5")){
            AlertController.s = "Bạn sắp có 1 "+ info[dem].getKindEvent()+" : " + info[dem].getTitle();
            Stage stage = new Stage();
            URL url = null;
            try {
                url = new File("src/artificialAlert/Alert.fxml").toURI().toURL();
                Parent root = FXMLLoader.load(url);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.getIcons().add(new Image("Image/icons8-calendar-96.png"));
                stage.setTitle("Thông báo");
                stage.show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dem = dem + 1;
        }

        System.out.println(time.getCurrentTime());
        time.oneSecondPassed();

    }

    public static void AlertAlarm() {
        for(int i = 0; i<sz; i++){
            info[i] = new remindInfo();
            sz = 0;
        }

        getAlarmMinues();
        getAlarmMinuesOfNextDay();
        SortAlarmMinues();
        System.out.println("xep xong");
        System.out.println(sz);
        dem = 0;
        if(sz == dem) return;

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        e -> {
                            alertInfo();

                        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();


    }
    public static void deleteOldEvent(){

        for(int t = 0; t<4;t++) {
            String sql = "Delete from " + kindEvent[t] + " where dateFinish <= '" +
                    LocalDate.now().minusMonths(1).toString() + "' and repeated = N'Không lặp lại'";
            try {
                pstST = conST.prepareStatement(sql);

                int i = pstST.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
