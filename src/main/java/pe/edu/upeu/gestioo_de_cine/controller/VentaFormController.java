package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.implementacion.FuncionDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.time.LocalDate;
import java.util.Locale;

public class VentaFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;
    @FXML
    private ComboBox<Funcion> cbFuncion;
    @FXML
    private TextField txtCliente, txtCantidad, txtTotal;
    @FXML
    private DatePicker dpFecha;

    private Venta venta;
    private boolean saved = false;

    private final IFuncionDao funcionDao = new FuncionDaoImpl();
    private final IVentaDao ventaDao = new VentaDaoImpl();

    @FXML
    public void initialize() {
        cbFuncion.getItems().setAll(funcionDao.listAll());
        dpFecha.setValue(LocalDate.now());
        txtTotal.setEditable(false);
        //cada cambio en la funcion o en la cantidad recalcula el total
        cbFuncion.valueProperty().addListener((obs, oldValue, newValue) -> calcularTotal());
        txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> calcularTotal());
    }

    public boolean isSaved() {
        return saved;
    }

    public Venta getVenta() {
        return venta;
    }

    private void calcularTotal() {
        Funcion f = cbFuncion.getValue();
        if (f == null) {
            txtTotal.setText("0.00");
            return;
        }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double total = cantidad * f.getPrecio();
            txtTotal.setText(String.format(Locale.US, "%.2f", total));
        } catch (NumberFormatException e) {
            txtTotal.setText("0.00");
        }
    }

    private boolean validate() {
        if (cbFuncion.getValue() == null
                || txtCliente.getText().isBlank()
                || txtCantidad.getText().isBlank()
                || dpFecha.getValue() == null) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Completa todos los campos obligatorios.", "ERROR");
            alert.show();
            return false;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            utilAlerts.mostrarAlerts("Cantidad invalida",
                    "Ingresa un numero entero de asientos.", "WARNING").showAndWait();
            return false;
        }
        if (cantidad <= 0) {
            utilAlerts.mostrarAlerts("Cantidad invalida",
                    "La cantidad de asientos debe ser mayor que cero.", "WARNING").showAndWait();
            return false;
        }
        //no se puede vender mas de la capacidad de la sala
        if (cbFuncion.getValue().getSala() != null
                && cantidad > cbFuncion.getValue().getSala().getCapacidad()) {
            utilAlerts.mostrarAlerts("Capacidad excedida",
                    "La sala solo tiene " + cbFuncion.getValue().getSala().getCapacidad()
                            + " asientos.", "WARNING").showAndWait();
            return false;
        }
        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) txtCliente.getScene().getWindow();
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
        if (venta == null) {
            venta = new Venta();
        }
        venta.setFuncion(cbFuncion.getValue());
        venta.setCliente(txtCliente.getText().trim());
        venta.setCantidadAsientos(Integer.parseInt(txtCantidad.getText().trim()));
        venta.setTotalPagar(Double.parseDouble(txtTotal.getText()));
        venta.setFechaVenta(dpFecha.getValue());
        saved = true;
        cerrar();
    }

    public void setVenta(Venta v) {
        this.venta = v;
        if (v != null) {
            lblTitulo.setText("Editar Venta");
            if (v.getFuncion() != null) {
                for (Funcion f : cbFuncion.getItems()) {
                    if (f.getIdfuncion() == v.getFuncion().getIdfuncion()) {
                        cbFuncion.setValue(f);
                        break;
                    }
                }
            }
            txtCliente.setText(v.getCliente());
            txtCantidad.setText(String.valueOf(v.getCantidadAsientos()));
            txtTotal.setText(String.valueOf(v.getTotalPagar()));
            if (v.getFechaVenta() != null) {
                dpFecha.setValue(v.getFechaVenta());
            }
        }
    }
}
