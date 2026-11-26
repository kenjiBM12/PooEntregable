package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * MEJORA 3: modelo del reporte de cierre de caja.
 *
 * Esta clase guarda los totales que calcula el ReporteDaoImpl con SQL, y el
 * desglose por forma de pago. Los totales son propiedades de JavaFX, igual
 * que en los demas modelos; la lista de pagos se maneja como ArrayList.
 *
 * Los boletos "activos" son los que cuentan como venta; los "anulados" ya
 * no cuentan como ingreso, pero se informan aparte para el cierre de caja.
 */
public class Reporte {

    //rango de fechas consultado (texto yyyy-MM-dd, solo para mostrarlo)
    private final StringProperty desde = new SimpleStringProperty();
    private final StringProperty hasta = new SimpleStringProperty();

    //cantidad de boletos
    private final IntegerProperty boletosActivos = new SimpleIntegerProperty();
    private final IntegerProperty boletosAnulados = new SimpleIntegerProperty();

    //dinero
    private final DoubleProperty ingresos = new SimpleDoubleProperty();     //suma de boletos activos
    private final DoubleProperty montoAnulado = new SimpleDoubleProperty(); //suma de boletos anulados

    //MEJORA 2: desglose por forma de pago (EFECTIVO / TARJETA / YAPE...)
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

    //el total neto es simplemente lo que realmente ingreso a caja
    public final double getTotalNeto() {
        return getIngresos();
    }

    //MEJORA 2: lista con un resumen por cada forma de pago del rango
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
