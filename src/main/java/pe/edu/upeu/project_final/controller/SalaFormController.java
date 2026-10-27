
package pe.edu.upeu.project_final.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.project_final.modelo.Sala;
import pe.edu.upeu.project_final.utils.UtilAlerts;

// AGREGADO: el formulario solo recoge datos; el controlador del listado llama al DAO.
public class SalaFormController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre, txtTipo, txtCapacidad;

    private Sala sala;
    private boolean saved = false;

    public void setSala(Sala modelo) {
        // CORREGIDO: se usa un objeto nuevo para que Cancelar no modifique la fila seleccionada.
        sala = new Sala();
        saved = false;

        if (modelo != null) {
            sala.setId(modelo.getId());
        }

        lblTitulo.setText(modelo == null ? "Nueva sala" : "Editar sala");
        txtNombre.setText(modelo == null ? "" : modelo.getNombre());
        txtTipo.setText(modelo == null ? "" : modelo.getTipo());
        txtCapacidad.setText(modelo == null ? "" : String.valueOf(modelo.getCapacidad()));
    }

    public Sala getSala() {
        return sala;
    }

    public boolean isSaved() {
        return saved;
    }

    private boolean validate() {
        if (txtNombre.getText().trim().isEmpty()
                || txtTipo.getText().trim().isEmpty()
                || txtCapacidad.getText().trim().isEmpty()) {
            UtilAlerts.mostrarAlerts("Datos requeridos",
                    "Complete todos los campos.", "WARNING").show();
            return false;
        }

        try {
            Integer.parseInt(txtCapacidad.getText().trim());
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
            sala.setNombre(txtNombre.getText().trim());
            sala.setTipo(txtTipo.getText().trim());
            sala.setCapacidad(Integer.parseInt(txtCapacidad.getText().trim()));
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
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}
