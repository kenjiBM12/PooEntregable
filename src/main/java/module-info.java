module com.example.proyecto_final {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.proyecto_final to javafx.fxml;
    opens com.example.proyecto_final.controller to javafx.fxml;

    opens com.example.proyecto_final.modelo to javafx.base;

    exports com.example.proyecto_final;
}