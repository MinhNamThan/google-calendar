package Search;

import Convert.ConvertName;
import OperationOfEvent.OOEcontroller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
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
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    @FXML
    private TableView<EventList> tableEvent;
    @FXML
    private TableColumn<? , ?> columnTitle;
    @FXML
    private TableColumn<? , ?> columnDateStart;
    @FXML
    private TableColumn<? , ?> columnTimeStart;
    @FXML
    private TableColumn<? , ?> columnAddresses;
    @FXML
    private ComboBox<String> kindEnvents;
    @FXML
    private TextField searchEvent;

    private Connection con = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private ObservableList<EventList> data;
    ObservableList<String> listEvent = FXCollections.observableArrayList("sự kiện", "lịch hẹn", "sinh nhật", "ngày lễ");

    public void setCellTable(){
        columnTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        columnDateStart.setCellValueFactory(new PropertyValueFactory<>("dayStart"));
        columnTimeStart.setCellValueFactory(new PropertyValueFactory<>("timeStart"));
        columnAddresses.setCellValueFactory(new PropertyValueFactory<>("addresses"));
    }
    public void loadData(){
        data.clear();
        String sql = "select Title, dateStart, timeStart, Addresses from " + ConvertName.convertEventToSql(kindEnvents.getValue());

        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()){
                data.add(new EventList(rs.getString(1),rs.getDate(2),rs.getString(3),rs.getString(4)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableEvent.setItems(data);
    }

    public void loadInfo(String kind, String Title){
        OOEcontroller.kindEvent = ConvertName.convertEventToSql(kind);
        OOEcontroller.nameTitle = Title;
        tableEvent.getScene().getWindow().hide();
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

    public void searchEvent(){
        if(searchEvent.getText().equals("")){
            loadData();
        }else {
            data.clear();
            String sql = "select Title, dateStart, timeStart, Addresses from " +
                    ConvertName.convertEventToSql(kindEnvents.getValue()) + " where Title LIKE '%" + searchEvent.getText() + "%'"
                        + " UNION select Title, dateStart, timeStart, Addresses from " + ConvertName.convertEventToSql(kindEnvents.getValue())
                    + " where Addresses LIKE '%" + searchEvent.getText() + "%'";
            try {
                pst = con.prepareStatement(sql);

                rs = pst.executeQuery();
                while (rs.next()) {
                    data.add(new EventList(rs.getString(1), rs.getDate(2), rs.getString(3), rs.getString(4)));

                }
                tableEvent.setItems(data);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void DetailEvent(){
        EventList e1 = tableEvent.getItems().get(tableEvent.getSelectionModel().getSelectedIndex());
        String kind = kindEnvents.getValue();
        String title = e1.getTitle();
        //System.out.println(kind);
        //System.out.println(title);
        loadInfo(kind,title);
    }

    public void loadHTML(String sURL){
        tableEvent.getScene().getWindow().hide();
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

    public void backHTML(){
        loadHTML("src/mainView/MainView.fxml");
    }

    public void initialize(URL arg0, ResourceBundle arg1){
        con = Database.DBConnection.CalendarConnection();
        data = FXCollections.observableArrayList();
        kindEnvents.setItems(listEvent);
        kindEnvents.setValue("sự kiện");
        setCellTable();
        loadData();
        searchEvent();
    }
}
