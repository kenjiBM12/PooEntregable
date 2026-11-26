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
import pe.edu.upeu.gestioo_de_cine.implementacion.SalaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IButacaDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.ISalaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Butaca;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.modelo.UserSession;
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
    private final ISalaDao salaDao = new SalaDaoImpl();

    @FXML
    public void initialize() {
        cboFuncion.getItems().setAll(funcionDao.listAll());
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
        Sala salaActual = salaDao.searchById(idSala);
        if (salaActual == null) {
            salaActual = f.getSala();
        }
        lblSala.setText(salaActual.getEtiqueta()
                + "  ·  Función: " + f.getFecha() + " " + f.getHorario());

        List<Butaca> butacas = butacaDao.listarPorSala(idSala);
        if (butacas.isEmpty()) {
            lblMensaje.setText("Esta sala todavía no tiene butacas. Use el botón GENERAR BUTACAS.");
            return;
        }

        Set<Integer> ocupadas = new HashSet<>();
        for (Butaca b : butacaDao.listarOcupadas(f.getIdfuncion())) {
            ocupadas.add(b.getIdbutaca());
        }

        int columnas = salaActual.getColumnas();
        if (columnas <= 0) {
            for (Butaca b : butacas) {
                if (b.getNumero() > columnas) {
                    columnas = b.getNumero();
                }
            }
        }
        if (columnas <= 0) {
            columnas = 1;
        }

        for (Butaca b : butacas) {
            int numeroVisible = (b.getFila() - 1) * columnas + b.getNumero();
            Button boton = new Button(String.valueOf(numeroVisible));
            boton.getStyleClass().add("butaca");
            boton.setMinSize(36, 36);
            if (ocupadas.contains(b.getIdbutaca())) {
                boton.getStyleClass().add("butaca-ocupada");
                boton.setDisable(true);
            }
            gridButacas.add(boton, b.getNumero() - 1, b.getFila() - 1);
        }
    }

    @FXML
    private void handleGenerar() {
        if (!UserSession.esAdmin()) {
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
        Sala salaActual;
        try {
            salaActual = salaDao.searchById(idSala);
        } catch (RuntimeException e) {
            utilAlert.mostrarAlerts("Error",
                    "No se pudo revisar la sala: " + e.getMessage(), "ERROR").showAndWait();
            return;
        }
        if (salaActual == null) {
            utilAlert.mostrarAlerts("Sala no encontrada",
                    "La sala ya no existe en la base de datos.", "WARNING").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/butaca-form.fxml"));
            Parent root = loader.load();
            ButacaFormController controller = loader.getController();
            controller.setSala(idSala, salaActual.getEtiqueta(), salaActual.getCapacidad(),
                    salaActual.getFilas(), salaActual.getColumnas());
            Stage stage = new Stage();
            stage.setTitle("Generar butacas");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()) {
                utilAlert.mostrarAlerts("Correcto",
                        "Butacas y capacidad de la sala actualizadas.", "CONFIRMATION").showAndWait();
                cargarButacas();
            }
        } catch (Exception e) {
            e.printStackTrace();
            utilAlert.mostrarAlerts("Error",
                    "Error al abrir el formulario: " + e.getMessage(), "ERROR").showAndWait();
        }
    }
}
