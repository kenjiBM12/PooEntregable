package com.example.proyecto_final.modelo;

import javafx.beans.property.*;

public class Pelicula {

    private final IntegerProperty idpelicula =
            new SimpleIntegerProperty();

    private final StringProperty titulo =
            new SimpleStringProperty();

    private final IntegerProperty duracion =
            new SimpleIntegerProperty();

    private final StringProperty genero =
            new SimpleStringProperty();

    private final StringProperty clasificacion =
            new SimpleStringProperty();

    public Pelicula() {
    }

    public Pelicula(int idpelicula, String titulo, int duracion,
                    String genero, String clasificacion){
        setIdpelicula(idpelicula);
        setTitulo(titulo);
        setDuracion(duracion);
        setGenero(genero);
        setClasificacion(clasificacion);

    }

    public final int getIdpelicula() {
        return idpelicula.get();
    }

    public final void setIdpelicula(int value) {
        idpelicula.set(value);
    }

    public final IntegerProperty idpeliculaProperty() {
        return idpelicula;
    }

    public final String getTitulo() {
        return titulo.get();
    }

    public final void setTitulo(String value) {
        titulo.set(value);
    }

    public final StringProperty tituloProperty() {
        return titulo;
    }

    public final int getDuracion() {
        return duracion.get();
    }

    public final void setDuracion(int value) {
        duracion.set(value);
    }

    public final IntegerProperty duracionProperty() {
        return duracion;
    }

    public final String getGenero() {
        return genero.get();
    }

    public final void setGenero(String value) {
        genero.set(value);
    }

    public final StringProperty generoProperty() {
        return genero;
    }

    public final String getClasificacion() {
        return clasificacion.get();
    }

    public final void setClasificacion(String value) {
        clasificacion.set(value);
    }

    public final StringProperty clasificacionProperty() {
        return clasificacion;
    }
}
