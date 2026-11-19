package pe.edu.upeu.taller_colaborativo.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Venta {

    private final IntegerProperty idVenta = new SimpleIntegerProperty();
    private final ObjectProperty<Funcion> funcion = new SimpleObjectProperty<>();
    private final StringProperty butacas = new SimpleStringProperty();
    private final DoubleProperty total = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();

    public Venta() {
    }

    public Venta(int idVenta, Funcion funcion, String butacas, double total, LocalDate fecha) {
        setIdVenta(idVenta);
        setFuncion(funcion);
        setButacas(butacas);
        setTotal(total);
        setFecha(fecha);
    }

    public final int getIdVenta() {
        return idVenta.get();
    }

    public final void setIdVenta(int value) {
        idVenta.set(value);
    }

    public final IntegerProperty idVentaProperty() {
        return idVenta;
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

    public final String getButacas() {
        return butacas.get();
    }

    public final void setButacas(String value) {
        butacas.set(value);
    }

    public final StringProperty butacasProperty() {
        return butacas;
    }

    public final double getTotal() {
        return total.get();
    }

    public final void setTotal(double value) {
        total.set(value);
    }

    public final DoubleProperty totalProperty() {
        return total;
    }

    public final LocalDate getFecha() {
        return fecha.get();
    }

    public final void setFecha(LocalDate value) {
        fecha.set(value);
    }

    public final ObjectProperty<LocalDate> fechaProperty() {
        return fecha;
    }

    // Las butacas se guardan separadas por coma, por ejemplo "A5, A6"
    public int getCantidadButacas() {
        return getButacas().isEmpty() ? 0 : getButacas().split(", ").length;
    }
}
