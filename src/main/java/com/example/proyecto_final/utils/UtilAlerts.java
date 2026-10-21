// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.utils;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import javafx.scene.control.Alert;

/**
 * UTILIDAD: centraliza la creación de mensajes de información, error y confirmación.
 * Una alerta es una ventana pequeña de JavaFX para comunicarse con el usuario.
 */
public class UtilAlerts {
    /**
     * Construye un Alert y lo devuelve; quien llama decide cuándo mostrarlo.
     */
    public Alert mostrarAlerts(String titulo, String mensaje, String tipo) {
        Alert alert = new Alert(Alert.AlertType.valueOf(tipo));
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert;
    }

    /**
     * Atajo que construye la alerta y la muestra con showAndWait().
     */
    public static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new UtilAlerts().mostrarAlerts(titulo, mensaje, tipo.name());
        alert.showAndWait();
    }
}
