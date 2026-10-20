package com.example.proyecto_final.utils;

import javafx.scene.control.Alert;

public class UtilAlerts {
    public Alert mostrarAlerts(String titulo, String mensaje, String tipo) {
        Alert alert = new Alert(Alert.AlertType.valueOf(tipo));
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert;
    }

    public static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new UtilAlerts().mostrarAlerts(titulo, mensaje, tipo.name());
        alert.showAndWait();
    }
}
