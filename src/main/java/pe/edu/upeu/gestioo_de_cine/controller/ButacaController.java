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
    private final ISalaDao salaDao = new SalaDaoImpl();

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
        //se vuelve a leer la sala porque su cuadricula pudo cambiar
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

        //ids de las butacas ocupadas en esta funcion
        Set<Integer> ocupadas = new HashSet<>();
        for (Butaca b : butacaDao.listarOcupadas(f.getIdfuncion())) {
            ocupadas.add(b.getIdbutaca());
        }

        //CORREGIDO: antes cada fila mostraba sus numeros desde 1 otra vez
        //(1,2,3... y en la siguiente fila 1,2,3...). La tabla butaca guarda
        //fila y numero dentro de la fila, por eso aqui se calcula el numero
        //global de la butaca: del 1 hasta la capacidad total de la sala.
        //Ancho de la sala: las columnas que se guardaron al crearla.
        //Si la sala es vieja y no las tiene, se deducen del numero mas alto
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
            //numero visible = butacas que hay antes de esta fila + su posicion.
            //Ejemplo con 10 columnas: fila 1 -> 1..10, fila 2 -> 11..20,
            //asi hasta el total (10x10 = 1..100)
            int numeroVisible = (b.getFila() - 1) * columnas + b.getNumero();
            Button boton = new Button(String.valueOf(numeroVisible));
            boton.getStyleClass().add("butaca");
            //CORREGIDO: tamaño minimo en vez de tamaño fijo, para que un
            //numero de 3 digitos (por ejemplo 100) no se corte
            boton.setMinSize(36, 36);
            if (ocupadas.contains(b.getIdbutaca())) {
                boton.getStyleClass().add("butaca-ocupada");
                boton.setDisable(true);
            }
            //la posicion en la grilla no cambia: columna = numero dentro de la
            //fila y fila = fila de la butaca. Solo cambia el texto del boton
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
        //CORREGIDO: antes no dejaba abrir el formulario si la sala ya estaba
        //completa, asi que la cuadricula no se podia corregir. Ahora siempre se
        //abre y desde ahi se pueden cambiar filas y columnas.
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
            //se muestra la sala como "Sala 2 — 3D", no el nombre guardado
            //la cuadricula viene de la sala, no se inventa en este formulario
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
