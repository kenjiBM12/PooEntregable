package pe.edu.upeu.taller_colaborativo.utils;

import javafx.scene.control.Alert;

public class UtilAlerts {

    public static Alert mostrarAlerts(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert;
    }
}
