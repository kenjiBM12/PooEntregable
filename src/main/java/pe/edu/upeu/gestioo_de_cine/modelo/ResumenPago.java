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

public class ResumenPago {

    private final StringProperty metodo = new SimpleStringProperty();
    private final IntegerProperty boletos = new SimpleIntegerProperty();
    private final DoubleProperty monto = new SimpleDoubleProperty();
    private final DoubleProperty devuelto = new SimpleDoubleProperty();

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

    public final double getNeto() {
        return neto.get();
    }

    public final ObservableValue<Double> netoProperty() {
        return neto.asObject();
    }
}
