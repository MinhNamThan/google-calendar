package OperationOfEvent;

import Convert.ConvertName;
import Dialog.AlertDialog;
import Validaytion.TextFieldValidaytion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import mainView.AnchorPaneNode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.YearMonth;
import java.util.ResourceBundle;

public class OOEcontroller implements Initializable {
    @FXML
    private Text nameEvent;
    @FXML
    private TextField TitleEvent;
    @FXML
    private DatePicker dayStart;
    @FXML
    private DatePicker dayFinish;
    @FXML
    private ComboBox<String> timeStart;
    @FXML
    private ComboBox<String> minuteStart;
    @FXML
    private ComboBox<String> timeFinish;
    @FXML
    private ComboBox<String> minuteFinish;
    @FXML
    private TextField addresses;
    @FXML
    private TextArea describes;
    @FXML
    private ComboBox<String> repeat;
    @FXML
    private ComboBox<String> remind;
    @FXML
    private Button back;
    @FXML
    private Label erorrTitle;

    public static String nameTitle;
    public static String kindEvent;
    private Connection con = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    ObservableList<String> listTime = FXCollections.observableArrayList();
    ObservableList<String> listMinute = FXCollections.observableArrayList();
    ObservableList<String> listRepeat = FXCollections.observableArrayList("Không lặp lại", "Hàng ngày", "Hàng tuần", "Hàng tháng", "Hàng năm", "Tùy chọn");
    ObservableList<String> listReminder = FXCollections.observableArrayList("Trước 15 phút", "Trước 30 phút", "Trước 1 giờ", "Trước 1 ngày", "Trước 1 tháng");

    public void setListTime(){
        for(Integer i = 0; i<24;i++){
            if(i<10) listTime.add("0"+i.toString());
            else listTime.add(i.toString());
        }
    }
    public void setListMinute(){
        for(Integer i = 0; i<60;i++){
            if(i<10) listMinute.add("0"+i.toString());
            else listMinute.add(i.toString());
        }
    }

    public void loadHTML(){
        back.getScene().getWindow().hide();
        Stage stage = new Stage();
        URL url = null;
        try {
            url = new File("src/mainView/MainView.fxml").toURI().toURL();
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


    public void LoadEvent(){
        nameEvent.setText(ConvertName.convertNameEvent(kindEvent));
//        System.out.println();
        String sql = "select * from " + kindEvent + " where Title = N'" + nameTitle+"'";
//        System.out.println(sql);
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            if(rs.next()){
                TitleEvent.setText(rs.getString(1));
                TitleEvent.setAlignment(Pos.CENTER);
                dayStart.setValue(rs.getDate(2).toLocalDate());
                dayFinish.setValue(rs.getDate(3).toLocalDate());
                String time = rs.getString(4);
                int tmp = time.indexOf(':');
                String hour = time.substring(0,tmp);
                String minute = time.substring(tmp+1);
                timeStart.setValue(hour);
                minuteStart.setValue(minute);
                time = rs.getString(5);
                tmp = time.indexOf(':');
                hour = time.substring(0,tmp);
                minute = time.substring(tmp+1);
                timeFinish.setValue(hour);
                minuteFinish.setValue(minute);
                addresses.setText(rs.getString(6));
                describes.setText(rs.getString(7));
                repeat.setValue(rs.getString(9));
                remind.setValue(rs.getString(8));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteEvent(){
        boolean isTitleEmpty = TextFieldValidaytion.isTextFieldNotEmpty(TitleEvent,erorrTitle,"Tiêu đề không được bỏ trống");
        if(isTitleEmpty) {
            String sql = "Delete from " + kindEvent + " where Title = ?";
            try {
                pst = con.prepareStatement(sql);
                pst.setString(1, TitleEvent.getText());
                int i = pst.executeUpdate();
                if (i == 1) {
                    AlertDialog.display("Info", "Xóa thành công");
                    loadHTML();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateEvent(){
        boolean isTitleEmpty = TextFieldValidaytion.isTextFieldNotEmpty(TitleEvent,erorrTitle,"Tiêu đề không được bỏ trống");
        if(isTitleEmpty) {
            System.out.println(nameTitle);
            String sql = "Update " + kindEvent + " set Title = ?, dateStart = ?, dateFinish = ?,timeStart = ?, timeFinish = ?," +
                    " Addresses = ?, Describle = ?, reminder = ?, repeated = ? where Title = '" + nameTitle + "'";
            try {
                String title = TitleEvent.getText();
                String begin = timeStart.getValue() + ":" + minuteStart.getValue();
                String finish = timeFinish.getValue() + ":" + minuteFinish.getValue();
                Date dateStart = Date.valueOf(dayStart.getValue());
                Date dateFinish = Date.valueOf(dayFinish.getValue());
                String describe = describes.getText();
                String address = addresses.getText();
                String reminder = remind.getValue();
                String REPEAT = repeat.getValue();


                pst = con.prepareStatement(sql);
                pst.setString(1, title);
                pst.setDate(2, dateStart);
                pst.setDate(3, dateFinish);
                pst.setString(4, begin);
                pst.setString(5, finish);
                pst.setString(6, address);
                pst.setString(7, describe);
                pst.setString(8, reminder);
                pst.setString(9, REPEAT);
                int i = pst.executeUpdate();
                if (i == 1) {
                    AlertDialog.display("Info", "Cập nhật thành công!");
                    LoadEvent();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize(URL arg0, ResourceBundle arg1){
        con = Database.DBConnection.CalendarConnection();
        setListMinute();
        setListTime();
        timeStart.setItems(listTime);
        timeFinish.setItems(listTime);
        minuteStart.setItems(listMinute);
        minuteFinish.setItems(listMinute);
        repeat.setItems(listRepeat);
        remind.setItems(listReminder);
        LoadEvent();
    }
}
