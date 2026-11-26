package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pe.edu.upeu.gestioo_de_cine.implementacion.ReporteDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IReporteDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Reporte;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MEJORA 3: pantalla de Reportes / cierre de caja.
 *
 * El administrador elige un rango de fechas y ve:
 *   - cuantos boletos se vendieron (activos)
 *   - cuantos se anularon
 *   - cuanto dinero se cobro
 *   - cuanto dinero se anulo
 *   - el detalle de los boletos de ese rango
 *
 * Los totales los calcula el ReporteDaoImpl con SUM/CASE en SQL; la tabla se
 * llena con la lista de ventas que ya existe (VentaDaoImpl.listAll) filtrada
 * por fecha en Java.
 */
public class ReporteController {

    private static UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;

    @FXML
    private Label lblActivos;
    @FXML
    private Label lblAnulados;
    @FXML
    private Label lblIngresos;
    @FXML
    private Label lblAnulado;
    @FXML
    private Label lblNeto;

    @FXML
    private TableView<Venta> tableReporte;
    @FXML
    private TableColumn<Venta, String> ColCompra;
    @FXML
    private TableColumn<Venta, String> ColCliente;
    @FXML
    private TableColumn<Venta, String> ColPelicula;
    @FXML
    private TableColumn<Venta, String> ColFecha;
    @FXML
    private TableColumn<Venta, String> ColMetodoPago;
    @FXML
    private TableColumn<Venta, Double> ColMonto;
    @FXML
    private TableColumn<Venta, String> ColEstado;

    private final IReporteDao reporteDao = new ReporteDaoImpl();
    private final IVentaDao ventaDao = new VentaDaoImpl();

    @FXML
    public void initialize() {
        //columnas de la tabla de detalle
        ColCompra.setCellValueFactory(
                cellData -> cellData.getValue().codigoCompraProperty());
        ColCliente.setCellValueFactory(
                cellData -> cellData.getValue().clienteProperty());
        ColPelicula.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncion() != null
                        && cellData.getValue().getFuncion().getPelicula() != null
                        ? cellData.getValue().getFuncion().getPelicula().getTitulo() : ""));
        //CORREGIDO: la columna Fecha muestra la fecha de VENTA, que es la que
        //se usa para el cierre de caja (no la fecha de la funcion).
        ColFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                fechaCierre(cellData.getValue())));
        ColMetodoPago.setCellValueFactory(
                cellData -> cellData.getValue().metodoPagoProperty());
        ColMonto.setCellValueFactory(
                cellData -> cellData.getValue().totalPagarProperty().asObject());
        ColEstado.setCellValueFactory(
                cellData -> cellData.getValue().estadoProperty());

        //por defecto: desde el primer dia del mes hasta hoy
        dpDesde.setValue(LocalDate.now().withDayOfMonth(1));
        dpHasta.setValue(LocalDate.now());
        handleGenerar();
    }

    @FXML
    private void handleGenerar() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        if (desde == null || hasta == null) {
            utilAlert.mostrarAlerts("Faltan fechas",
                    "Elija la fecha de inicio y la fecha de fin.", "WARNING").showAndWait();
            return;
        }
        if (desde.isAfter(hasta)) {
            utilAlert.mostrarAlerts("Rango invalido",
                    "La fecha 'desde' no puede ser mayor que la fecha 'hasta'.",
                    "WARNING").showAndWait();
            return;
        }

        Reporte reporte;
        try {
            reporte = reporteDao.generar(desde, hasta);
        } catch (RuntimeException e) {
            System.out.println("Error al generar el reporte: " + e.getMessage());
            utilAlert.mostrarAlerts("Error",
                    "No se pudo generar el reporte.\nDetalle: " + e.getMessage(),
                    "ERROR").showAndWait();
            return;
        }

        //se muestran los totales con dos decimales
        lblActivos.setText(String.valueOf(reporte.getBoletosActivos()));
        lblAnulados.setText(String.valueOf(reporte.getBoletosAnulados()));
        lblIngresos.setText("S/ " + String.format("%.2f", reporte.getIngresos()));
        lblAnulado.setText("S/ " + String.format("%.2f", reporte.getMontoAnulado()));
        lblNeto.setText("S/ " + String.format("%.2f", reporte.getTotalNeto()));

        //detalle: se recorre la lista completa y se queda con las ventas del rango
        //CORREGIDO: el filtro tambien usa la fecha de venta, igual que los totales
        List<Venta> detalle = new ArrayList<>();
        try {
            for (Venta v : ventaDao.listAll()) {
                String fecha = fechaCierre(v);
                if (fecha.isEmpty()) {
                    continue;
                }
                if (fecha.compareTo(desde.toString()) >= 0
                        && fecha.compareTo(hasta.toString()) <= 0) {
                    detalle.add(v);
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Error al listar el detalle: " + e.getMessage());
        }
        ObservableList<Venta> lista = FXCollections.observableArrayList(detalle);
        tableReporte.setItems(lista);
    }

    //limpia el rango y vuelve al mes actual
    @FXML
    private void handleLimpiar() {
        dpDesde.setValue(LocalDate.now().withDayOfMonth(1));
        dpHasta.setValue(LocalDate.now());
        handleGenerar();
    }

    /**
     * Fecha que usa el cierre de caja: la de VENTA (cuando se cobro el boleto).
     * Si una venta muy vieja no la tiene guardada, se usa la fecha de la funcion
     * para que no desaparezca del reporte.
     */
    private String fechaCierre(Venta v) {
        if (v.getFechaVenta() != null) {
            return v.getFechaVenta().toString();
        }
        if (v.getFuncion() != null && v.getFuncion().getFecha() != null) {
            return soloFecha(v.getFuncion().getFecha());
        }
        return "";
    }

    //de "2026-11-20 18:30" toma solo "2026-11-20"
    private String soloFecha(String fecha) {
        String texto = fecha.trim();
        return texto.length() > 10 ? texto.substring(0, 10) : texto;
    }
}
