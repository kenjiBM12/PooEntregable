
package pe.edu.upeu.project_final.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import pe.edu.upeu.project_final.utils.UtilAlerts;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        showHome();
    }

    @FXML
    private void showHome() {
        loadView("home-view.fxml");
    }

    @FXML
    private void showClient() {
        loadView("pelicula-view.fxml");
    }

    // AGREGADO: el menú permite abrir los cuatro módulos CRUD.
    @FXML
    private void showSalas() {
        loadView("sala-view.fxml");
    }

    @FXML
    private void showFunciones() {
        loadView("funcion-view.fxml");
    }

    @FXML
    private void showVentas() {
        loadView("venta-view.fxml");
    }

    @FXML
    private void showReportes() {
        loadView("reporte-view.fxml");
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/pe/edu/upeu/project_final/views/" + fxml));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            UtilAlerts.mostrarAlerts("Error de vista", e.getMessage(), "ERROR").show();
        }
    }
}