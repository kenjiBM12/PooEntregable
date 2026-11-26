package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * MEJORA 10: numeros rapidos de la pantalla de Inicio.
 *
 * Se llenan con UNA sola consulta SQL (MetricaDaoImpl) para no abrir varias
 * conexiones al entrar al sistema. Aunque son datos de solo lectura, usan
 * propiedades de JavaFX para seguir el mismo patron que el resto de modelos.
 */
public class Metrica {

    private final IntegerProperty peliculas = new SimpleIntegerProperty();
    private final IntegerProperty salas = new SimpleIntegerProperty();
    private final IntegerProperty salasActivas = new SimpleIntegerProperty();
    private final IntegerProperty clientes = new SimpleIntegerProperty();
    private final IntegerProperty funcionesHoy = new SimpleIntegerProperty();
    private final IntegerProperty boletosHoy = new SimpleIntegerProperty();
    private final DoubleProperty ingresosHoy = new SimpleDoubleProperty();

    public Metrica() {
    }

    public final int getPeliculas() {
        return peliculas.get();
    }

    public final void setPeliculas(int value) {
        peliculas.set(value);
    }

    public final IntegerProperty peliculasProperty() {
        return peliculas;
    }

    public final int getSalas() {
        return salas.get();
    }

    public final void setSalas(int value) {
        salas.set(value);
    }

    public final IntegerProperty salasProperty() {
        return salas;
    }

    public final int getSalasActivas() {
        return salasActivas.get();
    }

    public final void setSalasActivas(int value) {
        salasActivas.set(value);
    }

    public final IntegerProperty salasActivasProperty() {
        return salasActivas;
    }

    public final int getClientes() {
        return clientes.get();
    }

    public final void setClientes(int value) {
        clientes.set(value);
    }

    public final IntegerProperty clientesProperty() {
        return clientes;
    }

    public final int getFuncionesHoy() {
        return funcionesHoy.get();
    }

    public final void setFuncionesHoy(int value) {
        funcionesHoy.set(value);
    }

    public final IntegerProperty funcionesHoyProperty() {
        return funcionesHoy;
    }

    public final int getBoletosHoy() {
        return boletosHoy.get();
    }

    public final void setBoletosHoy(int value) {
        boletosHoy.set(value);
    }

    public final IntegerProperty boletosHoyProperty() {
        return boletosHoy;
    }

    public final double getIngresosHoy() {
        return ingresosHoy.get();
    }

    public final void setIngresosHoy(double value) {
        ingresosHoy.set(value);
    }

    public final DoubleProperty ingresosHoyProperty() {
        return ingresosHoy;
    }
}
