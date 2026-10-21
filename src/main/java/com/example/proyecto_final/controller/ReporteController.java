// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.controller;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.implementacion.VentaDaoImpl;
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * CONTROLADOR DEL REPORTE: conecta reporte-view.fxml con el DAO de ventas.
 * Carga las ventas de una fecha, llena la tabla y calcula los resúmenes.
 * No contiene SQL: la consulta por fecha está en VentaDaoImpl.
 */
public class ReporteController {
    // Se programa contra la interfaz; DaoImpl es la implementación concreta que ejecuta el SQL.
    private final IVentaDao ventaDao = new VentaDaoImpl();
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private ObservableList<Venta> listaVentas;
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    // Selector de fecha: su valor es LocalDate, no un String.
    @FXML private DatePicker dpFecha;
    // Texto visible de la vista que este controlador puede cambiar.
    @FXML private Label lblTotal, lblEntradas, lblOperaciones, lblEstado;
    // La tabla del FXML se inyecta aquí: su fx:id debe coincidir con este nombre.
    @FXML private TableView<Venta> tablaReportes;
    // Columna tipada: el primer tipo es el modelo de la fila; el segundo es el dato mostrado.
    @FXML private TableColumn<Venta, Integer> colIdVenta, colCantidad;
    // Columna tipada: el primer tipo es el modelo de la fila; el segundo es el dato mostrado.
    @FXML private TableColumn<Venta, String> colPelicula, colCliente, colHora;
    // Columna tipada: el primer tipo es el modelo de la fila; el segundo es el dato mostrado.
    @FXML private TableColumn<Venta, Double> colTotal;

    @FXML
    /**
     * Se ejecuta automáticamente después de cargar e inyectar el FXML.
     * Configura las columnas o controles y carga los primeros datos.
     */
    public void initialize() {
        tablaReportes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        // La expresión lambda toma una fila y devuelve la propiedad que esa columna debe mostrar.
        // asObject() adapta la propiedad numérica a Integer/Double para el tipo de TableColumn.
        colIdVenta.setCellValueFactory(cellData -> cellData.getValue().idventaProperty().asObject());
        colPelicula.setCellValueFactory(cellData -> cellData.getValue().getFuncion().getPelicula().tituloProperty());
        colCliente.setCellValueFactory(cellData -> cellData.getValue().clienteProperty());
        colCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadAsientosProperty().asObject());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalPagarProperty().asObject());
        colHora.setCellValueFactory(cellData -> cellData.getValue().horaVentaProperty().asString());
        colHora.setCellFactory(column -> new TableCell<>() {
            @Override
            /**
             * JavaFX reutiliza celdas y llama aquí al cambiar su contenido.
             * Se limpia el texto en filas vacías para no mostrar datos de otra fila.
             */
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                Venta v = getTableRow() == null ? null : getTableRow().getItem();
                setText(empty || v == null || v.getHoraVenta() == null ? null : v.getHoraVenta().format(HORA));
            }
        });
        colTotal.setCellFactory(column -> new TableCell<>() {
            @Override
            /**
             * JavaFX reutiliza celdas y llama aquí al cambiar su contenido.
             * Se limpia el texto en filas vacías para no mostrar datos de otra fila.
             */
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format(Locale.US, "%.2f", item));
            }
        });
        dpFecha.setValue(LocalDate.now(ZoneId.of("America/Lima")));
        dpFecha.setOnAction(event -> cargarDatos());
        cargarDatos();
    }

    @FXML
    /**
     * Vuelve a consultar el reporte para la fecha seleccionada.
     */
    private void handleActualizar() {
        cargarDatos();
    }

    /**
     * Consulta el DAO y actualiza los elementos visibles de la tabla o reporte.
     * Se llama al iniciar y al terminar las operaciones que cambian los datos.
     */
    private void cargarDatos() {
        if (dpFecha.getValue() == null) {
            mostrarSinDatos("Seleccione una fecha");
            return;
        }
        try {
            listaVentas = FXCollections.observableArrayList(ventaDao.listByDate(dpFecha.getValue()));
            tablaReportes.setItems(listaVentas);
            // Acumula importes del reporte a partir de los totales guardados de cada venta.
            BigDecimal total = BigDecimal.ZERO;
            long entradas = 0;
            for (Venta v : listaVentas) {
                total = total.add(BigDecimal.valueOf(v.getTotalPagar()));
                entradas += v.getCantidadAsientos();
            }
            lblTotal.setText(String.format(Locale.US, "Total vendido: S/ %.2f", total));
            lblEntradas.setText("Entradas vendidas: " + entradas);
            lblOperaciones.setText("Ventas registradas: " + listaVentas.size());
            lblEstado.setText("Fecha: " + dpFecha.getValue() + " · Hora de Perú (America/Lima)");
            tablaReportes.setPlaceholder(new Label("No hay ventas registradas para esta fecha"));
        } catch (RuntimeException e) {
            mostrarSinDatos("No se pudo cargar el reporte");
            e.printStackTrace();
            utilAlerts.mostrarAlerts("Error", e.getMessage(), "ERROR").showAndWait();
        }
    }

    /**
     * Limpia la tabla y muestra un estado sin datos disponibles.
     * Distingue una selección inválida o un error de un total real igual a cero.
     */
    private void mostrarSinDatos(String mensaje) {
        tablaReportes.setItems(FXCollections.observableArrayList());
        tablaReportes.setPlaceholder(new Label(mensaje));
        lblTotal.setText("Total vendido: —");
        lblEntradas.setText("Entradas vendidas: —");
        lblOperaciones.setText("Ventas registradas: —");
        lblEstado.setText(mensaje);
    }
}
