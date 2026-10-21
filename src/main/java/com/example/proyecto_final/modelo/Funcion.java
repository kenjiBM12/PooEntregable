// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.modelo;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import javafx.beans.property.*;

/**
 * MODELO: representa una proyección: película, sala, horario y precio.
 * Guarda datos en memoria; no contiene consultas SQL ni abre ventanas.
 * Las propiedades JavaFX permiten que la interfaz observe sus valores.
 */
public class Funcion {

    // Propiedad observable de un entero (int). final fija la referencia; el valor sí puede cambiar con set().
    private final IntegerProperty idfuncion =
            new SimpleIntegerProperty();

    // Asociación a otro modelo: se guarda el objeto; el DAO utiliza su ID al escribir SQL.
    private final ObjectProperty<Pelicula> pelicula =
            new SimpleObjectProperty<>();

    // Asociación a otro modelo: se guarda el objeto; el DAO utiliza su ID al escribir SQL.
    private final ObjectProperty<Sala> sala =
            new SimpleObjectProperty<>();

    // Propiedad observable de un texto (String).
    private final StringProperty horario =
            new SimpleStringProperty();

    // Propiedad observable de un número decimal (double).
    private final DoubleProperty precio =
            new SimpleDoubleProperty();

    /**
     * Constructor: permite crear un objeto de esta clase.
     * Si recibe parámetros, asigna los datos iniciales mediante los setters.
     */
    public Funcion() {
    }
/**
 * Constructor: permite crear un objeto de esta clase.
 * Si recibe parámetros, asigna los datos iniciales mediante los setters.
 */
public Funcion(int idfuncion, Pelicula pelicula, Sala sala,
               String horario, double precio){
        setIdfuncion(idfuncion);
        setPelicula(pelicula);
        setSala(sala);
        setHorario(horario);
        setPrecio(precio);
}
    /**
     * Getter: lee el identificador de la función guardado en la propiedad.
     */
    public final int getIdfuncion() {
        return idfuncion.get();
    }

    /**
     * Setter: asigna el identificador de la función a la propiedad.
     */
    public final void setIdfuncion(int value) {
        idfuncion.set(value);
    }

    /**
     * Devuelve la propiedad observable del identificador de la función.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final IntegerProperty idfuncionProperty() {
        return idfuncion;
    }

    /**
     * Getter: lee el película asociada guardado en la propiedad.
     */
    public final Pelicula getPelicula() {
        return pelicula.get();
    }

    /**
     * Setter: asigna el película asociada a la propiedad.
     */
    public final void setPelicula(Pelicula value) {
        pelicula.set(value);
    }

    /**
     * Devuelve la propiedad observable del película asociada.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final ObjectProperty<Pelicula> peliculaProperty() {
        return pelicula;
    }

    /**
     * Getter: lee el sala asociada guardado en la propiedad.
     */
    public final Sala getSala() {
        return sala.get();
    }

    /**
     * Setter: asigna el sala asociada a la propiedad.
     */
    public final void setSala(Sala value) {
        sala.set(value);
    }

    /**
     * Devuelve la propiedad observable del sala asociada.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final ObjectProperty<Sala> salaProperty() {
        return sala;
    }

    /**
     * Getter: lee el horario de la función guardado en la propiedad.
     */
    public final String getHorario() {
        return horario.get();
    }

    /**
     * Setter: asigna el horario de la función a la propiedad.
     */
    public final void setHorario(String value) {
        horario.set(value);
    }

    /**
     * Devuelve la propiedad observable del horario de la función.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final StringProperty horarioProperty() {
        return horario;
    }

    /**
     * Getter: lee el precio por entrada guardado en la propiedad.
     */
    public final double getPrecio() {
        return precio.get();
    }

    /**
     * Setter: asigna el precio por entrada a la propiedad.
     */
    public final void setPrecio(double value) {
        precio.set(value);
    }

    /**
     * Devuelve la propiedad observable del precio por entrada.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final DoubleProperty precioProperty() {
        return precio;
    }
}
