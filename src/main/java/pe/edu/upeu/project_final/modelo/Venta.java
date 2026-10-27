package pe.edu.upeu.project_final.modelo;

import javafx.beans.property.*;

public class Venta {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty idFuncion = new SimpleIntegerProperty();
    private final StringProperty cliente = new SimpleStringProperty();
    private final IntegerProperty cantidadAsientos = new SimpleIntegerProperty();
    private final DoubleProperty totalPagar = new SimpleDoubleProperty();
    private final StringProperty fechaVenta = new SimpleStringProperty();
    public Venta() {
    }

    public Venta(int id, int idFuncion, String cliente, int cantidadAsientos, double totalPagar, String fechaVenta) {
        setId(id);
        setIdFuncion(idFuncion);
        setCliente(cliente);
        setCantidadAsientos(cantidadAsientos);
        setTotalPagar(totalPagar);
        setFechaVenta(fechaVenta);
    }
    public final int getId() {
        return id.get();
    }

    public final void setId(int value) {
        id.set(value);
    }

    public final IntegerProperty idProperty() {
        return id;
    }


    public final int getIdFuncion() {
        return idFuncion.get();
    }

    public final void setIdFuncion(int value) {
        idFuncion.set(value);
    }

    public final IntegerProperty idFuncionProperty() {
        return idFuncion;
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