package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.implementacion.ButacaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.FuncionDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IButacaDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Butaca;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ButacaController {

    private static UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private ComboBox<Funcion> cboFuncion;
    @FXML
    private GridPane gridButacas;
    @FXML
    private Label lblSala;
    @FXML
    private Label lblMensaje;

    private final IFuncionDao funcionDao = new FuncionDaoImpl();
    private final IButacaDao butacaDao = new ButacaDaoImpl();

    @FXML
    public void initialize() {
        cboFuncion.getItems().setAll(funcionDao.listAll());
        //cuando cambia la funcion se vuelven a pintar las butacas
        cboFuncion.valueProperty().addListener((obs, viejo, nuevo) -> cargarButacas());
    }

    private void cargarButacas() {
        gridButacas.getChildren().clear();
        lblMensaje.setText("");
        Funcion f = cboFuncion.getValue();
        if (f == null) {
            lblSala.setText("Elija una función para ver sus butacas.");
            return;
        }
        if (f.getSala() == null) {
            lblSala.setText("La función no tiene sala asignada.");
            return;
        }
        int idSala = f.getSala().getIdsala();
        lblSala.setText(f.getSala().getEtiqueta()
                + "  ·  Función: " + f.getFecha() + " " + f.getHorario());

        List<Butaca> butacas = butacaDao.listarPorSala(idSala);
        if (butacas.isEmpty()) {
            lblMensaje.setText("Esta sala todavía no tiene butacas. Use el botón GENERAR BUTACAS.");
            return;
        }

        //ids de las butacas ocupadas en esta funcion
        Set<Integer> ocupadas = new HashSet<>();
        for (Butaca b : butacaDao.listarOcupadas(f.getIdfuncion())) {
            ocupadas.add(b.getIdbutaca());
        }

        for (Butaca b : butacas) {
            Button boton = new Button(String.valueOf(b.getNumero()));
            boton.getStyleClass().add("butaca");
            boton.setPrefSize(36, 36);
            if (ocupadas.contains(b.getIdbutaca())) {
                boton.getStyleClass().add("butaca-ocupada");
                boton.setDisable(true);
            }
            gridButacas.add(boton, b.getNumero() - 1, b.getFila() - 1);
        }
    }

    @FXML
    private void handleGenerar() {
        //generar butacas cambia la estructura de la sala: solo el admin
        if (!Sesion.esAdmin()) {
            utilAlert.mostrarAlerts("Sin permiso",
                    "Solo el administrador puede generar butacas.", "WARNING").showAndWait();
            return;
        }
        Funcion f = cboFuncion.getValue();
        if (f == null || f.getSala() == null) {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Elija primero una función para saber de qué sala son las butacas.", "WARNING").showAndWait();
            return;
        }
        int idSala = f.getSala().getIdsala();
        int registradas;
        try {
            registradas = butacaDao.contarPorSala(idSala);
        } catch (RuntimeException e) {
            utilAlert.mostrarAlerts("Error",
                    "No se pudo revisar las butacas: " + e.getMessage(), "ERROR").showAndWait();
            return;
        }
        //si la sala quedo incompleta se puede volver a abrir para completarla
        if (f.getSala().getCapacidad() > 0 && registradas >= f.getSala().getCapacidad()) {
            utilAlert.mostrarAlerts("Ya tiene butacas",
                    "La " + f.getSala().getEtiqueta() + " ya tiene sus " + registradas
                            + " butacas registradas.", "WARNING").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/butaca-form.fxml"));
            Parent root = loader.load();
            ButacaFormController controller = loader.getController();
            //se muestra la sala como "Sala 2 — 3D", no el nombre guardado
            controller.setSala(idSala, f.getSala().getEtiqueta(), f.getSala().getCapacidad());
            Stage stage = new Stage();
            stage.setTitle("Generar butacas");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()) {
                utilAlert.mostrarAlerts("Success",
                        "Butacas generadas!!!", "CONFIRMATION").showAndWait();
                cargarButacas();
            }
        } catch (Exception e) {
            e.printStackTrace();
            utilAlert.mostrarAlerts("Error",
                    "Error al abrir el formulario: " + e.getMessage(), "ERROR").showAndWait();
        }
    }
}
