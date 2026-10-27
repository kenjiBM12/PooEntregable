package pe.edu.upeu.project_final.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pelicula {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty titulo = new SimpleStringProperty();
    private final StringProperty genero = new SimpleStringProperty();
    private final IntegerProperty duracionMin = new SimpleIntegerProperty();
    private final StringProperty clasificacion = new SimpleStringProperty();

    public Pelicula() {
    }

    public Pelicula(int id, String titulo, String genero, int duracionMin, String clasificacion) {
        setId(id);
        setTitulo(titulo);
        setGenero(genero);
        setDuracionMin(duracionMin);
        setClasificacion(clasificacion);
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


    public final String getTitulo() {
        return titulo.get();
    }

    public final void setTitulo(String value) {
        titulo.set(value);
    }

    public final StringProperty tituloProperty() {
        return titulo;
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


    public final int getDuracionMin() {
        return duracionMin.get();
    }

    public final void setDuracionMin(int value) {
        duracionMin.set(value);
    }

    public final IntegerProperty duracionMinProperty() {
        return duracionMin;
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