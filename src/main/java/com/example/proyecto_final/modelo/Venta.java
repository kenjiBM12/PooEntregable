package com.example.proyecto_final.modelo;
import javafx.beans.property.*;

import java.time.LocalDate;

public class Venta {

    private final IntegerProperty idventa =
            new SimpleIntegerProperty();

    private final ObjectProperty<Funcion> funcion =
            new SimpleObjectProperty<>();
    private final StringProperty cliente =
            new SimpleStringProperty();
    private final IntegerProperty cantidadAsientos =
            new SimpleIntegerProperty();
    private final DoubleProperty totalPagar =
            new SimpleDoubleProperty();
    private final ObjectProperty<LocalDate> fechaVenta = new SimpleObjectProperty<>();

    public Venta() {
    }

    public Venta(int idventa, Funcion funcion, String cliente,
                 int cantidadAsientos, double totalPagar, LocalDate fechaVenta) {
        setIdventa(idventa);
        setFuncion(funcion);
        setCliente(cliente);
        setCantidadAsientos(cantidadAsientos);
        setTotalPagar(totalPagar);
        setFechaVenta(fechaVenta);

    }

    public final LocalDate getFechaVenta() {
        return fechaVenta.get();
    }

    public final void setFechaVenta(LocalDate value) {
        fechaVenta.set(value);
    }

    public final ObjectProperty<LocalDate> fechaVentaProperty() {
        return fechaVenta;
    }

    public final int getIdventa() {
        return idventa.get();
    }

    public final void setIdventa(int value) {
        idventa.set(value);
    }

    public final IntegerProperty idventaProperty() {
        return idventa;
    }

    public final Funcion getFuncion() {
        return funcion.get();
    }

    public final void setFuncion(Funcion value) {
        funcion.set(value);
    }

    public final ObjectProperty<Funcion> funcionProperty() {
        return funcion;
    }

    public final String getCliente() {
        return cliente.get();
    }

    public final void setCliente(String value) {
        cliente.set(value);
    }

    public final StringProperty clienteProperty() {
        return cliente;
    }

    public final int getCantidadAsientos() {
        return cantidadAsientos.get();
    }

    public final void setCantidadAsientos(int value) {
        cantidadAsientos.set(value);
    }

    public final IntegerProperty cantidadAsientosProperty() {
        return cantidadAsientos;
    }

    public final double getTotalPagar() {
        return totalPagar.get();
    }

    public final void setTotalPagar(double value) {
        totalPagar.set(value);
    }

    public final DoubleProperty totalPagarProperty() {
        return totalPagar;
    }

}
