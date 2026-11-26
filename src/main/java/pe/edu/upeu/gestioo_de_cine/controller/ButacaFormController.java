package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.implementacion.SalaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.ISalaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

public class ButacaFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSala;
    @FXML
    private TextField txtFilas, txtColumnas;

    private int idSala;
    private int capacidad;
    private int filasSala;
    private int columnasSala;
    private boolean saved = false;

    private final ISalaDao salaDao = new SalaDaoImpl();

    public boolean isSaved() {
        return saved;
    }

    public void setSala(int idSala, String nombre, int capacidad, int filas, int columnas) {
        this.idSala = idSala;
        this.capacidad = capacidad;
        this.filasSala = filas;
        this.columnasSala = columnas;
        lblSala.setText("Sala: " + nombre + "  ·  Capacidad actual: " + capacidad
                + "  ·  Cuadricula actual: " + filas + " x " + columnas);
        if (filas > 0) {
            txtFilas.setText(String.valueOf(filas));
        }
        if (columnas > 0) {
            txtColumnas.setText(String.valueOf(columnas));
        }
    }

    private boolean validate() {
        if (txtFilas.getText().isBlank() || txtColumnas.getText().isBlank()) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Complete la cantidad de filas y de columnas.", "ERROR");
            alert.showAndWait();
            return false;
        }
        try {
            int filas = Integer.parseInt(txtFilas.getText().trim());
            int columnas = Integer.parseInt(txtColumnas.getText().trim());
            if (filas <= 0 || columnas <= 0) {
                utilAlerts.mostrarAlerts("Datos invalidos",
                        "Las filas y las columnas deben ser mayores que cero.", "WARNING").showAndWait();
                return false;
            }
            if (filas > 100 || columnas > 100) {
                utilAlerts.mostrarAlerts("Cuadricula muy grande",
                        "Las filas y las columnas no pueden pasar de 100.", "WARNING").showAndWait();
                return false;
            }
        } catch (NumberFormatException e) {
            utilAlerts.mostrarAlerts("Datos invalidos",
                    "Ingrese numeros enteros en filas y columnas.", "WARNING").showAndWait();
            return false;
        }
        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) txtFilas.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleSave() {
        if (!validate()) {
            return;
        }
        int filas = Integer.parseInt(txtFilas.getText().trim());
        int columnas = Integer.parseInt(txtColumnas.getText().trim());

        Sala sala;
        try {
            sala = salaDao.searchById(idSala);
        } catch (RuntimeException e) {
            System.out.println("Error al leer la sala: " + e.getMessage());
            utilAlerts.mostrarAlerts("Error",
                    "No se pudo leer la sala: " + e.getMessage(), "ERROR").showAndWait();
            return;
        }
        if (sala == null) {
            utilAlerts.mostrarAlerts("Sala no encontrada",
                    "La sala ya no existe en la base de datos.", "WARNING").showAndWait();
            return;
        }

        sala.setFilas(filas);
        sala.setColumnas(columnas);
        sala.setCapacidad(filas * columnas);

        try {
            if (!salaDao.update(sala)) {
                utilAlerts.mostrarAlerts("Sin cambios",
                        "No se pudo actualizar la sala.", "WARNING").showAndWait();
                return;
            }
        } catch (RuntimeException e) {
            System.out.println("Error al cambiar la cuadricula: " + e.getMessage());
            utilAlerts.mostrarAlerts("No se pudo cambiar la cuadricula",
                    e.getMessage(), "ERROR").showAndWait();
            return;
        }
        saved = true;
        cerrar();
    }
}
