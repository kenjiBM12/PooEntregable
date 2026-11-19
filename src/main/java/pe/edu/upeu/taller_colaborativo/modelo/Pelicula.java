package pe.edu.upeu.taller_colaborativo.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pelicula {

    private final IntegerProperty idPelicula = new SimpleIntegerProperty();
    private final StringProperty titulo = new SimpleStringProperty();
    private final StringProperty genero = new SimpleStringProperty();
    private final IntegerProperty duracion = new SimpleIntegerProperty();
    private final StringProperty clasificacion = new SimpleStringProperty();
    private final StringProperty estado = new SimpleStringProperty();
    private final StringProperty poster = new SimpleStringProperty();

    public Pelicula() {
    }

    public Pelicula(int idPelicula, String titulo, String genero, int duracion,
                    String clasificacion, String estado, String poster) {
        setIdPelicula(idPelicula);
        setTitulo(titulo);
        setGenero(genero);
        setDuracion(duracion);
        setClasificacion(clasificacion);
        setEstado(estado);
        setPoster(poster);
    }

    public final int getIdPelicula() {
        return idPelicula.get();
    }

    public final void setIdPelicula(int value) {
        idPelicula.set(value);
    }

    public final IntegerProperty idPeliculaProperty() {
        return idPelicula;
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

    public final int getDuracion() {
        return duracion.get();
    }

    public final void setDuracion(int value) {
        duracion.set(value);
    }

    public final IntegerProperty duracionProperty() {
        return duracion;
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

    public final String getEstado() {
        return estado.get();
    }

    public final void setEstado(String value) {
        estado.set(value);
    }

    public final StringProperty estadoProperty() {
        return estado;
    }

    // Archivo del poster dentro de resources/img (si es null se usa el de por defecto)
    public final String getPoster() {
        return poster.get();
    }

    public final void setPoster(String value) {
        poster.set(value);
    }

    public final StringProperty posterProperty() {
        return poster;
    }

    @Override
    public String toString() {
        return getTitulo();
    }
}
