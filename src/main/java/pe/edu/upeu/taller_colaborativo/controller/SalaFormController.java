package pe.edu.upeu.taller_colaborativo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.taller_colaborativo.modelo.Sala;

public class SalaFormController {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtFilas;
    @FXML
    private TextField txtColumnas;
    @FXML
    private ComboBox<String> cbTipo;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private Label lblMensaje;

    private Sala sala;
    private boolean guardado = false;

    @FXML
    public void initialize() {
        cbTipo.getItems().addAll("2D", "3D", "IMAX");
        cbEstado.getItems().addAll("Disponible", "Mantenimiento");
        cbEstado.getSelectionModel().selectFirst();
    }

    public void setSala(Sala sala) {
        this.sala = sala;

        if (sala == null) {
            lblTitulo.setText("Nueva sala");
            return;
        }

        lblTitulo.setText("Editar sala");
        txtNombre.setText(sala.getNombre());
        txtFilas.setText(String.valueOf(sala.getFilas()));
        txtColumnas.setText(String.valueOf(sala.getColumnas()));
        cbTipo.setValue(sala.getTipo());
        cbEstado.setValue(sala.getEstado());
    }

    public boolean isGuardado() {
        return guardado;
    }

    public Sala getSala() {
        return sala;
    }

    @FXML
    private void handleSave() {
        String nombre = txtNombre.getText().trim();
        String tipo = cbTipo.getValue();

        if (nombre.isEmpty() || tipo == null || cbEstado.getValue() == null
                || txtFilas.getText().trim().isEmpty() || txtColumnas.getText().trim().isEmpty()) {
            lblMensaje.setText("Complete todos los campos de la sala.");
            return;
        }

        int filas;
        int columnas;
        try {
            filas = Integer.parseInt(txtFilas.getText().trim());
            columnas = Integer.parseInt(txtColumnas.getText().trim());
            if (filas <= 0 || columnas <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lblMensaje.setText("Las filas y columnas deben ser números mayores a 0.");
            return;
        }

        if (sala == null) {
            sala = new Sala();
        }

        sala.setNombre(nombre);
        sala.setFilas(filas);
        sala.setColumnas(columnas);
        sala.setTipo(tipo);
        sala.setEstado(cbEstado.getValue());

        guardado = true;
        cerrar();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}
