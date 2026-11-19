package pe.edu.upeu.taller_colaborativo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.taller_colaborativo.modelo.Pelicula;

public class PeliculaFormController {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtTitulo;
    @FXML
    private ComboBox<String> cbGenero;
    @FXML
    private TextField txtDuracion;
    @FXML
    private ComboBox<String> cbClasificacion;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private Label lblMensaje;

    private Pelicula pelicula;
    private boolean guardado = false;

    @FXML
    public void initialize() {
        cbGenero.getItems().addAll("Acción", "Animación", "Aventura", "Ciencia ficción",
                "Comedia", "Drama", "Terror");
        cbClasificacion.getItems().addAll("TP", "+14", "+16", "+18");
        cbEstado.getItems().addAll("Activa", "Inactiva");
        cbEstado.getSelectionModel().selectFirst();
    }

    // Si recibe una pelicula se llenan los campos, si es null el formulario queda vacio
    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;

        if (pelicula == null) {
            lblTitulo.setText("Nueva película");
            return;
        }

        lblTitulo.setText("Editar película");
        txtTitulo.setText(pelicula.getTitulo());
        cbGenero.setValue(pelicula.getGenero());
        txtDuracion.setText(String.valueOf(pelicula.getDuracion()));
        cbClasificacion.setValue(pelicula.getClasificacion());
        cbEstado.setValue(pelicula.getEstado());
    }

    public boolean isGuardado() {
        return guardado;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    @FXML
    private void handleSave() {
        String titulo = txtTitulo.getText().trim();
        String genero = cbGenero.getValue();
        String duracionTexto = txtDuracion.getText().trim();
        String clasificacion = cbClasificacion.getValue();

        if (titulo.isEmpty() || genero == null || clasificacion == null
                || duracionTexto.isEmpty() || cbEstado.getValue() == null) {
            lblMensaje.setText("Complete todos los campos de la película.");
            return;
        }

        int duracion;
        try {
            duracion = Integer.parseInt(duracionTexto);
            if (duracion <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lblMensaje.setText("La duración debe ser un número de minutos mayor a 0.");
            return;
        }

        if (pelicula == null) {
            pelicula = new Pelicula();
        }

        pelicula.setTitulo(titulo);
        pelicula.setGenero(genero);
        pelicula.setDuracion(duracion);
        pelicula.setClasificacion(clasificacion);
        pelicula.setEstado(cbEstado.getValue());

        guardado = true;
        cerrar();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    private void cerrar() {
        ((Stage) txtTitulo.getScene().getWindow()).close();
    }
}
