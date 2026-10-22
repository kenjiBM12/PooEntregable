package com.example.proyecto_final.modelo;

import javafx.beans.property.*;

public class Sala {

    private final IntegerProperty idsala =
            new SimpleIntegerProperty();

    private final StringProperty nombre =
            new SimpleStringProperty();

    private final StringProperty tipo =
            new SimpleStringProperty();

    private final IntegerProperty capacidad =
            new SimpleIntegerProperty();

    public Sala() {
    }

    public Sala(int idsala, String nombre, String tipo, int capacidad){
        setIdsala(idsala);
        setNombre(nombre);
        setTipo(tipo);
        setCapacidad(capacidad);
    }

    public final int getIdsala() {
        return idsala.get();
    }

    public final void setIdsala(int value) {
        idsala.set(value);
    }

    public final IntegerProperty idsalaProperty() {
        return idsala;
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

    public final String getTipo() {
        return tipo.get();
    }

    public final void setTipo(String value) {
        tipo.set(value);
    }

    public final StringProperty tipoProperty() {
        return tipo;
    }

    public final int getCapacidad() {
        return capacidad.get();
    }

    public final void setCapacidad(int value) {
        capacidad.set(value);
    }

    public final IntegerProperty capacidadProperty() {
        return capacidad;
    }
}
