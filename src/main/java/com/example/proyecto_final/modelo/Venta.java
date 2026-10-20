package com.example.proyecto_final.modelo;

import javafx.beans.property.*;

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

    private final StringProperty fechaVenta =
            new SimpleStringProperty();

    public Venta() {
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

    public final String getFechaVenta() {
        return fechaVenta.get();
    }

    public final void setFechaVenta(String value) {
        fechaVenta.set(value);
    }

    public final StringProperty fechaVentaProperty() {
        return fechaVenta;
    }
}