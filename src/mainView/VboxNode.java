package mainView;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class VboxNode extends VBox {
    private LocalDate date;
    public VboxNode (Node... Children){
        super(Children);
        this.setOnMouseClicked(e-> System.out.println(date));
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
