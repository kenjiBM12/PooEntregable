package pe.edu.upeu.taller_colaborativo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;
    @FXML
    private Label lblUsuario;
    @FXML
    private Button btnInicio;
    @FXML
    private Button btnCartelera;
    @FXML
    private Button btnPeliculas;
    @FXML
    private Button btnSalas;
    @FXML
    private Button btnFunciones;
    @FXML
    private Button btnVentas;
    @FXML
    private Button btnVenta;

    // Guarda el controlador para que otras vistas puedan pedir un cambio de pantalla
    private static MainController instancia;

    @FXML
    public void initialize() {
        instancia = this;
        showInicio();
    }

    public static MainController getInstancia() {
        return instancia;
    }

    public void setUsuario(String usuario) {
        lblUsuario.setText("Bienvenido, " + usuario);
    }

    @FXML
    private void showInicio() {
        cargarVista("home-view.fxml");
        marcarActivo(btnInicio);
    }

    @FXML
    private void showCartelera() {
        cargarVista("cartelera-view.fxml");
        marcarActivo(btnCartelera);
    }

    @FXML
    private void showPeliculas() {
        cargarVista("pelicula-view.fxml");
        marcarActivo(btnPeliculas);
    }

    @FXML
    private void showSalas() {
        cargarVista("sala-view.fxml");
        marcarActivo(btnSalas);
    }

    @FXML
    private void showFunciones() {
        cargarVista("funcion-view.fxml");
        marcarActivo(btnFunciones);
    }

    @FXML
    private void showVentas() {
        cargarVista("venta-lista-view.fxml");
        marcarActivo(btnVentas);
    }

    // Tambien la llama la cartelera desde el boton "Elegir butacas"
    public void showVenta() {
        cargarVista("venta-view.fxml");
        marcarActivo(btnVenta);
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/pe/edu/upeu/taller_colaborativo/views/login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setWidth(900);
            stage.setHeight(560);
            stage.centerOnScreen();
            stage.setTitle("Cine UPeU - Iniciar sesión");
        } catch (IOException e) {
            System.err.println("No se pudo cerrar la sesión: " + e.getMessage());
        }
    }

    // Metodo que busca el archivo fxml y lo coloca en el area de contenido
    private void cargarVista(String archivo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/pe/edu/upeu/taller_colaborativo/views/" + archivo));
            Parent vista = loader.load();
            contentArea.getChildren().setAll(vista);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista " + archivo + ": " + e.getMessage());
        }
    }

    // Pinta el boton del menu que esta seleccionado
    private void marcarActivo(Button activo) {
        for (Button boton : new Button[]{btnInicio, btnCartelera, btnPeliculas,
                btnSalas, btnFunciones, btnVentas, btnVenta}) {
            boton.getStyleClass().remove("nav-button-activo");
        }
        activo.getStyleClass().add("nav-button-activo");
    }
}
