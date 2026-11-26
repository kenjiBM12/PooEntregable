package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Cliente;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.util.ArrayList;
import java.util.List;

/**
 * MEJORA 3: ventana con el historial de compras de un cliente.
 *
 * Se abre desde la pantalla de Clientes con el boton HISTORIAL. Muestra una
 * fila por boleto (igual que la pantalla de Ventas) y un resumen abajo con
 * cuantos boletos siguen activos y cuanto dinero lleva gastado el cliente.
 */
public class ClienteHistorialController {

    private static UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblResumen;
    @FXML
    private TableView<Venta> tableHistorial;
    @FXML
    private TableColumn<Venta, String> ColFecha;
    @FXML
    private TableColumn<Venta, String> ColCompra;
    @FXML
    private TableColumn<Venta, String> ColPelicula;
    @FXML
    private TableColumn<Venta, String> ColFuncion;
    @FXML
    private TableColumn<Venta, String> ColButaca;
    @FXML
    private TableColumn<Venta, String> ColPago;
    @FXML
    private TableColumn<Venta, Double> ColMonto;
    @FXML
    private TableColumn<Venta, String> ColEstado;

    private final IVentaDao ventaDao = new VentaDaoImpl();

    @FXML
    public void initialize() {
        ColFecha.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().getFechaVenta() != null
                        ? celda.getValue().getFechaVenta().toString() : ""));
        ColCompra.setCellValueFactory(
                celda -> celda.getValue().codigoCompraProperty());
        ColPelicula.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().getFuncion() != null
                        && celda.getValue().getFuncion().getPelicula() != null
                        ? celda.getValue().getFuncion().getPelicula().getTitulo() : ""));
        ColFuncion.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().getFuncion() != null
                        ? celda.getValue().getFuncion().getFecha() + "  "
                        + celda.getValue().getFuncion().getHorario() : ""));
        //la butaca se muestra con su numero global (1..capacidad), igual que en ventas
        ColButaca.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().getButaca() != null
                        ? String.valueOf(numeroVisible(celda.getValue())) : ""));
        ColPago.setCellValueFactory(
                celda -> celda.getValue().metodoPagoProperty());
        ColMonto.setCellValueFactory(
                celda -> celda.getValue().totalPagarProperty().asObject());
        ColEstado.setCellValueFactory(
                celda -> celda.getValue().estadoProperty());
    }

    /**
     * Recibe el cliente elegido en la tabla y carga sus compras.
     * Se llama despues de cargar el FXML, por eso los campos ya existen.
     */
    public void setCliente(Cliente c) {
        if (c == null) {
            return;
        }
        lblTitulo.setText("Compras de " + c.getNombre() + " "
                + (c.getApellidos() != null ? c.getApellidos() : "")
                + "  ·  DNI " + c.getDni());

        List<Venta> ventas;
        try {
            ventas = ventaDao.listarPorCliente(c.getIdcliente());
        } catch (RuntimeException e) {
            System.out.println("Error al listar las compras del cliente: " + e.getMessage());
            utilAlert.mostrarAlerts("Error",
                    "No se pudieron leer las compras del cliente.\nDetalle: " + e.getMessage(),
                    "ERROR").showAndWait();
            return;
        }
        tableHistorial.setItems(FXCollections.observableArrayList(ventas));
        mostrarResumen(ventas);
    }

    //resumen de abajo: boletos activos, anulados y dinero realmente gastado
    private void mostrarResumen(List<Venta> ventas) {
        int activos = 0;
        int anulados = 0;
        double total = 0;
        for (Venta v : ventas) {
            if ("activa".equalsIgnoreCase(v.getEstado())) {
                activos++;
                total = total + v.getTotalPagar();
            } else {
                anulados++;
            }
        }
        lblResumen.setText("Compras: " + ventas.size()
                + "   ·   Boletos activos: " + activos
                + "   ·   Anulados: " + anulados
                + "   ·   Gastado: S/ " + String.format("%.2f", total));
    }

    /**
     * Mismo calculo que en la pantalla de ventas: la butaca se guarda por
     * fila y numero, y aqui se muestra el numero corrido de la sala.
     */
    private int numeroVisible(Venta v) {
        if (v.getButaca() == null) {
            return 0;
        }
        int columnas = 0;
        if (v.getFuncion() != null && v.getFuncion().getSala() != null) {
            columnas = v.getFuncion().getSala().getColumnas();
        }
        if (columnas <= 0) {
            return v.getButaca().getNumero();
        }
        return (v.getButaca().getFila() - 1) * columnas + v.getButaca().getNumero();
    }
}
