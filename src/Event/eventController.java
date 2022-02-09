package Event;

import Validaytion.TextFieldValidaytion;
import javafx.scene.control.*;
import mainView.mainViewController;
import Convert.ConvertName;
import Dialog.AlertDialog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ResourceBundle;

public class eventController implements Initializable {
    @FXML
    private TextField eventTitle;
    @FXML
    private TextArea eventDescribe;
    @FXML
    private DatePicker eventDateStart;
    @FXML
    private DatePicker eventDateFinish;
    @FXML
    private ComboBox<String> timeBegin;
    @FXML
    private ComboBox<String> timeFinish;
    @FXML
    private ComboBox<String> minuteStart;
    @FXML
    private ComboBox<String> minuteFinish;
    @FXML
    private ComboBox<String> chooseEvent;
    @FXML
    private TextField area;
    @FXML
    private ComboBox<String> repeat;
    @FXML
    private CheckBox checkReminder;
    @FXML
    private ComboBox<String> reminder;
    @FXML
    private Label errorTitle;

    ObservableList<String> listTime = FXCollections.observableArrayList();
    ObservableList<String> listMinute = FXCollections.observableArrayList();
    ObservableList<String> listEvent = FXCollections.observableArrayList("sự kiện", "lịch hẹn", "sinh nhật", "ngày lễ");
    ObservableList<String> listRepeat = FXCollections.observableArrayList("Không lặp lại", "Hàng ngày", "Hàng tuần", "Hàng tháng", "Hàng năm", "Tùy chọn");
    ObservableList<String> listReminder = FXCollections.observableArrayList("Trước 15 phút", "Trước 30 phút", "Trước 1 giờ", "Trước 1 ngày", "Trước 1 tháng");
    private Connection con = null;
    private PreparedStatement pst = null;
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
    public void CheckReminder(){
        if(checkReminder.isSelected()){
            reminder.setVisible(true);

        }else{
            reminder.setVisible(false);
        }
    }

    public boolean RightCondition(){
        LocalDate dateStart = eventDateStart.getValue();
        LocalDate dateFinish = eventDateFinish.getValue();
        if(dateStart.isBefore(dateFinish)) return true;
        if(Integer.parseInt(timeFinish.getValue()) - Integer.parseInt(timeBegin.getValue()) >0) return true;
        if(Integer.parseInt(timeFinish.getValue()) - Integer.parseInt(timeBegin.getValue()) ==0 &&
                (Integer.parseInt(minuteFinish.getValue()) - Integer.parseInt(minuteStart.getValue()) >0))
            return true;
        return false;
    }

    public void AddEvent() throws SQLException {
        boolean isTitleEmpty = TextFieldValidaytion.isTextFieldNotEmpty(eventTitle,errorTitle,"Tiêu đề không được bỏ trống");
        if(isTitleEmpty) {
            if (chooseEvent.getValue() == null) {
                AlertDialog.display("Info", "Chọn loại sự kiện");
                return;
            }
            if (!RightCondition()) {
                AlertDialog.display("Info", "Thông tin chưa thỏa mãn");
                return;
            }

            String name = ConvertName.convertEventToSql(chooseEvent.getValue());
            String sql = "Insert into " + name + "(Title,dateStart,dateFinish,timeStart,timeFinish,Addresses,Describle,reminder, repeated)" +
                    " values(?,?,?,?,?,?,?,?,?)";
            String title = eventTitle.getText();
            String begin = timeBegin.getValue() + ":" + minuteStart.getValue();
            String finish = timeFinish.getValue() + ":" + minuteFinish.getValue();
            Date dateStart = Date.valueOf(eventDateStart.getValue());
            Date dateFinish = Date.valueOf(eventDateFinish.getValue());
            String describe = eventDescribe.getText();
            String address = area.getText();
            String REPEAT = repeat.getValue();
            String remind = "Không nhắc";
            if (checkReminder.isSelected()) {
                remind = reminder.getValue();
            }
            try {
                pst = con.prepareStatement(sql);
                pst.setString(1, title);
                pst.setDate(2, dateStart);
                pst.setDate(3, dateFinish);
                pst.setString(4, begin);
                pst.setString(5, finish);
                pst.setString(6, address);
                pst.setString(7, describe);
                pst.setString(9, REPEAT);
                pst.setString(8, remind);

                int i = pst.executeUpdate();
                if (i == 1)
//                System.out.println("successfully!");
                    AlertDialog.display("Info", "Cập nhật thành công");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                pst.close();
            }
            chooseEvent.getScene().getWindow().hide();
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        con = Database.DBConnection.CalendarConnection();
        setListMinute();
        setListTime();
        timeBegin.setItems(listTime);
        timeFinish.setItems(listTime);
        minuteStart.setItems(listMinute);
        minuteFinish.setItems(listMinute);
        chooseEvent.setItems(listEvent);
        eventDateStart.setValue(LocalDate.now());
        eventDateFinish.setValue(LocalDate.now());
        repeat.setItems(listRepeat);
        repeat.setValue("Không lặp lại");
        reminder.setItems(listReminder);
        reminder.setValue("Trước 15 phút");

        System.out.println("eok");
    }
}
