package com.example.proyecto_final.controller;

import com.example.proyecto_final.implementacion.PeliculaDaoImpl;
import com.example.proyecto_final.implementacion.SalaDaoImpl;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class FuncionFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;

    @FXML
    private ComboBox<String> cbPelicula, cbSala;

    @FXML
    private TextField txtHorario, txtPrecio;

    private Funcion funcion;
    private boolean saved = false;
    private List<Pelicula> peliculas;
    private List<Sala> salas;

    @FXML
    public void initialize() {
        peliculas = new PeliculaDaoImpl().listAll();
        for (Pelicula p : peliculas) {
            cbPelicula.getItems().add(p.getIdpelicula() + " - " + p.getTitulo());
        }
        salas = new SalaDaoImpl().listAll();
        for (Sala s : salas) {
            cbSala.getItems().add(s.getIdsala() + " - " + s.getNombre());
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    private boolean validate() {
        if (cbPelicula.getValue() == null
                || cbSala.getValue() == null
                || txtHorario.getText().isEmpty()
                || txtPrecio.getText().isEmpty()) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Película, sala, horario y precio son obligatorios",
                    "ERROR");
            alert.show();
            return false;
        }
        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) txtHorario.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleSave() {
        if (validate()) {
            try {
                double precio = Double.parseDouble(txtPrecio.getText());
                if (funcion == null) {
                    funcion = new Funcion();
                }
                funcion.setPelicula(peliculas.get(cbPelicula.getSelectionModel().getSelectedIndex()));
                funcion.setSala(salas.get(cbSala.getSelectionModel().getSelectedIndex()));
                funcion.setHorario(txtHorario.getText());
                funcion.setPrecio(precio);
                saved = true;
                cerrar();
            } catch (NumberFormatException e) {
                Alert alert = utilAlerts.mostrarAlerts("",
                        "El precio debe ser un número",
                        "ERROR");
                alert.show();
            }
        }
    }

    public void setFuncion(Funcion f) {
        this.funcion = f;
        if (f != null) {
            lblTitulo.setText("Editar función");
            for (int i = 0; i < peliculas.size(); i++) {
                if (peliculas.get(i).getIdpelicula() == f.getPelicula().getIdpelicula()) {
                    cbPelicula.getSelectionModel().select(i);
                }
            }
            for (int i = 0; i < salas.size(); i++) {
                if (salas.get(i).getIdsala() == f.getSala().getIdsala()) {
                    cbSala.getSelectionModel().select(i);
                }
            }
            txtHorario.setText(f.getHorario());
            txtPrecio.setText(String.valueOf(f.getPrecio()));
        }
    }
}
