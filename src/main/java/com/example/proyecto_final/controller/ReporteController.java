package com.example.proyecto_final.controller;

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

import java.util.Locale;

public class ReporteController {

    private final IVentaDao ventaDao = new VentaDaoImpl();
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private ObservableList<Venta> listaVentas;

    @FXML private DatePicker dpFecha;

    @FXML private Label lblTotal, lblEntradas, lblOperaciones, lblEstado;

    @FXML private TableView<Venta> tablaReportes;

    @FXML private TableColumn<Venta, Integer> colIdVenta, colCantidad;

    @FXML private TableColumn<Venta, String> colPelicula, colCliente;
    @FXML private TableColumn<Venta, LocalDate> colHorario;

    @FXML private TableColumn<Venta, Double> colTotal;

    @FXML

    public void initialize() {
        tablaReportes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colIdVenta.setCellValueFactory(cellData -> cellData.getValue().idventaProperty().asObject());
        colPelicula.setCellValueFactory(cellData -> cellData.getValue().getFuncion().getPelicula().tituloProperty());
        colCliente.setCellValueFactory(cellData -> cellData.getValue().clienteProperty());
        colCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadAsientosProperty().asObject());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalPagarProperty().asObject());

        colHorario.setCellValueFactory(cellData -> cellData.getValue().getFuncion().horarioProperty());
        colTotal.setCellFactory(column -> new TableCell<>() {
            @Override

            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format(Locale.US, "%.2f", item));
            }
        });
        dpFecha.setValue(LocalDate.now());
        dpFecha.setOnAction(event -> cargarDatos());
        cargarDatos();
    }

    @FXML

    private void handleActualizar() {
        cargarDatos();
    }

    private void cargarDatos() {
        if (dpFecha.getValue() == null) {
            mostrarSinDatos("Seleccione una fecha");
            return;
        }
        try {
            listaVentas = FXCollections.observableArrayList(ventaDao.listByDate(dpFecha.getValue()));
            tablaReportes.setItems(listaVentas);

            BigDecimal total = BigDecimal.ZERO;
            long entradas = 0;
            for (Venta v : listaVentas) {
                total = total.add(BigDecimal.valueOf(v.getTotalPagar()));
                entradas += v.getCantidadAsientos();
            }
            lblTotal.setText(String.format(Locale.US, "Total vendido: S/ %.2f", total));
            lblEntradas.setText("Entradas vendidas: " + entradas);
            lblOperaciones.setText("Ventas registradas: " + listaVentas.size());
            lblEstado.setText("Fecha: " + dpFecha.getValue());
            tablaReportes.setPlaceholder(new Label("No hay ventas registradas para esta fecha"));
        } catch (RuntimeException e) {
            mostrarSinDatos("No se pudo cargar el reporte");
            e.printStackTrace();
            utilAlerts.mostrarAlerts("Error", e.getMessage(), "ERROR").showAndWait();
        }
    }

    private void mostrarSinDatos(String mensaje) {
        tablaReportes.setItems(FXCollections.observableArrayList());
        tablaReportes.setPlaceholder(new Label(mensaje));
        lblTotal.setText("Total vendido: —");
        lblEntradas.setText("Entradas vendidas: —");
        lblOperaciones.setText("Ventas registradas: —");
        lblEstado.setText(mensaje);
    }
}
