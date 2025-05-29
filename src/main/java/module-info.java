module com.example.bdtry {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bdtry to javafx.fxml;
    exports com.example.bdtry;
}