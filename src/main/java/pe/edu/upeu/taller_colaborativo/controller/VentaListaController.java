package pe.edu.upeu.taller_colaborativo.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pe.edu.upeu.taller_colaborativo.modelo.Funcion;
import pe.edu.upeu.taller_colaborativo.modelo.Venta;
import pe.edu.upeu.taller_colaborativo.utils.UtilAlerts;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class VentaListaController {

    @FXML
    private TableView<Venta> tableVenta;
    @FXML
    private TableColumn<Venta, Number> colNumero;
    @FXML
    private TableColumn<Venta, Integer> colId;
    @FXML
    private TableColumn<Venta, String> colPelicula;
    @FXML
    private TableColumn<Venta, String> colFuncion;
    @FXML
    private TableColumn<Venta, String> colButacas;
    @FXML
    private TableColumn<Venta, Double> colTotal;
    @FXML
    private TableColumn<Venta, LocalDate> colFecha;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Ventas de la sesion. Es static para que se conserven al cambiar de vista
    private static final ObservableList<Venta> ventas = FXCollections.observableArrayList();

    // La llama la venta de entradas cada vez que se confirma una compra
    public static void registrar(Funcion funcion, String butacas, double total) {
        ventas.add(new Venta(siguienteId(), funcion, butacas, total, LocalDate.now()));
    }

    // Butacas ocupadas de una funcion: son las de sus ventas registradas
    public static Set<String> butacasVendidasDe(Funcion funcion) {
        Set<String> butacas = new HashSet<>();

        for (Venta venta : ventas) {
            if (venta.getFuncion().equals(funcion)) {
                butacas.addAll(Arrays.asList(venta.getButacas().split(", ")));
            }
        }
        return butacas;
    }

    // Resumen de ventas que muestra el panel de inicio
    public static double totalRecaudado() {
        return ventas.stream().mapToDouble(Venta::getTotal).sum();
    }

    public static int entradasVendidas() {
        return ventas.stream().mapToInt(Venta::getCantidadButacas).sum();
    }

    // Cuantas ventas tiene una funcion, para avisar antes de eliminarla
    public static int ventasDe(Funcion funcion) {
        return (int) ventas.stream().filter(venta -> venta.getFuncion().equals(funcion)).count();
    }

    // Se usa cuando se elimina una funcion, para no dejar sus ventas sueltas
    public static void eliminarPorFuncion(Funcion funcion) {
        ventas.removeIf(venta -> venta.getFuncion().equals(funcion));
    }

    private static int siguienteId() {
        return ventas.stream().mapToInt(Venta::getIdVenta).max().orElse(0) + 1;
    }

    @FXML
    public void initialize() {
        colNumero.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio ? null : String.valueOf(getIndex() + 1));
            }
        });

        colId.setCellValueFactory(dato -> dato.getValue().idVentaProperty().asObject());
        colPelicula.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().getFuncion().getPelicula().getTitulo()));
        colFuncion.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().getFuncion().getDescripcion()));
        colButacas.setCellValueFactory(dato -> dato.getValue().butacasProperty());
        colTotal.setCellValueFactory(dato -> dato.getValue().totalProperty().asObject());
        colFecha.setCellValueFactory(dato -> dato.getValue().fechaProperty());

        // El total se muestra como S/ 36.00 y la fecha como dd/MM/yyyy
        colTotal.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Double total, boolean vacio) {
                super.updateItem(total, vacio);
                setText(vacio || total == null ? null : String.format(Locale.US, "S/ %.2f", total));
            }
        });

        colFecha.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean vacio) {
                super.updateItem(fecha, vacio);
                setText(vacio || fecha == null ? null : fecha.format(FORMATO_FECHA));
            }
        });

        tableVenta.setItems(ventas);
    }

    @FXML
    private void handleAnular() {
        Venta seleccionada = tableVenta.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            UtilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione una venta de la tabla para anularla.",
                    "WARNING").showAndWait();
            return;
        }

        Alert confirmacion = UtilAlerts.mostrarAlerts("Confirmar anulación",
                "¿Anular la venta de las butacas " + seleccionada.getButacas() + "?",
                "CONFIRMATION");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Al quitar la venta, sus butacas quedan libres otra vez
            ventas.remove(seleccionada);
        }
    }
}
