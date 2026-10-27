package pe.edu.upeu.project_final.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pe.edu.upeu.project_final.implementacion.VentaDaoImpl;
import pe.edu.upeu.project_final.interfaces.IVentaDao;
import pe.edu.upeu.project_final.modelo.Venta;
import pe.edu.upeu.project_final.utils.UtilAlerts;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Locale;

// CORREGIDO: el reporte muestra los datos de Venta directamente, sin consultas a otras tablas.
public class ReporteController {

    @FXML private TextField txtFecha;
    @FXML private Label lblTotal, lblEntradas, lblOperaciones, lblEstado;
    @FXML private TableView<Venta> tableReporte;
    @FXML private TableColumn<Venta, Integer> ColId, ColFuncion, ColCantidad;
    @FXML private TableColumn<Venta, String> ColCliente, ColFecha;
    @FXML private TableColumn<Venta, Double> ColTotal;

    private final IVentaDao dao = new VentaDaoImpl();
    private ObservableList<Venta> listaVentas;

    @FXML
    public void initialize() {
        ColId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        ColFuncion.setCellValueFactory(c -> c.getValue().idFuncionProperty().asObject());
        ColCliente.setCellValueFactory(c -> c.getValue().clienteProperty());
        ColCantidad.setCellValueFactory(c -> c.getValue().cantidadAsientosProperty().asObject());
        ColTotal.setCellValueFactory(c -> c.getValue().totalPagarProperty().asObject());
        ColFecha.setCellValueFactory(c -> c.getValue().fechaVentaProperty());
        txtFecha.setText(LocalDate.now(ZoneId.of("America/Lima")).toString());
        cargarDatos();
    }

    private void cargarDatos() {
        LocalDate fecha;

        try {
            fecha = LocalDate.parse(txtFecha.getText().trim());
        } catch (DateTimeParseException e) {
            UtilAlerts.mostrarAlerts("Fecha inválida",
                    "Ingrese una fecha con formato aaaa-mm-dd.", "ERROR").show();
            return;
        }

        try {
            listaVentas = FXCollections.observableArrayList(dao.listByDate(fecha));
            tableReporte.setItems(listaVentas);
            double total = 0;
            int entradas = 0;

            for (Venta venta : listaVentas) {
                total = total + venta.getTotalPagar();
                entradas = entradas + venta.getCantidadAsientos();
            }

            lblTotal.setText(String.format(Locale.ROOT, "Total vendido: S/ %.2f", total));
            lblEntradas.setText("Entradas vendidas: " + entradas);
            lblOperaciones.setText("Ventas registradas: " + listaVentas.size());
            lblEstado.setText("Fecha de venta: " + fecha);
        } catch (RuntimeException e) {
            // CORREGIDO: se informa un fallo de consulta sin mostrarlo como cero ventas.
            lblEstado.setText("No se pudo cargar el reporte.");
            UtilAlerts.mostrarAlerts("Error de consulta", e.getMessage(), "ERROR").show();
        }
    }

    @FXML
    private void handleActualizar() {
        cargarDatos();
    }
}
