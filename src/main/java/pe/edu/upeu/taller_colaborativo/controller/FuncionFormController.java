package pe.edu.upeu.taller_colaborativo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.taller_colaborativo.modelo.Funcion;
import pe.edu.upeu.taller_colaborativo.modelo.Pelicula;
import pe.edu.upeu.taller_colaborativo.modelo.Sala;

import java.util.Locale;

public class FuncionFormController {

    @FXML
    private Label lblTitulo;
    @FXML
    private ComboBox<Pelicula> cbPelicula;
    @FXML
    private ComboBox<Sala> cbSala;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ComboBox<String> cbHora;
    @FXML
    private TextField txtPrecio;
    @FXML
    private Label lblMensaje;

    private Funcion funcion;
    private boolean guardado = false;

    @FXML
    public void initialize() {
        // Se llenan con las peliculas y salas que ya estan registradas
        cbPelicula.getItems().addAll(PeliculaController.getListaPeliculas());
        cbSala.getItems().addAll(SalaController.getListaSalas());
        cbHora.getItems().addAll("12:30", "14:30", "15:30", "17:45", "19:20", "21:00", "22:10");
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;

        if (funcion == null) {
            lblTitulo.setText("Nueva función");
            return;
        }

        lblTitulo.setText("Editar función");
        cbPelicula.setValue(funcion.getPelicula());
        cbSala.setValue(funcion.getSala());
        dpFecha.setValue(funcion.getFecha());
        cbHora.setValue(funcion.getHora());
        txtPrecio.setText(String.format(Locale.US, "%.2f", funcion.getPrecio()));
    }

    public boolean isGuardado() {
        return guardado;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    @FXML
    private void handleSave() {
        Pelicula pelicula = cbPelicula.getValue();
        Sala sala = cbSala.getValue();
        String hora = cbHora.getValue();
        String precioTexto = txtPrecio.getText().trim();

        if (pelicula == null || sala == null || dpFecha.getValue() == null
                || hora == null || precioTexto.isEmpty()) {
            lblMensaje.setText("Complete todos los campos de la función.");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTexto);
            if (precio <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lblMensaje.setText("El precio debe ser un número mayor a 0.");
            return;
        }

        if (funcion == null) {
            funcion = new Funcion();
        }

        funcion.setPelicula(pelicula);
        funcion.setSala(sala);
        funcion.setFecha(dpFecha.getValue());
        funcion.setHora(hora);
        funcion.setPrecio(precio);

        guardado = true;
        cerrar();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    private void cerrar() {
        ((Stage) cbPelicula.getScene().getWindow()).close();
    }
}
