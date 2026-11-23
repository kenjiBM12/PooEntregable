package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.implementacion.ButacaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IButacaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Butaca;
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
    private boolean saved = false;

    private final IButacaDao butacaDao = new ButacaDaoImpl();

    public boolean isSaved() {
        return saved;
    }

    public void setSala(int idSala, String nombre, int capacidad) {
        this.idSala = idSala;
        this.capacidad = capacidad;
        lblSala.setText("Sala: " + nombre + "  ·  Capacidad: " + capacidad);
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
            //se multiplica con long para que el calculo no se desborde
            long total = (long) filas * (long) columnas;
            if (capacidad > 0 && total > capacidad) {
                utilAlerts.mostrarAlerts("Capacidad excedida",
                        "La sala solo tiene capacidad para " + capacidad + " butacas.", "WARNING").showAndWait();
                return false;
            }
        } catch (NumberFormatException e) {
            utilAlerts.mostrarAlerts("Datos invalidos",
                    "Ingrese números enteros en filas y columnas.", "WARNING").showAndWait();
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
        int creadas;
        try {
            //el DAO genera todas las butacas en una sola transaccion:
            //si una falla se deshace todo y la sala no queda a medias
            creadas = butacaDao.generarButacas(idSala, filas, columnas, capacidad);
        } catch (RuntimeException e) {
            System.out.println("Error al generar butacas:" + e.getMessage());
            utilAlerts.mostrarAlerts("Error",
                    "No se generaron las butacas: " + e.getMessage(), "ERROR").showAndWait();
            return;
        }
        if (creadas == 0) {
            utilAlerts.mostrarAlerts("Sin cambios",
                    "La sala ya tenía esas butacas registradas.", "WARNING").showAndWait();
            return;
        }
        saved = true;
        cerrar();
    }
}
