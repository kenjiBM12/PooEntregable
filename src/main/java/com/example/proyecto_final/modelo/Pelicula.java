// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.modelo;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import javafx.beans.property.*;

/**
 * MODELO: representa una película: título, género, duración y clasificación.
 * Guarda datos en memoria; no contiene consultas SQL ni abre ventanas.
 * Las propiedades JavaFX permiten que la interfaz observe sus valores.
 */
public class Pelicula {

    // Propiedad observable de un entero (int). final fija la referencia; el valor sí puede cambiar con set().
    private final IntegerProperty idpelicula =
            new SimpleIntegerProperty();

    // Propiedad observable de un texto (String).
    private final StringProperty titulo =
            new SimpleStringProperty();

    // Propiedad observable de un entero (int). final fija la referencia; el valor sí puede cambiar con set().
    private final IntegerProperty duracion =
            new SimpleIntegerProperty();

    // Propiedad observable de un texto (String).
    private final StringProperty genero =
            new SimpleStringProperty();

    // Propiedad observable de un texto (String).
    private final StringProperty clasificacion =
            new SimpleStringProperty();

    /**
     * Constructor: permite crear un objeto de esta clase.
     * Si recibe parámetros, asigna los datos iniciales mediante los setters.
     */
    public Pelicula() {
    }

    /**
     * Constructor: permite crear un objeto de esta clase.
     * Si recibe parámetros, asigna los datos iniciales mediante los setters.
     */
    public Pelicula(int idpelicula, String titulo, int duracion,
                    String genero, String clasificacion){
        setIdpelicula(idpelicula);
        setTitulo(titulo);
        setDuracion(duracion);
        setGenero(genero);
        setClasificacion(clasificacion);

    }

    /**
     * Getter: lee el identificador de la película guardado en la propiedad.
     */
    public final int getIdpelicula() {
        return idpelicula.get();
    }

    /**
     * Setter: asigna el identificador de la película a la propiedad.
     */
    public final void setIdpelicula(int value) {
        idpelicula.set(value);
    }

    /**
     * Devuelve la propiedad observable del identificador de la película.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final IntegerProperty idpeliculaProperty() {
        return idpelicula;
    }

    /**
     * Getter: lee el título de la película guardado en la propiedad.
     */
    public final String getTitulo() {
        return titulo.get();
    }

    /**
     * Setter: asigna el título de la película a la propiedad.
     */
    public final void setTitulo(String value) {
        titulo.set(value);
    }

    /**
     * Devuelve la propiedad observable del título de la película.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final StringProperty tituloProperty() {
        return titulo;
    }

    /**
     * Getter: lee el duración en minutos guardado en la propiedad.
     */
    public final int getDuracion() {
        return duracion.get();
    }

    /**
     * Setter: asigna el duración en minutos a la propiedad.
     */
    public final void setDuracion(int value) {
        duracion.set(value);
    }

    /**
     * Devuelve la propiedad observable del duración en minutos.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final IntegerProperty duracionProperty() {
        return duracion;
    }

    /**
     * Getter: lee el género de la película guardado en la propiedad.
     */
    public final String getGenero() {
        return genero.get();
    }

    /**
     * Setter: asigna el género de la película a la propiedad.
     */
    public final void setGenero(String value) {
        genero.set(value);
    }

    /**
     * Devuelve la propiedad observable del género de la película.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final StringProperty generoProperty() {
        return genero;
    }

    /**
     * Getter: lee el clasificación de la película guardado en la propiedad.
     */
    public final String getClasificacion() {
        return clasificacion.get();
    }

    /**
     * Setter: asigna el clasificación de la película a la propiedad.
     */
    public final void setClasificacion(String value) {
        clasificacion.set(value);
    }

    /**
     * Devuelve la propiedad observable del clasificación de la película.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final StringProperty clasificacionProperty() {
        return clasificacion;
    }
}
