package com.example.proyecto_final.controller;

import com.example.proyecto_final.implementacion.VentaDaoImpl;
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.time.LocalDate;

public class ReporteController {
    private final UtilAlerts utilAlert = new UtilAlerts();
    private final IVentaDao ventaDao = new VentaDaoImpl();
    private ObservableList<Venta> listaVentas;
    @FXML
    private TextField txtFecha;
    @FXML
    private Label lblTotal, lblEntradas, lblOperaciones, lblEstado;
    @FXML
    private TableView<Venta> tableReporte;
    @FXML
    private TableColumn<Venta, Integer> ColId, ColCantidad;
    @FXML
    private TableColumn<Venta, String> ColPelicula, ColCliente, ColHorario;
    @FXML
    private TableColumn<Venta, Double> ColTotal;

    @FXML
    public void initialize() {
        ColId.setCellValueFactory(cellData ->
                cellData.getValue().idventaProperty().asObject());
        ColPelicula.setCellValueFactory(cellData ->
                cellData.getValue().getFuncion().getPelicula().tituloProperty());
        ColCliente.setCellValueFactory(cellData ->
                cellData.getValue().clienteProperty());
        ColCantidad.setCellValueFactory(cellData ->
                cellData.getValue().cantidadAsientosProperty().asObject());
        ColTotal.setCellValueFactory(cellData ->
                cellData.getValue().totalPagarProperty().asObject());
        ColHorario.setCellValueFactory(cellData ->
                cellData.getValue().getFuncion().horarioProperty());
        txtFecha.setText(LocalDate.now().toString());
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            LocalDate fecha = LocalDate.parse(txtFecha.getText());
            listaVentas = FXCollections.observableArrayList(ventaDao.listByDate(fecha));
            tableReporte.setItems(listaVentas);
            double total = 0;
            int entradas = 0;
            for (Venta venta : listaVentas) {
                total = total + venta.getTotalPagar();
                entradas = entradas + venta.getCantidadAsientos();
            }
            lblTotal.setText("Total vendido: S/ " + total);
            lblEntradas.setText("Entradas vendidas: " + entradas);
            lblOperaciones.setText("Ventas registradas: " + listaVentas.size());
            lblEstado.setText("Fecha: " + fecha);
        } catch (Exception e) {
            listaVentas = FXCollections.observableArrayList();
            tableReporte.setItems(listaVentas);
            lblTotal.setText("Total vendido: S/ 0");
            lblEntradas.setText("Entradas vendidas: 0");
            lblOperaciones.setText("Ventas registradas: 0");
            lblEstado.setText("Ingrese una fecha con formato aaaa-mm-dd");
            Alert alert = utilAlert.mostrarAlerts("Error",
                    "Ingrese una fecha con formato aaaa-mm-dd", "ERROR");
            alert.show();
        }
    }

    @FXML
    private void handleActualizar() {
        cargarDatos();
    }
}