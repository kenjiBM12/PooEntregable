
package pe.edu.upeu.project_final.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.project_final.modelo.Pelicula;
import pe.edu.upeu.project_final.utils.UtilAlerts;

// AGREGADO: el formulario solo recoge datos; el controlador del listado llama al DAO.
public class PeliculaFormController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtTitulo, txtGenero, txtDuracion, txtClasificacion;

    private Pelicula pelicula;
    private boolean saved = false;

    public void setPelicula(Pelicula modelo) {
        // CORREGIDO: se usa un objeto nuevo para que Cancelar no modifique la fila seleccionada.
        pelicula = new Pelicula();
        saved = false;

        if (modelo != null) {
            pelicula.setId(modelo.getId());
        }

        lblTitulo.setText(modelo == null ? "Nueva película" : "Editar película");
        txtTitulo.setText(modelo == null ? "" : modelo.getTitulo());
        txtGenero.setText(modelo == null ? "" : modelo.getGenero());
        txtDuracion.setText(modelo == null ? "" : String.valueOf(modelo.getDuracionMin()));
        txtClasificacion.setText(modelo == null ? "" : modelo.getClasificacion());
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public boolean isSaved() {
        return saved;
    }

    private boolean validate() {
        if (txtTitulo.getText().trim().isEmpty()
                || txtGenero.getText().trim().isEmpty()
                || txtDuracion.getText().trim().isEmpty()
                || txtClasificacion.getText().trim().isEmpty()) {
            UtilAlerts.mostrarAlerts("Datos requeridos",
                    "Complete todos los campos.", "WARNING").show();
            return false;
        }

        try {
            Integer.parseInt(txtDuracion.getText().trim());
        } catch (NumberFormatException e) {
            UtilAlerts.mostrarAlerts("Datos inválidos",
                    "Revise los campos numéricos.", "ERROR").show();
            return false;
        }

        return true;
    }

    @FXML
    private void handleSave() {
        if (validate()) {
            pelicula.setTitulo(txtTitulo.getText().trim());
            pelicula.setGenero(txtGenero.getText().trim());
            pelicula.setDuracionMin(Integer.parseInt(txtDuracion.getText().trim()));
            pelicula.setClasificacion(txtClasificacion.getText().trim());
            saved = true;
            cerrar();
        }
    }

    @FXML
    private void handleCancel() {
        saved = false;
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }
}
