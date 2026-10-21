// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.modelo;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import javafx.beans.property.*;

/**
 * MODELO: representa una sala: nombre, tipo y capacidad.
 * Guarda datos en memoria; no contiene consultas SQL ni abre ventanas.
 * Las propiedades JavaFX permiten que la interfaz observe sus valores.
 */
public class Sala {

    // Propiedad observable de un entero (int). final fija la referencia; el valor sí puede cambiar con set().
    private final IntegerProperty idsala =
            new SimpleIntegerProperty();

    // Propiedad observable de un texto (String).
    private final StringProperty nombre =
            new SimpleStringProperty();

    // Propiedad observable de un texto (String).
    private final StringProperty tipo =
            new SimpleStringProperty();

    // Propiedad observable de un entero (int). final fija la referencia; el valor sí puede cambiar con set().
    private final IntegerProperty capacidad =
            new SimpleIntegerProperty();

    /**
     * Constructor: permite crear un objeto de esta clase.
     * Si recibe parámetros, asigna los datos iniciales mediante los setters.
     */
    public Sala() {
    }
    /**
     * Constructor: permite crear un objeto de esta clase.
     * Si recibe parámetros, asigna los datos iniciales mediante los setters.
     */
    public Sala(int idsala, String nombre, String tipo, int capacidad){
        setIdsala(idsala);
        setNombre(nombre);
        setTipo(tipo);
        setCapacidad(capacidad);
    }

    /**
     * Getter: lee el identificador de la sala guardado en la propiedad.
     */
    public final int getIdsala() {
        return idsala.get();
    }

    /**
     * Setter: asigna el identificador de la sala a la propiedad.
     */
    public final void setIdsala(int value) {
        idsala.set(value);
    }

    /**
     * Devuelve la propiedad observable del identificador de la sala.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final IntegerProperty idsalaProperty() {
        return idsala;
    }

    /**
     * Getter: lee el nombre de la sala guardado en la propiedad.
     */
    public final String getNombre() {
        return nombre.get();
    }

    /**
     * Setter: asigna el nombre de la sala a la propiedad.
     */
    public final void setNombre(String value) {
        nombre.set(value);
    }

    /**
     * Devuelve la propiedad observable del nombre de la sala.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final StringProperty nombreProperty() {
        return nombre;
    }

    /**
     * Getter: lee el tipo de sala guardado en la propiedad.
     */
    public final String getTipo() {
        return tipo.get();
    }

    /**
     * Setter: asigna el tipo de sala a la propiedad.
     */
    public final void setTipo(String value) {
        tipo.set(value);
    }

    /**
     * Devuelve la propiedad observable del tipo de sala.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final StringProperty tipoProperty() {
        return tipo;
    }

    /**
     * Getter: lee el capacidad de la sala guardado en la propiedad.
     */
    public final int getCapacidad() {
        return capacidad.get();
    }

    /**
     * Setter: asigna el capacidad de la sala a la propiedad.
     */
    public final void setCapacidad(int value) {
        capacidad.set(value);
    }

    /**
     * Devuelve la propiedad observable del capacidad de la sala.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final IntegerProperty capacidadProperty() {
        return capacidad;
    }
}
