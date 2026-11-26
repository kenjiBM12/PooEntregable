package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Reporte {

    private final StringProperty desde = new SimpleStringProperty();
    private final StringProperty hasta = new SimpleStringProperty();

    private final IntegerProperty boletosActivos = new SimpleIntegerProperty();
    private final IntegerProperty boletosAnulados = new SimpleIntegerProperty();

    private final DoubleProperty ingresos = new SimpleDoubleProperty();         private final DoubleProperty montoAnulado = new SimpleDoubleProperty();
    private final List<ResumenPago> porMetodoPago = new ArrayList<>();

    public Reporte() {
    }

    public final String getDesde() {
        return desde.get();
    }

    public final void setDesde(String value) {
        desde.set(value);
    }

    public final StringProperty desdeProperty() {
        return desde;
    }

    public final String getHasta() {
        return hasta.get();
    }

    public final void setHasta(String value) {
        hasta.set(value);
    }

    public final StringProperty hastaProperty() {
        return hasta;
    }

    public final int getBoletosActivos() {
        return boletosActivos.get();
    }

    public final void setBoletosActivos(int value) {
        boletosActivos.set(value);
    }

    public final IntegerProperty boletosActivosProperty() {
        return boletosActivos;
    }

    public final int getBoletosAnulados() {
        return boletosAnulados.get();
    }

    public final void setBoletosAnulados(int value) {
        boletosAnulados.set(value);
    }

    public final IntegerProperty boletosAnuladosProperty() {
        return boletosAnulados;
    }

    public final double getIngresos() {
        return ingresos.get();
    }

    public final void setIngresos(double value) {
        ingresos.set(value);
    }

    public final DoubleProperty ingresosProperty() {
        return ingresos;
    }

    public final double getMontoAnulado() {
        return montoAnulado.get();
    }

    public final void setMontoAnulado(double value) {
        montoAnulado.set(value);
    }

    public final DoubleProperty montoAnuladoProperty() {
        return montoAnulado;
    }

    public final double getTotalNeto() {
        return getIngresos();
    }

    public List<ResumenPago> getPorMetodoPago() {
        return porMetodoPago;
    }

    public void setPorMetodoPago(List<ResumenPago> lista) {
        porMetodoPago.clear();
        if (lista != null) {
            porMetodoPago.addAll(lista);
        }
    }
}
