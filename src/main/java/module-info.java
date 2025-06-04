module com.example.bdtry {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.zaxxer.hikari;
    requires java.sql;
    requires org.slf4j;


    opens com.example.bdtry to javafx.fxml;
    exports com.example.bdtry;
    exports com.example.bdtry.controller;
    opens com.example.bdtry.controller to javafx.fxml;
}