
package pe.edu.upeu.project_final.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.project_final.modelo.Funcion;
import pe.edu.upeu.project_final.utils.UtilAlerts;

// AGREGADO: el formulario solo recoge datos; el controlador del listado llama al DAO.
public class FuncionFormController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtIdPelicula, txtIdSala, txtHorario, txtPrecio;

    private Funcion funcion;
    private boolean saved = false;

    public void setFuncion(Funcion modelo) {
        // CORREGIDO: se usa un objeto nuevo para que Cancelar no modifique la fila seleccionada.
        funcion = new Funcion();
        saved = false;

        if (modelo != null) {
            funcion.setId(modelo.getId());
        }

        lblTitulo.setText(modelo == null ? "Nueva función" : "Editar función");
        txtIdPelicula.setText(modelo == null ? "" : String.valueOf(modelo.getIdPelicula()));
        txtIdSala.setText(modelo == null ? "" : String.valueOf(modelo.getIdSala()));
        txtHorario.setText(modelo == null ? "" : modelo.getHorario());
        txtPrecio.setText(modelo == null ? "" : String.valueOf(modelo.getPrecio()));
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public boolean isSaved() {
        return saved;
    }

    private boolean validate() {
        if (txtIdPelicula.getText().trim().isEmpty()
                || txtIdSala.getText().trim().isEmpty()
                || txtHorario.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()) {
            UtilAlerts.mostrarAlerts("Datos requeridos",
                    "Complete todos los campos.", "WARNING").show();
            return false;
        }

        try {
            Integer.parseInt(txtIdPelicula.getText().trim());
            Integer.parseInt(txtIdSala.getText().trim());
            Double.parseDouble(txtPrecio.getText().trim());
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
            funcion.setIdPelicula(Integer.parseInt(txtIdPelicula.getText().trim()));
            funcion.setIdSala(Integer.parseInt(txtIdSala.getText().trim()));
            funcion.setHorario(txtHorario.getText().trim());
            funcion.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
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
        Stage stage = (Stage) txtIdPelicula.getScene().getWindow();
        stage.close();
    }
}