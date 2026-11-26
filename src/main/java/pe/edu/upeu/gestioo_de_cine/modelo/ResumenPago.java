package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 * MEJORA 2: fila del cierre de caja por forma de pago.
 *
 * El reporte usa un objeto de esta clase por cada forma de pago encontrada
 * en el rango de fechas (EFECTIVO, TARJETA, YAPE...). Asi el administrador
 * sabe cuanto dinero entro por cada medio y puede cuadrar la caja.
 *
 * Usa propiedades de JavaFX como los demas modelos. El neto es una propiedad
 * calculada: se actualiza solo cuando cambian el cobrado o el devuelto.
 */
public class ResumenPago {

    private final StringProperty metodo = new SimpleStringProperty();
    private final IntegerProperty boletos = new SimpleIntegerProperty();
    private final DoubleProperty monto = new SimpleDoubleProperty();
    //MEJORA 1: dinero devuelto por anulaciones en esa forma de pago.
    //Asi el cierre de caja muestra cobrado, devuelto y neto por medio.
    private final DoubleProperty devuelto = new SimpleDoubleProperty();

    //cobrado - devuelto: es lo que realmente queda en caja de ese medio
    private final DoubleBinding neto = Bindings.createDoubleBinding(
            () -> getMonto() - getDevuelto(), monto, devuelto);

    public ResumenPago() {
    }

    public ResumenPago(String metodo, int boletos, double monto) {
        setMetodo(metodo);
        setBoletos(boletos);
        setMonto(monto);
    }

    public final String getMetodo() {
        return metodo.get();
    }

    public final void setMetodo(String value) {
        metodo.set(value);
    }

    public final StringProperty metodoProperty() {
        return metodo;
    }

    public final int getBoletos() {
        return boletos.get();
    }

    public final void setBoletos(int value) {
        boletos.set(value);
    }

    public final IntegerProperty boletosProperty() {
        return boletos;
    }

    public final double getMonto() {
        return monto.get();
    }

    public final void setMonto(double value) {
        monto.set(value);
    }

    public final DoubleProperty montoProperty() {
        return monto;
    }

    public final double getDevuelto() {
        return devuelto.get();
    }

    public final void setDevuelto(double value) {
        devuelto.set(value);
    }

    public final DoubleProperty devueltoProperty() {
        return devuelto;
    }

    /** Lo que queda en caja de ese medio: cobrado menos devuelto. */
    public final double getNeto() {
        return neto.get();
    }

    /** Version observable del neto, lista para una columna de TableView. */
    public final ObservableValue<Double> netoProperty() {
        return neto.asObject();
    }
}
