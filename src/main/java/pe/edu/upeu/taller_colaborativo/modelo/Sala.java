package pe.edu.upeu.taller_colaborativo.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Sala {

    private final IntegerProperty idSala = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final IntegerProperty filas = new SimpleIntegerProperty();
    private final IntegerProperty columnas = new SimpleIntegerProperty();
    private final StringProperty tipo = new SimpleStringProperty();
    private final StringProperty estado = new SimpleStringProperty();

    public Sala() {
    }

    public Sala(int idSala, String nombre, int filas, int columnas, String tipo, String estado) {
        setIdSala(idSala);
        setNombre(nombre);
        setFilas(filas);
        setColumnas(columnas);
        setTipo(tipo);
        setEstado(estado);
    }

    public final int getIdSala() {
        return idSala.get();
    }

    public final void setIdSala(int value) {
        idSala.set(value);
    }

    public final IntegerProperty idSalaProperty() {
        return idSala;
    }

    public final String getNombre() {
        return nombre.get();
    }

    public final void setNombre(String value) {
        nombre.set(value);
    }

    public final StringProperty nombreProperty() {
        return nombre;
    }

    // Aforo de la sala: todas sus butacas (filas x columnas)
    public int getAforo() {
        return getFilas() * getColumnas();
    }

    public final int getFilas() {
        return filas.get();
    }

    public final void setFilas(int value) {
        filas.set(value);
    }

    public final IntegerProperty filasProperty() {
        return filas;
    }

    public final int getColumnas() {
        return columnas.get();
    }

    public final void setColumnas(int value) {
        columnas.set(value);
    }

    public final IntegerProperty columnasProperty() {
        return columnas;
    }

    public final String getTipo() {
        return tipo.get();
    }

    public final void setTipo(String value) {
        tipo.set(value);
    }

    public final StringProperty tipoProperty() {
        return tipo;
    }

    public final String getEstado() {
        return estado.get();
    }

    public final void setEstado(String value) {
        estado.set(value);
    }

    public final StringProperty estadoProperty() {
        return estado;
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
