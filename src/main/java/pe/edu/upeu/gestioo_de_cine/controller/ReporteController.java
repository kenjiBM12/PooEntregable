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
import pe.edu.upeu.gestioo_de_cine.modelo.ResumenPago;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private TableView<ResumenPago> tablePago;
    @FXML
    private TableColumn<ResumenPago, String> ColPagoMetodo;
    @FXML
    private TableColumn<ResumenPago, Integer> ColPagoBoletos;
    @FXML
    private TableColumn<ResumenPago, Double> ColPagoMonto;

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

    private final List<Venta> ultimoDetalle = new ArrayList<>();

    @FXML
    public void initialize() {
        ColCompra.setCellValueFactory(
                cellData -> cellData.getValue().codigoCompraProperty());
        ColCliente.setCellValueFactory(
                cellData -> cellData.getValue().clienteProperty());
        ColPelicula.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncion() != null
                        && cellData.getValue().getFuncion().getPelicula() != null
                        ? cellData.getValue().getFuncion().getPelicula().getTitulo() : ""));
        ColFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                fechaCierre(cellData.getValue())));
        ColMetodoPago.setCellValueFactory(
                cellData -> cellData.getValue().metodoPagoProperty());
        ColMonto.setCellValueFactory(
                cellData -> cellData.getValue().totalPagarProperty().asObject());
        ColEstado.setCellValueFactory(
                cellData -> cellData.getValue().estadoProperty());

        ColPagoMetodo.setCellValueFactory(celda -> celda.getValue().metodoProperty());
        ColPagoBoletos.setCellValueFactory(
                celda -> celda.getValue().boletosProperty().asObject());
        ColPagoMonto.setCellValueFactory(
                celda -> celda.getValue().montoProperty().asObject());

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

        lblActivos.setText(String.valueOf(reporte.getBoletosActivos()));
        lblAnulados.setText(String.valueOf(reporte.getBoletosAnulados()));
        lblIngresos.setText("S/ " + String.format("%.2f", reporte.getIngresos()));
        lblAnulado.setText("S/ " + String.format("%.2f", reporte.getMontoAnulado()));
        lblNeto.setText("S/ " + String.format("%.2f", reporte.getTotalNeto()));

        tablePago.setItems(FXCollections.observableArrayList(reporte.getPorMetodoPago()));

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
        ultimoDetalle.clear();
        ultimoDetalle.addAll(detalle);
        ObservableList<Venta> lista = FXCollections.observableArrayList(detalle);
        tableReporte.setItems(lista);
    }

    @FXML
    private void handleLimpiar() {
        dpDesde.setValue(LocalDate.now().withDayOfMonth(1));
        dpHasta.setValue(LocalDate.now());
        handleGenerar();
    }

    @FXML
    private void handleExportarCsv() {
        if (dpDesde.getValue() == null || dpHasta.getValue() == null) {
            utilAlert.mostrarAlerts("Faltan fechas",
                    "Genere primero el cierre con sus fechas.", "WARNING").showAndWait();
            return;
        }
        try {
            File carpeta = new File(ConexionDB.archivoBase().getParentFile(), "reportes");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }
            File archivo = new File(carpeta, "cierre_"
                    + dpDesde.getValue() + "_" + dpHasta.getValue() + ".csv");

            StringBuilder csv = new StringBuilder();
            csv.append('\uFEFF');
            csv.append("Compra;Cliente;DNI;Pelicula;Fecha venta;Forma de pago;Monto;Estado\n");
            for (Venta v : ultimoDetalle) {
                csv.append(csv(v.getCodigoCompra())).append(';')
                        .append(csv(v.getCliente())).append(';')
                        .append(csv(v.getDniCliente())).append(';')
                        .append(csv(tituloPelicula(v))).append(';')
                        .append(csv(fechaCierre(v))).append(';')
                        .append(csv(v.getMetodoPago())).append(';')
                        .append(String.format("%.2f", v.getTotalPagar())).append(';')
                        .append(csv(v.getEstado())).append('\n');
            }
            Files.writeString(archivo.toPath(), csv.toString(), StandardCharsets.UTF_8);
            utilAlert.mostrarAlerts("Cierre exportado",
                    "El archivo se guardo en:\n" + archivo.getAbsolutePath(),
                    "INFORMATION").showAndWait();
        } catch (Exception e) {
            System.out.println("No se pudo exportar el cierre: " + e.getMessage());
            utilAlert.mostrarAlerts("Error",
                    "No se pudo guardar el archivo CSV.\nDetalle: " + e.getMessage(),
                    "ERROR").showAndWait();
        }
    }

    private String fechaCierre(Venta v) {
        if (v.getFechaVenta() != null) {
            return v.getFechaVenta().toString();
        }
        if (v.getFuncion() != null && v.getFuncion().getFecha() != null) {
            return soloFecha(v.getFuncion().getFecha());
        }
        return "";
    }

    private String soloFecha(String fecha) {
        String texto = fecha.trim();
        return texto.length() > 10 ? texto.substring(0, 10) : texto;
    }

    private String tituloPelicula(Venta v) {
        if (v.getFuncion() != null && v.getFuncion().getPelicula() != null) {
            return v.getFuncion().getPelicula().getTitulo();
        }
        return "";
    }

    private String csv(String valor) {
        if (valor == null) {
            return "\"\"";
        }
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
}
