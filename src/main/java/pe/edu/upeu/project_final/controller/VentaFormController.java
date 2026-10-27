
package pe.edu.upeu.project_final.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.project_final.modelo.Venta;
import pe.edu.upeu.project_final.utils.UtilAlerts;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

// AGREGADO: el formulario solo recoge datos; el controlador del listado llama al DAO.
public class VentaFormController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtIdFuncion, txtCliente, txtCantidad, txtTotal, txtFecha;

    private Venta venta;
    private boolean saved = false;

    public void setVenta(Venta modelo) {
        // CORREGIDO: se usa un objeto nuevo para que Cancelar no modifique la fila seleccionada.
        venta = new Venta();
        saved = false;

        if (modelo != null) {
            venta.setId(modelo.getId());
        }

        lblTitulo.setText(modelo == null ? "Nueva venta" : "Editar venta");
        txtIdFuncion.setText(modelo == null ? "" : String.valueOf(modelo.getIdFuncion()));
        txtCliente.setText(modelo == null ? "" : modelo.getCliente());
        txtCantidad.setText(modelo == null ? "" : String.valueOf(modelo.getCantidadAsientos()));
        txtTotal.setText(modelo == null ? "" : String.valueOf(modelo.getTotalPagar()));
        txtFecha.setText(modelo == null ? LocalDateTime.now(ZoneId.of("America/Lima"))
                .format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")) : modelo.getFechaVenta());
    }

    public Venta getVenta() {
        return venta;
    }

    public boolean isSaved() {
        return saved;
    }

    private boolean validate() {
        if (txtIdFuncion.getText().trim().isEmpty()
                || txtCliente.getText().trim().isEmpty()
                || txtCantidad.getText().trim().isEmpty()
                || txtTotal.getText().trim().isEmpty()
                || txtFecha.getText().trim().isEmpty()) {
            UtilAlerts.mostrarAlerts("Datos requeridos",
                    "Complete todos los campos.", "WARNING").show();
            return false;
        }

        try {
            Integer.parseInt(txtIdFuncion.getText().trim());
            Integer.parseInt(txtCantidad.getText().trim());
            Double.parseDouble(txtTotal.getText().trim());
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
            venta.setIdFuncion(Integer.parseInt(txtIdFuncion.getText().trim()));
            venta.setCliente(txtCliente.getText().trim());
            venta.setCantidadAsientos(Integer.parseInt(txtCantidad.getText().trim()));
            venta.setTotalPagar(Double.parseDouble(txtTotal.getText().trim()));
            venta.setFechaVenta(txtFecha.getText().trim());
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
        Stage stage = (Stage) txtIdFuncion.getScene().getWindow();
        stage.close();
    }
}
