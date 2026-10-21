// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * PUNTO DE ENTRADA JAVAFX: crea la ventana inicial de la aplicación.
 * Carga main-view.fxml; su controlador se encarga de la navegación.
 */
public class HelloApplication extends Application {
    @Override
    /**
     * JavaFX llama a este método después de launch().
     * Stage es la ventana, Scene es su contenido y FXMLLoader construye la vista.
     */
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);
        stage.setTitle("Sistema de Gestión Cine");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Punto de entrada que ejecuta Java al iniciar esta clase.
     * En los tests permite trabajar por consola; en HelloApplication inicia JavaFX.
     */
    public static void main(String[] args) {
        launch();
    }
}
