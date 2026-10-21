/**
 * MÓDULO JAVA: declara qué bibliotecas necesita la aplicación y qué paquetes abre.
 * requires permite usar módulos externos; opens permite la reflexión del cargador FXML.
 * exports hace visible el paquete principal para iniciar la aplicación.
 */
module com.example.proyecto_final {
    // Controles, ventanas y tablas de JavaFX.
    requires javafx.controls;
    // FXMLLoader y la anotación @FXML.
    requires javafx.fxml;
    // API JDBC: Connection, PreparedStatement y ResultSet.
    requires java.sql;

    opens com.example.proyecto_final to javafx.fxml;
    opens com.example.proyecto_final.controller to javafx.fxml;

    opens com.example.proyecto_final.modelo to javafx.base;

    exports com.example.proyecto_final;
}